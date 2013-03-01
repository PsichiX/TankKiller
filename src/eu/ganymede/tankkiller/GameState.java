package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.XeSense;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeApplication.*;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;

import java.util.LinkedList;
import java.util.Random;

public class GameState extends State implements CommandQueue.Delegate
{
	private Scene _scn;
	private Camera2D _cam;
	private Scene _scnHud;
	private Camera2D _camHud;
	private ActorsManager _actors = new ActorsManager(this);
	private CollisionManager _colls = new CollisionManager();
	private TurnManager _turns = new TurnManager(_actors);
	private PlayerInfo[] _players;
	private CommandQueue _cmds = new CommandQueue();
	private PopupsManager _popups;
	private boolean _paused = false;
	private TileMap _map;
	private Flag _flag;
	private final int _cols = 50;
	private final int _rows = 50;
	private final float _width = _cols * 100.0f;
	private final float _height = _rows * 100.0f;
	private int[] _area = new int[]{ -1, -1 };
	private int[] _padding = new int[]{ 5, 5 };
	private float[] _startLoc;
	private float _camTargetX = -1.0f;
	private float _camTargetY = -1.0f;
	private float _camSpeed = 5000.0f;
	private Text _turnTimerText;
	private Text _turnNameText;
	private Sprite _turnNextBtn;
	private int _turnLastTime = -1;
	private Font _font;
	private Material _fontMaterial;
	
	public GameState(PlayerInfo[] players)
	{
		super();
		_players = players;
		_cmds.setDelegate(this);
		_turns.setReceiver(_cmds);
	}
	
	@Override
	public void onEnter()
	{
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.0f, 0.0f, 0.0f, 1.0f);
		
		//getApplication().getAssets().get(R.raw.camera, Camera2D.class);
		//Message.alert(getApplication().getContext(), "error", getApplication().getAssets().getLastError(), "ok", null);
		
		_scn = (Scene)getApplication().getAssets().get(R.raw.scene, Scene.class);
		_cam = (Camera2D)_scn.getCamera();
		_cam.setViewPosition(_width * 0.5f, _height * 0.5f);
		_scnHud = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_camHud = (Camera2D)_scnHud.getCamera();
		_camHud.setViewPosition(
			_camHud.getViewWidth() * 0.5f,
			_camHud.getViewHeight() * 0.5f
			);
		_popups = new PopupsManager(_scnHud);
		
		TileMapGenerator map = (TileMapGenerator)getApplication().getAssets().get(R.raw.map, TileMapGenerator.class);
		String[] md = map.createMap("terrain", _cols, _rows, null);
		for(int i = 0; i < md.length; i++)
			md[i] = "0";
		Random r = new Random(System.currentTimeMillis());
		for(int i = 0; i < 10; i++)
			applyCircle(md, _cols, _rows, Math.abs(r.nextInt()) % (_cols - 1), Math.abs(r.nextInt()) % (_rows - 1), 5 + Math.abs(r.nextInt()) % 15, "1");
		for(int i = 0; i < 10; i++)
			applyCircle(md, _cols, _rows, Math.abs(r.nextInt()) % (_cols - 1), Math.abs(r.nextInt()) % (_rows - 1), 1 + Math.abs(r.nextInt()) % 7, "0");
		_map = new TileMap();
		map.applyPatterns("terrain");
		map.buildCompatibleTileMap("terrain", _map, _width, _height, 1.0f, 1.0f);
		map.applyTiles("terrain", _map);
		_scn.attach(_map);
		
		for(PlayerInfo player : _players)
		{
			Base base = BaseFactory.getFactory().createBase(player.color);
			Tank tank = TankFactory.getFactory().createTank(player.color, _width, _height);
			tank.setRange(tank.getHeight() * 0.5f);
			base.setRange(base.getHeight() * 0.5f);
			tank.setReceiver(_cmds);
			_scn.attach(base);
			_scn.attach(tank);
			_actors.attach(tank);
			_colls.attach(tank);
			_colls.attach(base);
			_turns.addPlayer(tank);
			
			base.setPosition(tank.getPositionX(), tank.getPositionY());
		}
		
		Material mat = (Material)MainActivity.app.getAssets().get(R.raw.flag_material, Material.class);
		Image img = (Image)MainActivity.app.getAssets().get(R.drawable.flaga, Image.class);
		
		_flag = new Flag(mat, _width * 0.5f, _height * 0.5f);
		_flag.setSizeFromImage(img);
		_flag.setOffsetFromSize(0.5f, 0.5f);
		_flag.setRange(100);
		_colls.attach(_flag);
		_actors.attach(_flag);
		_scn.attach(_flag);
		
		_font = (Font)getApplication().getAssets().get(R.raw.badaboom_font, Font.class);
		_fontMaterial = (Material)getApplication().getAssets().get(R.raw.badaboom_material, Material.class);
		
		_turnNameText = new Text();
		_turnNameText.setPosition(_camHud.getViewWidth(), 0.0f);
		_scnHud.attach(_turnNameText);
		setPlayerText("");
		_turnTimerText = new Text();
		_turnTimerText.setPosition(_camHud.getViewWidth(), _camHud.getViewHeight());
		_scnHud.attach(_turnTimerText);
		setTimerText(0);
		mat = (Material)getApplication().getAssets().get(R.raw.turn_next_btn_material, Material.class);
		img = (Image)getApplication().getAssets().get(R.drawable.btn_next_turn, Image.class);
		_turnNextBtn = new Sprite(mat);
		_turnNextBtn.setSizeFromImage(img, 0.75f);
		_turnNextBtn.setOffsetFromSize(0.0f, 1.0f);
		_turnNextBtn.setPosition(0.0f, _camHud.getViewHeight());
		_scnHud.attach(_turnNextBtn);
		
		_popups.push(new StartPopup(getApplication().getAssets(), _cmds));
	}
	
	@Override
	public void onExit()
	{
		_scn.releaseAll();
		_scnHud.releaseAll();
		_actors.detachAll();
		_colls.detachAll();
		_turns.clearPlayers();
		_popups.release();
		_popups = null;
		getApplication().getPhoton().unregisterDrawCalls();
	}
	
	@Override
	public void onInput(Touches ev)
	{
		if(_popups.onInput(ev))
		{
			_startLoc = null;
			return;
		}
		Touch t = ev.getTouchByState(Touch.State.UP);
		if(t != null)
		{
			_startLoc = null;
			float[] locHud = _scnHud.getCamera().convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			if(Utils.hitTest(_turnNextBtn, locHud[0], locHud[1]))
			{
				_turns.nextPlayer();
				_turns.resetTimer();
				return;
			}
		}
		_actors.onInput(ev);
		t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			_startLoc = _scn.getCamera().convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
		}
		t = ev.getTouchByState(Touch.State.IDLE);
		if(!_paused && t != null && _startLoc != null)
		{
			float[] loc = _scn.getCamera().convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			loc[0] -= _startLoc[0];
			loc[1] -= _startLoc[1];
			float w = _cam.getViewWidth() * 0.5f;
			float h = _cam.getViewHeight() * 0.5f;
			_cam.setViewPosition(
				Math.max(w, Math.min(_width - w, _cam.getViewPositionX() - loc[0])),
				Math.max(h, Math.min(_height - h, _cam.getViewPositionY() - loc[1]))
				);
			_camTargetX = -1.0f;
			_camTargetY = -1.0f;
		}
	}
	
	@Override
	public void onUpdate()
	{
		//float dt = getApplication().getTimer().getDeltaTime() / 1000.0f;
		float dt = 1.0f / 30.0f;
		
		_cmds.run();
		_paused = _popups.isShowing();
		if(!_paused)
		{
			_turns.update(dt);
			setTimerText(1 + (int)_turns.getTimeLeft());
			// move camera to target point
			if(_camTargetX >= 0.0f && _camTargetY >= 0.0f)
			{
				float dx = _camTargetX - _cam.getViewPositionX();
				float dy = _camTargetY - _cam.getViewPositionY();
				float len = MathHelper.vecLength(dx, dy, 0.0f);
				if(len > _camSpeed * dt)
					_cam.setViewPosition(_cam.getViewPositionX() + ((dx / len) * _camSpeed * dt), _cam.getViewPositionY() + ((dy / len) * _camSpeed * dt));
				else
				{
					_cam.setViewPosition(_camTargetX, _camTargetY);
					_camTargetX = -1.0f;
					_camTargetY = -1.0f;
				}
			}
			_actors.onUpdate(dt);
			_colls.test();
			int[] tloc = _map.convertLocationWorldToTile(
				_cam.getViewPositionX(),
				_cam.getViewPositionY()
				);
			if(	Math.abs(tloc[0] - _area[0]) > _padding[0] ||
				Math.abs(tloc[1] - _area[1]) > _padding[1] )
			{
				int cols = (int)(_cam.getViewWidth() / _map.getCellWidth()) + (2 * _padding[0]) + 2;
				int rows = (int)(_cam.getViewHeight() / _map.getCellHeight()) + (2 * _padding[1]) + 2;
				_map.setVisibleRegion(tloc[0] - cols / 2, tloc[1] - rows / 2, tloc[0] + cols / 2, tloc[1] + rows / 2, null);
				_area[0] = tloc[0];
				_area[1] = tloc[1];
			}
		}
		_scn.update(dt);
		_scnHud.update(dt);
	}
	
	public void onCommand(Object sender, String cmd, Object data)
	{
		if(cmd.equals("NextPlayer") && data instanceof Tank)
		{
			_startLoc = null;
			Tank tank = (Tank)data;
			setPlayerText(tank.getName());
			cameraMoveTo(tank.getPositionX(), tank.getPositionY());
		}
		else if(cmd.equals("Start"))
		{
			_turns.start();
			_popups.pop();
		}
		else if(cmd.equals("Stop"))
		{
			_turns.stop();
		}
		else if(cmd.equals("Shot") && data instanceof float[])
		{
			float[] loc = (float[])data;
			detonate(loc[0], loc[1]);
		}
//		else if(cmd.equals("Pause"))
//		{
//			_paused = true;
//		}
//		else if(cmd.equals("Resume"))
//		{
//			_paused = false;
//		}
	}
	
	private void detonate(float x, float y)
	{
		for(IActor act : _actors.getActors())
		{
			if(act instanceof Tank)
			{
				Tank tank = (Tank)act;
				if(Utils.hitTest(tank, x, y))
					_actors.detach(tank);
			}
		}
	}
	
	private void cameraMoveTo(float x, float y)
	{
		float w = _cam.getViewWidth() * 0.5f;
		float h = _cam.getViewHeight() * 0.5f;
		x = Math.max(w, Math.min(_width - w, x));
		y = Math.max(h, Math.min(_height - h, y));
		_camTargetX = x;
		_camTargetY = y;
	}
	
	private void setTimerText(int value)
	{
		if(value != _turnLastTime)
		{
			_turnLastTime = value;
			_turnTimerText.build("Time left: " + value, _font, _fontMaterial, Font.Alignment.RIGHT, Font.Alignment.BOTTOM, 1.0f, 1.0f);
		}
	}
	
	private void setPlayerText(String value)
	{
		_turnNameText.build("Current turn: " + value, _font, _fontMaterial, Font.Alignment.RIGHT, Font.Alignment.TOP, 1.0f, 1.0f);
	}
	
	public void applyCircle(String[] d, int w, int h, int x, int y, int r, String repl)
	{
		if(d == null)
			return;
		if(d.length != w * h)
			return;
		int axb = Math.max(0, Math.min(w - 1, x - r));
		int ayb = Math.max(0, Math.min(h - 1, y - r));
		int axe = Math.max(0, Math.min(w - 1, x + r));
		int aye = Math.max(0, Math.min(h - 1, y + r));
		int rx = 0;
		int ry = 0;
		int rr = r * r;
		for(int k = ayb; k <= aye; k++)
		{
			ry = k - y;
			ry = ry * ry;
			for(int i = axb; i <= axe; i++)
			{
				rx = i - x;
				rx = rx * rx;
				if(rx + ry < rr)
					d[(k * w) + i] = repl;
			}
		}
	}
	
	public boolean checkQuad(String[] d, byte[] vd, int w, int h, int fx, int fy, int tx, int ty)
	{
		if(d == null || vd == null)
			return false;
		if(d.length != w * h || vd.length != w * h)
			return false;
		int cb = Math.max(0, Math.min(w - 1, fx));
		int ce = Math.max(0, Math.min(w - 1, tx));
		int rb = Math.max(0, Math.min(h - 1, fy));
		int re = Math.max(0, Math.min(w - 1, ty));
		int p = 0;
		for(int k = rb; k <= re; k++)
		{
			for(int i = cb; i <= ce; i++)
			{
				p = ((k * h) + i);
				if(vd[p] > 0 || d[p].equals("0"))
					return false;
			}
		}
		return true;
	}
	
	public void applyQuad(int[] d, byte[] vd, int w, int h, int x, int y, int tcb, int trb, int tce, int tre)
	{
		if(d == null || vd == null)
			return;
		if(d.length != w * h * 2 || vd.length != w * h)
			return;
		int tc = Math.abs(tce - tcb) + 1;
		int tr = Math.abs(tre - trb) + 1;
		int cb = Math.max(0, Math.min(w - 1, x));
		int ce = Math.max(0, Math.min(w - 1, x + tc));
		int rb = Math.max(0, Math.min(h - 1, y));
		int re = Math.max(0, Math.min(w - 1, y + tr));
		int c = tcb;
		int r = trb;
		int p = 0;
		for(int k = rb; k < re; k++, r++)
		{
			c = tcb;
			for(int i = cb; i < ce; i++, c++)
			{
				p = ((k * h) + i);
				vd[p] = (byte)1;
				p *= 2;
				d[p + 0] = c;
				d[p + 1] = r;
			}
		}
	}
}

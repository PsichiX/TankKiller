package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.XeSense;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeApplication.*;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XePhoton;
import java.util.ArrayList;
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
	private TurnManager _turns;
	private PlayerInfo[] _players;
	private CommandQueue _cmds = new CommandQueue();
	private PopupsManager _popups;
	private boolean _paused = false;
	private TileMapGenerator _tmap;
	private byte[] _smap;
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
	private float _delayedNextTurnTimer = 0.0f;
	private boolean _delayedNextTurnTrigger = false;
	private Font _font;
	private Material _fontMaterial;
	
	public GameState(PlayerInfo[] players)
	{
		super();
		_players = players;
		_cmds.setDelegate(this);
		_turns = new TurnManager(_actors);
		_turns.setReceiver(_cmds);
	}
	
	@Override
	public void onBack()
	{
		if(_popups.isShowing())
			_popups.onBack();
		else
			_cmds.queueCommand(this, "Pause", null);
	}
	
	@Override
	public void onEnter()
	{
		//getApplication().getPhoton().setRenderMode(XePhoton.RenderMode.STREAM, true);
		/*WaitingScreen ws = (WaitingScreen)getApplication().getAssets().get(
			R.raw.waiting_screen,
			WaitingScreen.class
			);
		ws.setOrder(-0.9f);
		ws.start();*/
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.0f, 0.0f, 0.0f, 1.0f);
		
		//getApplication().getAssets().get(R.raw.camera, Camera2D.class);
		//Message.alert(getApplication().getContext(), "error", getApplication().getAssets().getLastError(), "ok", null);
		
		_scn = (Scene)getApplication().getAssets().get(R.raw.scene, Scene.class);
		_cam = (Camera2D)_scn.getCamera();
		_cam.setViewPosition(_width * 0.5f, _height * 0.5f);
		//_cam.setNearFar(-3.0f, 1.0f);
		_scnHud = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_camHud = (Camera2D)_scnHud.getCamera();
		_camHud.setViewPosition(
			_camHud.getViewWidth() * 0.5f,
			_camHud.getViewHeight() * 0.5f
			);
		//_camHud.setNearFar(-3.0f, 1.0f);
		_popups = new PopupsManager(_scnHud);
		
		_tmap = (TileMapGenerator)getApplication().getAssets().get(R.raw.map, TileMapGenerator.class);
		String[] md = _tmap.createMap("terrain", _cols, _rows, null);
		for(int i = 0; i < md.length; i++)
			md[i] = "0";
		Random r = new Random(System.currentTimeMillis());
		for(int i = 0; i < 15; i++)
			applyCircle(md, _cols, _rows, Math.abs(r.nextInt()) % (_cols - 1), Math.abs(r.nextInt()) % (_rows - 1), 6 + Math.abs(r.nextInt()) % 10, "1");
		for(int i = 0; i < 10; i++)
			applyCircle(md, _cols, _rows, Math.abs(r.nextInt()) % (_cols - 1), Math.abs(r.nextInt()) % (_rows - 1), 3 + Math.abs(r.nextInt()) % 5, "0");
		applyCircle(md, _cols, _rows, 0, 0, 5, "1");
		applyCircle(md, _cols, _rows, _cols - 1, 0, 5, "1");
		applyCircle(md, _cols, _rows, _cols - 1, _rows - 1, 5, "1");
		applyCircle(md, _cols, _rows, 0, _rows - 1, 5, "1");
		applyCircle(md, _cols, _rows, _cols / 2, _rows / 2, 5, "1");
		applyLine(md, _cols, _rows, 0, 0, _cols - 1, _rows - 1, "1");
		applyLine(md, _cols, _rows, 0, _rows - 1, _cols - 1, 0, "1");
		_map = new TileMap();
		_tmap.applyPatterns("terrain");
		_smap = _tmap.generateSolidMap("terrain", null);
		_tmap.buildCompatibleTileMap("terrain", _map, _width, _height, 1.0f, 1.0f);
		_tmap.applyTiles("terrain", _map);
		_scn.attach(_map);
		
		for(PlayerInfo player : _players)
		{
			Base base = BaseFactory.getFactory().createBase(player.color);
			Tank tank = TankFactory.getFactory().createTank(player.color, _width, _height, true);
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
		
		Material mat = (Material)getApplication().getAssets().get(R.raw.flag_material, Material.class);
		Image img = (Image)getApplication().getAssets().get(R.drawable.flaga, Image.class);
		
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
		_turnNameText.setPosition(_camHud.getViewWidth(), 0.0f, -0.7f);
		_scnHud.attach(_turnNameText);
		setPlayerText("");
		_turnTimerText = new Text();
		_turnTimerText.setPosition(_camHud.getViewWidth(), _camHud.getViewHeight(), -0.7f);
		_scnHud.attach(_turnTimerText);
		setTimerText(0);
		mat = (Material)getApplication().getAssets().get(R.raw.turn_next_btn_material, Material.class);
		img = (Image)getApplication().getAssets().get(R.drawable.btn_next_turn, Image.class);
		_turnNextBtn = new Sprite(mat);
		_turnNextBtn.setSizeFromImage(img, 0.6f);
		_turnNextBtn.setOffsetFromSize(0.0f, 1.0f);
		_turnNextBtn.setPosition(0.0f, _camHud.getViewHeight(), -0.7f);
		_scnHud.attach(_turnNextBtn);
		
		_popups.push(new StartPopup(getApplication(), _cmds));
		
		//ws.stop();
		//getApplication().getPhoton().setRenderMode(XePhoton.RenderMode.QUEUE, true);
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
		TileMapGenerator map = (TileMapGenerator)getApplication().getAssets().get(R.raw.map, TileMapGenerator.class);
		map.destroyMap("terrain");
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
			float[] locHud = _camHud.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			if(Utils.hitTest(_turnNextBtn, locHud[0], locHud[1]))
			{
				_turns.nextPlayer();
				return;
			}
		}
		_actors.onInput(ev);
		t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			_startLoc = _cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
		}
		t = ev.getTouchByState(Touch.State.IDLE);
		if(!_paused && t != null && _startLoc != null)
		{
			float[] loc = _cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
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
		float dt = 0.001f * (float)getApplication().getTimer().getFixedStep();
		//float dt = 1.0f / 30.0f;
		
		if(_delayedNextTurnTrigger)
		{
			_delayedNextTurnTimer -= dt;
			if(_delayedNextTurnTimer <= 0.0f)
				_turns.nextPlayer();
		}
		
		_cmds.run();
		_paused = _popups.isShowing();
		if(_paused)
			_popups.onUpdate(dt);
		else
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
			if(_turns.getPlayersCount() <= 1)
				_popups.push(new EndPopup(getApplication(), _cmds, (Tank)_turns.getPlayer(0)));
			_startLoc = null;
			Tank tank = (Tank)data;
			setPlayerText(tank.getName());
			cameraMoveTo(tank.getPositionX(), tank.getPositionY());
			_delayedNextTurnTrigger = false;
		}
		else if(cmd.equals("Start"))
		{
			_turns.start();
			if(sender instanceof Popup)
				_popups.pop((Popup)sender);
		}
		else if(cmd.equals("Stop"))
		{
			_turns.stop();
			if(sender instanceof Popup)
				_popups.pop((Popup)sender);
			getApplication().popState();
		}
		else if(cmd.equals("Shot") && data instanceof float[])
		{
			float[] loc = (float[])data;
			detonate(loc[0], loc[1], loc[2], loc[3]);
			//_turns.nextPlayer();
			_delayedNextTurnTimer = 1.5f;
			_delayedNextTurnTrigger = true;
			Vibration.getInstance().vibrate(2);
		}
		else if(cmd.equals("MoveTank") && data instanceof float[])
		{
			if(sender instanceof Tank)
			{
				Tank tank = (Tank)sender;
				float[] loc = (float[])data;
				int[] tcr = _map.convertLocationWorldToTile(tank.getPositionX(), tank.getPositionY());
				int[] cr = _map.convertLocationWorldToTile(loc[0], loc[1]);
				if(!checkLineSolid(_smap, _map.getCols(), _map.getRows(), tcr[0], tcr[1], cr[0], cr[1]))
					tank.moveToPos(loc[0], loc[1]);
				else
					_cmds.queueCommand(this, "CannotMove", null);
			}
		}
		else if(cmd.equals("TankKilled") && sender instanceof Tank)
		{
			Tank tank = (Tank)sender;
			tank.reset();
		}
		else if(cmd.equals("CannotMove"))
		{
			Vibration.getInstance().vibrate(0);
		}
		else if(cmd.equals("FlagScored") && data instanceof Integer)
		{
			int score = (Integer)data;
			if(score >= 1)
				 _popups.push(new EndPopup(getApplication(), _cmds, (Tank)sender));
		}
		else if(cmd.equals("Pause"))
		{
			while(_popups.has(PausePopup.class))
				_popups.pop(_popups.get(PausePopup.class));
			_popups.push(new PausePopup(getApplication(), _cmds));
		}
		else if(cmd.equals("Resume"))
		{
			if(sender instanceof Popup)
				_popups.pop((Popup)sender);
		}
	}
	
	private void detonate(float xs, float ys, float xe, float ye)
	{
		for(IActor act : _actors.getActors())
		{
			if(act instanceof Tank)
			{
				Tank tank = (Tank)act;
				if(Utils.hitTest(tank, xe, ye))
					//_actors.detach(tank);
					//tank.resetPosition();
					tank.setEnergy(tank.getEnergy() - 0.25f);
			}
		}
		Random r = new Random(System.currentTimeMillis());
		Material mat = (Material)getApplication().getAssets().get(R.raw.explosion_material, Material.class);
		Explosion e = new Explosion(mat, 0.5f);
		e.setSize(64.0f, 64.0f);
		e.setOffsetFromSize(0.5f, 0.5f);
		e.setPosition(xs, ys, -0.75f);
		e.setAngle(r.nextFloat() * 360.0f);
		_scn.attach(e);
		_actors.attach(e);
		for(int i = 0; i < 12; i++)
		{
			float rx = (r.nextFloat() * 128.0f) - 64.0f;
			float ry = (r.nextFloat() * 128.0f) - 64.0f;
			e = new Explosion(mat, 0.5f + (r.nextFloat() * 0.5f));
			e.setSize(256.0f, 256.0f);
			e.setOffsetFromSize(0.5f, 0.5f);
			e.setPosition(xe + rx, ye + ry, -0.75f);
			e.setAngle(r.nextFloat() * 360.0f);
			_scn.attach(e);
			_actors.attach(e);
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
	
	private boolean checkLineSolid(byte[] d, int w, int h, int xs, int ys, int xe, int ye)
	{
		if(d == null)
			return true;
		if(d.length != w * h)
			return true;
		if(xs < 0 || xs >= w || ys < 0 || ys >= h ||
			xe < 0 || xe >= w || ye < 0 || ye >= h)
			return true;
		int dx = xe - xs;
		int dy = ye - ys;
		int sdx = dx >= 0 ? 1 : -1;
		int sdy = dy >= 0 ? 1 : -1;
		int adx = Math.abs(dx);
		int ady = Math.abs(dy);
		if(adx == 0 && ady == 0)
		{
			int p = (w * ys) + xs;
			return d[p] > (byte)0;
		}
		else if(adx >= ady)
		{
			int x = 0;
			int y = 0;
			int p = 0;
			float delta = (float)(dy) / (float)(dx);
			for(int t = 0; t <= adx; t++)
			{
				x = (t * sdx) + xs;
				y = (int)(delta * (float)x + (float)ys - delta * (float)xs);
				p = (w * y) + x;
				if(p < 0 || p >= w * h)
					Message.alert(getApplication().getContext(), "log",
						"Xd: "+delta+
						"\nx: "+x+
						"\ny: "+y+
						"\nxs: "+xs+
						"\nys: "+ys+
						"\nxe: "+xe+
						"\nye: "+ye+
						"\np: "+p,
						"ok", null);
				if(d[p] > (byte)0)
					return true;
			}
		}
		else
		{
			int y = 0;
			int x = 0;
			int p = 0;
			float delta = (float)(dx) / (float)(dy);
			for(int t = 0; t <= ady; t++)
			{
				y = (t * sdy) + ys;
				x = (int)(delta * (float)y + xs - delta * (float)ys);
				p = (w * y) + x;
				if(p < 0 || p >= w * h)
					Message.alert(getApplication().getContext(), "log",
						"Yd: "+delta+
						"\nx: "+x+
						"\ny: "+y+
						"\nxs: "+xs+
						"\nys: "+ys+
						"\nxe: "+xe+
						"\nye: "+ye+
						"\np: "+p,
						"ok", null);
				if(d[p] > (byte)0)
					return true;
			}
		}
		return false;
	}
	
	public void applyLine(String[] d, int w, int h, int xs, int ys, int xe, int ye, String repl)
	{
		if(d == null)
			return;
		if(d.length != w * h)
			return;
		if(xs < 0 || xs >= w || ys < 0 || ys >= h ||
			xe < 0 || xe >= w || ye < 0 || ye >= h)
			return;
		int dx = xe - xs;
		int dy = ye - ys;
		int sdx = dx >= 0 ? 1 : -1;
		int sdy = dy >= 0 ? 1 : -1;
		int adx = Math.abs(dx);
		int ady = Math.abs(dy);
		if(adx == 0 && ady == 0)
		{
			int p = (w * ys) + xs;
			d[p] = repl;
		}
		else if(adx >= ady)
		{
			int x = 0;
			int y = 0;
			int p = 0;
			float delta = (float)(dy) / (float)(dx);
			for(int t = 0; t <= adx; t++)
			{
				x = (t * sdx) + xs;
				y = (int)(delta * (float)x + (float)ys - delta * (float)xs);
				p = (w * y) + x;
				if(p < 0 || p >= w * h)
					Message.alert(getApplication().getContext(), "log",
						"Xd: "+delta+
						"\nx: "+x+
						"\ny: "+y+
						"\nxs: "+xs+
						"\nys: "+ys+
						"\np: "+p,
						"ok", null);
				d[p] = repl;
			}
		}
		else
		{
			int y = 0;
			int x = 0;
			int p = 0;
			float delta = (float)(dx) / (float)(dy);
			for(int t = 0; t <= ady; t++)
			{
				y = (t * sdy) + ys;
				x = (int)(delta * (float)y + (float)xs - delta * (float)ys);
				p = (w * y) + x;
				if(p < 0 || p >= w * h)
					Message.alert(getApplication().getContext(), "log",
						"Yd: "+delta+
						"\nx: "+x+
						"\ny: "+y+
						"\nxs: "+xs+
						"\nys: "+ys+
						"\np: "+p,
						"ok", null);
				d[p] = repl;
			}
		}
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

package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.XeSense;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeApplication.*;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import java.util.Random;

public class TestState extends State implements CommandQueue.Delegate
{
	private Scene _scn;
	private Camera2D _cam;
	private Scene _scnHud;
	private Camera2D _camHud;
	private ActorsManager _actors = new ActorsManager(this);
	private CollisionManager _colls = new CollisionManager();
	private CommandQueue _cmds = new CommandQueue();
	private TileMap _map;
	private Tank _tank;
	private Controler _controler;
	private final int _cols = 50;
	private final int _rows = 50;
	private final float _width = _cols * 100.0f;
	private final float _height = _rows * 100.0f;
	private int[] _area = new int[]{ -1, -1 };
	private int[] _padding = new int[]{ 5, 5 };
	
	@Override
	public void onEnter()
	{
		//getApplication().getAssets().get(R.raw.camera, Camera2D.class);
		//Message.alert(getApplication().getContext(), "error", getApplication().getAssets().getLastError(), "ok", null);
		_cmds.setDelegate(this);
		
		_scn = (Scene)getApplication().getAssets().get(R.raw.scene, Scene.class);
		_cam = (Camera2D)_scn.getCamera();
		_scnHud = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_camHud = (Camera2D)_scnHud.getCamera();
		_camHud.setViewPosition(
			_camHud.getViewWidth() * 0.5f,
			_camHud.getViewHeight() * 0.5f
			);
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 1.0f, 1.0f, 1.0f, 1.0f);
		
		TileMapGenerator map = (TileMapGenerator)getApplication().getAssets().get(R.raw.map, TileMapGenerator.class);
		String[] md = map.createMap("terrain", _cols, _rows, null);
		for(int i = 0; i < md.length; i++)
			md[i] = "0";
		Random r = new Random(System.currentTimeMillis());
		for(int i = 0; i < 15; i++)
			applyCircle(md, _cols, _rows, Math.abs(r.nextInt()) % (_cols - 1), Math.abs(r.nextInt()) % (_rows - 1), 10 + Math.abs(r.nextInt()) % 50, "1");
		for(int i = 0; i < 25; i++)
			applyCircle(md, _cols, _rows, Math.abs(r.nextInt()) % (_cols - 1), Math.abs(r.nextInt()) % (_rows - 1), 5 + Math.abs(r.nextInt()) % 25, "0");
		_map = new TileMap();
		map.applyPatterns("terrain");
		map.buildCompatibleTileMap("terrain", _map, _width, _height, 1.0f, 1.0f);
		map.applyTiles("terrain", _map);
		_scn.attach(_map);
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.0f, 0.0f, 1.0f, 1.0f);
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.0f, 1.0f, 0.0f, 1.0f);
		
		Material mat = (Material)getApplication().getAssets().get(R.raw.tank_material, Material.class);
		Image img = (Image)getApplication().getAssets().get(R.drawable.swallow, Image.class);
		_tank = new Tank(mat);
		_tank.setSizeFromImage(img);
		_tank.setOffsetFromSize(0.5f, 0.5f);
		_tank.setPosition(_width * 0.5f, _height * 0.5f);
		_tank.setRange(_tank.getHeight() * 0.5f);
		_tank.setReceiver(_cmds);
		_scn.attach(_tank);
		_actors.attach(_tank);
		_colls.attach(_tank);
		
		_controler = new TouchControler(getApplication().getAssets(), _scnHud, 100.0f, 75.0f, _camHud.getViewHeight() - 75.0f);
		_controler.setTarget(_tank);
		_actors.attach(_controler);
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 1.0f, 0.0f, 0.0f, 1.0f);
	}
	
	@Override
	public void onExit()
	{
		_scn.releaseAll();
		_scnHud.releaseAll();
		_actors.detachAll();
		_colls.detachAll();
		getApplication().getPhoton().unregisterDrawCalls();
	}
	
	@Override
	public void onInput(Touches ev)
	{
		_actors.onInput(ev);
	}
	
	@Override
	public void onSensor(XeSense.EventData ev)
	{
		ev.owner.remapCoords(ev.values, 1, 0);
		_actors.onSensor(ev);
	}
	
	@Override
	public void onUpdate()
	{
		getApplication().getSense().setCoordsOrientation(-1);
		
		//float dt = getApplication().getTimer().getDeltaTime() / 1000.0f;
		float dt = 1.0f / 30.0f;
		
		_cmds.run();
		_actors.onUpdate(dt);
		_colls.test();
		float w = _cam.getViewWidth() * 0.5f;
		float h = _cam.getViewHeight() * 0.5f;
		_tank.setPosition(
			Math.max(0.0f, Math.min(_width, _tank.getPositionX())),
			Math.max(0.0f, Math.min(_height, _tank.getPositionY()))
			);
		_cam.setViewPosition(
			Math.max(w, Math.min(_width - w, _tank.getPositionX())),
			Math.max(h, Math.min(_height - h, _tank.getPositionY()))
			);
		int[] tloc = _map.convertLocationWorldToTile(
			_cam.getViewPositionX(),
			_cam.getViewPositionY()
			);
		if(Math.abs(tloc[0] - _area[0]) > _padding[0] ||
			Math.abs(tloc[1] - _area[1]) > _padding[1] )
		{
			int cols = (int)(_cam.getViewWidth() / _map.getCellWidth()) + (2 * _padding[0]) + 2;
			int rows = (int)(_cam.getViewHeight() / _map.getCellHeight()) + (2 * _padding[1]) + 2;
			_map.setVisibleRegion(tloc[0] - cols / 2, tloc[1] - rows / 2, tloc[0] + cols / 2, tloc[1] + rows / 2, null);
			_area[0] = tloc[0];
			_area[1] = tloc[1];
		}
		_scn.update(dt);
		_scnHud.update(dt);
	}
	
	public void onCommand(Object sender, String cmd, Object data)
	{
		
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

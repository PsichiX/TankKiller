package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Camera2D;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Scene;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.XeApplication.State;
import com.PsichiX.XenonCoreDroid.XeApplication.Touch;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;

public class SplashScreenState extends State {
	
	private Camera2D _cam;
	private Scene _scn;
	
	private Sprite _background;
	
	@Override
	public void onEnter()
	{
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.176f, 0.498f, 0.156f, 1.0f);
		
		_scn = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_cam = (Camera2D)_scn.getCamera();
		_cam.setViewPosition(_cam.getViewWidth() * 0.5f, _cam.getViewHeight() * 0.5f);
		
		Material matBg = (Material) getApplication().getAssets().get(R.raw.menu_bg_mat, Material.class);
		Image bgImg = (Image) getApplication().getAssets().get(R.drawable.menu_bg, Image.class);
		
		_background = new Sprite(matBg);
		_background.setSize(_cam.getViewWidth(), _cam.getViewHeight());
		
		_scn.attach(_background);
	}
	
	@Override
	public void onReload()
	{
		_cam.setViewPosition(_cam.getViewWidth() * 0.5f, _cam.getViewHeight() * 0.5f);
	}

	@Override
	public void onUpdate()
	{
		float dt = getApplication().getTimer().getDeltaTime() * 0.001f;
		_scn.update(dt);
	}
	
	
	@Override
	public void onInput(Touches ev)
	{
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		
		if(t!= null){
			getApplication().changeState(new MenuState());
		}

	}
	
	@Override
	public void onExit()
	{
		_scn.releaseAll();
		getApplication().getPhoton().unregisterDrawCalls();
	}
	
}

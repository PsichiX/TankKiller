package com.PsichiX.TankKiller;

import com.PsichiX.TankKiller.R;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Camera2D;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Scene;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Font;
import com.PsichiX.XenonCoreDroid.XeApplication.State;
import com.PsichiX.XenonCoreDroid.XeApplication.Touch;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;

public class SplashScreenState extends State {
	
	private Camera2D _cam;
	private Scene _scn;
	
	private Sprite _background;
	private BlinkLabel _title;
	
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
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.176f, 0.498f, 0.156f, 1.0f);
		
		_scn = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_cam = (Camera2D)_scn.getCamera();
		_cam.setViewPosition(_cam.getViewWidth() * 0.5f, _cam.getViewHeight() * 0.5f);
		
		Material mat = (Material)getApplication().getAssets().get(R.raw.start_material, Material.class);
		Image img = (Image)getApplication().getAssets().get(R.drawable.start_bg, Image.class);
		_background = new Sprite(mat);
		_background.setPosition(_cam.getViewWidth() * 0.5f, _cam.getViewHeight() * 0.5f);
		_background.setSizeFromImageAspect(img, false, _cam.getViewZoomOut());
		_background.setOffsetFromSize(0.5f, 0.5f);
		_scn.attach(_background);
		
		mat = (Material)getApplication().getAssets().get(R.raw.alpha_shadow_material, Material.class);
		_title = new BlinkLabel(mat);
		_title.setPadding(48.0f, 16.0f);
		mat = (Material)getApplication().getAssets().get(R.raw.alpha_badaboom_material, Material.class);
		Font font = (Font)getApplication().getAssets().get(R.raw.badaboom_font, Font.class);
		_title.build("Tap to start battle", font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.5f, 1.5f);
		_title.setPosition(_cam.getViewWidth() * 0.5f, _cam.getViewHeight() * 0.75f, -0.5f);
		_scn.attach(_title);
		
		//ws.stop();
		//getApplication().getPhoton().setRenderMode(XePhoton.RenderMode.QUEUE, true);
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
		_title.animate(dt);
		_scn.update(dt);
	}
	
	@Override
	public void onInput(Touches ev)
	{
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		
		if(t != null)
			getApplication().changeState(new MenuState());
	}
	
	@Override
	public void onExit()
	{
		_scn.releaseAll();
		getApplication().getPhoton().unregisterDrawCalls();
	}
	
}

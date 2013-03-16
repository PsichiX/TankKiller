package com.PsichiX.TankKiller;

import com.PsichiX.TankKiller.R;
import com.PsichiX.XenonCoreDroid.XeApplication.Touch;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;
import com.PsichiX.XenonCoreDroid.XeApplication;
import com.PsichiX.XenonCoreDroid.XeUtils.CommandQueue;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Camera2D;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Font;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;

public class PausePopup extends Popup
{
	private XeApplication _app;
	private CommandQueue _rcv;
	private Sprite _bg;
	private Sprite _logo;
	private Label _title;
	private Label _resume;
	private Label _exit;
	
	public PausePopup(XeApplication app, CommandQueue rcv)
	{
		super();
		_app = app;
		_rcv = rcv;
		Material mat = (Material)app.getAssets().get(R.raw.popup_material, Material.class);
		_bg = new Sprite(mat);
		//_bg.getProperties().setVec("uColor", new float[]{0.0f, 0.0f, 0.0f, 0.75f});
		mat = (Material)app.getAssets().get(R.raw.logo_material, Material.class);
		Image img = (Image)app.getAssets().get(R.drawable.logo, Image.class);
		_logo = new Sprite(mat);
		_logo.setSizeFromImageAspect(img, false, 150.0f);
		Material smat = (Material)app.getAssets().get(R.raw.shadow_material, Material.class);
		Material bmat = (Material)app.getAssets().get(R.raw.button_material, Material.class);
		mat = (Material)app.getAssets().get(R.raw.badaboom_material, Material.class);
		Font font = (Font)app.getAssets().get(R.raw.badaboom_font, Font.class);
		_title = new Label(smat);
		_title.setPadding(32.0f, 16.0f);
		_title.build("Game is paused", font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.5f, 1.5f);
		_resume = new Label(bmat);
		_resume.setPadding(16.0f, 16.0f);
		_resume.setMinSize(200.0f, 0.0f);
		_resume.build("Resume", font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.5f, 1.5f);
		_exit = new Label(bmat);
		_exit.setPadding(16.0f, 16.0f);
		_exit.setMinSize(200.0f, 0.0f);
		_exit.build("Exit", font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.5f, 1.5f);
	}
	
	@Override
	public void onShow(PopupsManager man)
	{
		super.onShow(man);
		Camera2D cam = (Camera2D)man.getScene().getCamera();
		_bg.setSize(cam.getViewWidth(), cam.getViewHeight());
		_bg.setPosition(0.0f, 0.0f, -0.8f);
		_logo.setPosition(0.0f, 0.0f, -0.85f);
		_title.setPosition(cam.getViewWidth() * 0.5f, cam.getViewHeight() * 0.4f, -0.9f);
		_resume.setPosition(cam.getViewWidth() * 0.5f, cam.getViewHeight() * 0.6f, -0.9f);
		_exit.setPosition(cam.getViewWidth() * 0.5f, cam.getViewHeight() * 0.8f, -0.9f);
		man.getScene().attach(_bg);
		man.getScene().attach(_logo);
		man.getScene().attach(_title);
		man.getScene().attach(_resume);
		man.getScene().attach(_exit);
		_app.getTouches().clear();
	}
	
	@Override
	public void onHide()
	{
		super.onHide();
		if(_bg.getScene() != null)
			_bg.getScene().detach(_bg);
		if(_logo.getScene() != null)
			_logo.getScene().detach(_logo);
		if(_title.getScene() != null)
			_title.getScene().detach(_title);
		if(_resume.getScene() != null)
			_resume.getScene().detach(_resume);
		if(_exit.getScene() != null)
			_exit.getScene().detach(_exit);
	}
	
	@Override
	public boolean onInput(Touches ev)
	{
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null && _rcv != null)
		{
			Camera2D cam = (Camera2D)getOwner().getScene().getCamera();
			float[] loc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			if(Utils.hitTest(_resume, loc[0], loc[1]))
				_rcv.queueCommand(this, "Resume", null);
			if(Utils.hitTest(_exit, loc[0], loc[1]))
				_rcv.queueCommand(this, "Stop", null);
		}
		return true;
	}
}

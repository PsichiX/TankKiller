package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.XeApplication.Touch;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;
import com.PsichiX.XenonCoreDroid.XeApplication;
import com.PsichiX.XenonCoreDroid.XeUtils.CommandQueue;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Camera2D;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Font;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Text;

public class EndPopup extends Popup
{
	private XeApplication _app;
	private CommandQueue _rcv;
	private Sprite _bg;
	private Text _title;
	
	public EndPopup(XeApplication app, CommandQueue rcv, Tank tank)
	{
		super();
		_app = app;
		_rcv = rcv;
		Material mat = (Material)app.getAssets().get(R.raw.popup_win_material, Material.class);
		_bg = new Sprite(mat);
		//_bg.getProperties().setVec("uColor", new float[]{0.0f, 0.0f, 0.0f, 0.75f});
		mat = (Material)app.getAssets().get(R.raw.badaboom_material, Material.class);
		Font font = (Font)app.getAssets().get(R.raw.badaboom_font, Font.class);
		_title = new Text();
		if(tank != null)
			_title.build(tank.getName() + " won battle with\n" + tank.getScore() + "\nflags scored!\n\nTap to back",
				font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.5f, 1.5f
				);
		else
			_title.build("Everyone lose battle!\n\nTap to back",
				font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.5f, 1.5f
				);
	}
	
	@Override
	public void onShow(PopupsManager man)
	{
		super.onShow(man);
		Camera2D cam = (Camera2D)man.getScene().getCamera();
		_bg.setSize(cam.getViewWidth(), cam.getViewHeight());
		_bg.setPosition(0.0f, 0.0f, -0.8f);
		_title.setPosition(cam.getViewWidth() * 0.5f, cam.getViewHeight() * 0.5f, -0.9f);
		man.getScene().attach(_bg);
		man.getScene().attach(_title);
		_app.getTouches().clear();
	}
	
	@Override
	public void onHide()
	{
		super.onHide();
		if(_bg.getScene() != null)
			_bg.getScene().detach(_bg);
		if(_title.getScene() != null)
			_title.getScene().detach(_title);
	}
	
	@Override
	public boolean onInput(Touches ev)
	{
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null && _rcv != null)
			_rcv.queueCommand(this, "Stop", null);
		return true;
	}
}

package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.XeApplication.Touch;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;
import com.PsichiX.XenonCoreDroid.XeAssets;
import com.PsichiX.XenonCoreDroid.XeUtils.CommandQueue;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Camera2D;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Font;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Text;

public class StartPopup extends Popup
{
	private PopupsManager _man;
	private CommandQueue _rcv;
	private Sprite _bg;
	private Text _title;
	
	public StartPopup(XeAssets ass, CommandQueue rcv)
	{
		super();
		_rcv = rcv;
		Material mat = (Material)ass.get(R.raw.fill_color_material, Material.class);
		_bg = new Sprite(mat);
		_bg.getProperties().setVec("uColor", new float[]{0.0f, 0.0f, 0.0f, 0.75f});
		mat = (Material)ass.get(R.raw.badaboom_material, Material.class);
		Font font = (Font)ass.get(R.raw.badaboom_font, Font.class);
		_title = new Text();
		_title.build("Tap to start", font, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.0f, 1.0f);
	}
	
	@Override
	public void onShow(PopupsManager man)
	{
		super.onShow(man);
		_man = man;
		Camera2D cam = (Camera2D)man.getScene().getCamera();
		_bg.setSize(cam.getViewWidth(), cam.getViewHeight());
		_bg.setPosition(0.0f, 0.0f);
		_title.setPosition(cam.getViewWidth() * 0.5f, cam.getViewHeight() * 0.5f);
		man.getScene().attach(_bg);
		man.getScene().attach(_title);
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
		Touch t = ev.getTouchByState(Touch.State.UP);
		if(t != null && _rcv != null)
			_rcv.queueCommand(this, "Start", null);
//		_man.pop();
		return true;
	}
}

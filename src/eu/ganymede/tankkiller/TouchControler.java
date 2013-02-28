package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeAssets;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class TouchControler extends Controler
{
	private Sprite _base;
	private Sprite _joy;
	private float _size = 0.0f;
	private Scene _scn;
	private int _tid = -1;
	
	public TouchControler(XeAssets ass, Scene scn, float joySize, float x, float y)
	{
		super();
		_scn = scn;
		_size = joySize;
		Material mat = (Material)ass.get(R.raw.joystick_material, Material.class);
		_base = new Sprite(mat);
		_base.setSize(_size, _size);
		_base.setOffsetFromSize(0.5f, 0.5f);
		_base.setTextureOffset(-0.5f, -0.5f);
		_joy = new Sprite(mat);
		_joy.setSize(_size * 0.5f, _size * 0.5f);
		_joy.setOffsetFromSize(0.5f, 0.5f);
		_joy.setTextureOffset(-0.5f, -0.5f);
		setPosition(x, y);
	}
	
	public void setPosition(float x, float y)
	{
		_base.setPosition(x, y);
		_joy.setPosition(x, y);
	}
	
	@Override
	public void onAttach(ActorsManager m)
	{
		super.onAttach(m);
		_scn.attach(_base);
		_scn.attach(_joy);
	}
	
	@Override
	public void onDetach(ActorsManager m)
	{
		super.onDetach(m);
		_scn.detach(_base);
		_scn.detach(_joy);
	}
	
	@Override
	public void onInput(Touches ev)
	{
		if(!(getTarget() instanceof Tank))
			return;
		Tank sw = (Tank)getTarget();
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			float[] loc = _scn.getCamera().convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			if(MathHelper.vecLength(loc[0] - _base.getPositionX(), loc[1] - _base.getPositionY(), 0.0f) < _size * 0.5f)
				_tid = t.getId();
		}
		t = ev.getTouchByState(Touch.State.UP);
		if(t != null && t.getId() == _tid)
		{
			_tid = -1;
			/*_joy.setPosition(
				_base.getPositionX(),
				_base.getPositionY()
				);*/
		}
		t = ev.getTouchByState(Touch.State.IDLE);
		if(t != null && t.getId() == _tid)
		{
			float[] loc = _scn.getCamera().convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			float mx = (loc[0] - _base.getPositionX()) / (_size * 0.5f);
			float my = (loc[1] - _base.getPositionY()) / (_size * 0.5f);
			float len = MathHelper.vecLength(mx, my, 0.0f);
			float len2 = Math.min(1.0f, len);
			if(len > 0.0f)
			{
				mx = (mx / len) * len2;
				my = (my / len) * len2;
			}
			else
			{
				mx = 0.0f;
				my = 0.0f;
			}
			sw.setMove(mx, my);
			_joy.setPosition(
				_base.getPositionX() + (mx * _size * 0.5f),
				_base.getPositionY() + (my * _size * 0.5f)
				);
		}
	}
}

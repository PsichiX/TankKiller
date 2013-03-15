package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;

public class BlinkLabel extends Label
{
	private float _phase = 0.0f;
	
	public BlinkLabel(Material smat)
	{
		super(smat);
		setPhase(0.0f);
	}
	
	public void setPhase(float p)
	{
		_phase = p;
		float d = 0.5f + (0.5f * (float)Math.cos(_phase * (float)Math.PI * 2.0f));
		getProperties().setVec("uPhase", new float[]{ d });
		_bg.getProperties().setVec("uPhase", new float[]{ d });
	}
	
	public float getPhase()
	{
		return _phase;
	}
	
	public void animate(float dt)
	{
		setPhase(_phase + dt);
	}
}

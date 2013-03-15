package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Actors.ActorSprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;

public class Explosion extends ActorSprite
{
	private float _phase = 1.0f;
	private float _speed = 1.0f;

	public Explosion(Material mat, float spd)
	{
		super(mat);
		_speed = spd;
	}
	
	@Override
	public void onUpdate(float dt)
	{
		super.onUpdate(dt);
		_phase -= _speed * dt;
		getProperties().setVec("uPhase", new float[]{ _phase });
		if(_phase <= 0.0f && getManager() != null)
			getManager().detach(this);
	}
}

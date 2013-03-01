package eu.ganymede.tankkiller;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Actors.ActorSprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;

public class Base extends ActorSprite implements ICollidable
{
	private TankColor _color; 
	public Base(Material mat, TankColor color)
	{
		super(mat);
		_color = color;
	}

	@Override
	public void onAttach(CollisionManager m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDetach(CollisionManager m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CollisionManager getCollisionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRange(float val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCollision(ICollidable o) {
		if(o instanceof Tank)
		{
			Tank t = (Tank) o;
			if(t.getColor() == _color)
			{
				Log.d("BASE <> TANK", "same color " + _color);
				t.flagScored();
				Log.d("BASE SCORE"," tank score " + t.getScore());
			}
		}
	}
	
	public TankColor getColor()
	{
		return _color;
	}
}

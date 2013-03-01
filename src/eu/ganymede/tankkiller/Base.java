package eu.ganymede.tankkiller;

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
		// TODO Auto-generated method stub
		
	}
	
	public TankColor getColor()
	{
		return _color;
	}
}

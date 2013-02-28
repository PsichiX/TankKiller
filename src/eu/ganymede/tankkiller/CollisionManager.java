package eu.ganymede.tankkiller;

import java.util.LinkedList;
import com.PsichiX.XenonCoreDroid.XeUtils.*;

public class CollisionManager
{
	private LinkedList<ICollidable> _colls = new LinkedList<ICollidable>();
	
	public void attach(ICollidable c)
	{
		if(!_colls.contains(c))
		{
			_colls.add(c);
			c.onAttach(this);
		}
	}
	
	public void detach(ICollidable c)
	{
		if(_colls.contains(c))
		{
			_colls.remove(c);
			c.onDetach(this);
		}
	}
	
	public void detachAll()
	{
		for(ICollidable c : _colls)
			c.onDetach(this);
		_colls.clear();
	}
	
	public void test()
	{
		float len = 0.0f;
		for(ICollidable a : _colls)
		{
			for(ICollidable b : _colls)
			{
				if(a != b)
				{
					len = MathHelper.vecLength(
						a.getPositionX() - b.getPositionX(),
						a.getPositionY() - b.getPositionY(),
						0.0f
						);
					if(len < a.getRange() + b.getRange())
						a.onCollision(b);
				}
			}
		}
	}
}

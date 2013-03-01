package eu.ganymede.tankkiller;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeSense;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class Flag extends ActorSprite implements ICollidable
{	
	private CollisionManager _collMan;
	private float _initPosX = 0.0f;
	private float _initPosY = 0.0f;
	private float _range = 0.0f;

	public Flag(Material mat, float initPosX, float initPosY)
	{
		super(mat);
		_initPosX = initPosX;
		_initPosY = initPosY;
		
		resetPosition();
	}

	public void resetPosition()
	{
		setPosition(_initPosX, _initPosY);
	}

	@Override
	public void onDetach(ActorsManager m)
	{
		super.onDetach(m);
		if(getCollisionManager() != null)
			getCollisionManager().detach(this);
	}

	public void onAttach(CollisionManager m)
	{
		_collMan = m;
		Log.d("FLAG","ADDED TO STAGE");
	}

	public void onDetach(CollisionManager m)
	{
		_collMan = null;
	}

	public CollisionManager getCollisionManager()
	{
		return _collMan;
	}
	
	public float getRange()
	{
		return _range;
	}
	
	public void setRange(float val)
	{
		_range = val;
	}
	public void onCollision(ICollidable o)
	{
	    // TODO
	}

}
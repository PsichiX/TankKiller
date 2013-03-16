package com.PsichiX.TankKiller;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;

public class Flag extends ActorSprite implements ICollidable
{	
	private CollisionManager _collMan;
	private float _initPosX = 0.0f;
	private float _initPosY = 0.0f;
	private float _range = 0.0f;
	private Tank _tank = null;

	public Flag(Material mat, float initPosX, float initPosY)
	{
		super(mat);
		_initPosX = initPosX;
		_initPosY = initPosY;
		
		resetPosition();
	}

	public void resetPosition()
	{
		setPosition(_initPosX, _initPosY, -0.5f);
		_tank = null;
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
	

	public void setTank(Tank t)
	{
		_tank = t;
	}
	
	public Tank getTank()
	{
		return _tank;
    }

}
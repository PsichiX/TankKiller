package eu.ganymede.tankkiller;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeSense;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class Tank extends ActorSprite implements ICollidable, IControlable
{
	private CollisionManager _collMan;
	private Controler _controler;
	private float _range = 0.0f;
	private float _movX = 0.0f;
	private float _movY = 0.0f;
	
	private float _destX = 0.0f;
	private float _destY = 0.0f;
	private float _spdX = 500.0f;
	private float _spdY = 500.0f;
	private float _energy = 100.0f;
	private CommandQueue _rcv;
	private float _phase = 0.0f;
	
	public Tank(Material mat)
	{
		super(mat);
	}
	
	public void setSpeed(float x, float y)
	{
		_spdX = x;
		_spdY = y;
	}
	
	public float getMoveX()
	{
		return _movX;
	}
	
	public float getMoveY()
	{
		return _movY;
	}
	

	public void moveToPos(float x, float y)
	{
		float currentPosX = getPositionX();
		float currentPosY = getPositionY();
		_destX = x;
		_destY = y;
		float deltaX = x - currentPosX; 
		float deltaY = y - currentPosY;
		float dest  = MathHelper.vecLength(deltaX, deltaY, 0);
		if(dest != 0)
		{
			_movX = deltaX / dest;
			_movY = deltaY / dest;
		}
		
		setAngle((float)(Math.toDegrees(MathHelper.vecDirectionXY(-deltaX, -deltaY)) + 90));
	}
	
	public float getEnergy()
	{
		return _energy;
	}
	
	public void setReceiver(CommandQueue cmds)
	{
		_rcv = cmds;
	}
	
	@Override
	public void onUpdate(float dt)
	{
		Log.d("DIST"," >> " + MathHelper.vecLength(_destX - _x, _destY - _y, 0));
		if(MathHelper.vecLength(_destX - _x, _destY - _y, 0) > 20)
		{
			setPosition(
				_x + _movX * _spdX * dt,
				_y + _movY * _spdY * dt
				);
		}
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
		Log.d("TANK","ADDED TO STAGE");
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
		/*if(o instanceof Rock)
		{
			Rock r = (Rock)o;
			if(r.getManager() != null)
				r.getManager().detach(r);
			_energy -= 25.0f;
			if(_rcv != null)
				_rcv.queueCommand(this, "Energy", new Float(_energy));
		}*/
	}
	
	public void onAttach(Controler c)
	{
		_controler = c;
	}
	
	public void onDetach(Controler c)
	{
		_controler = null;
	}
	
	public Controler getControler()
	{
		return _controler;
	}
}

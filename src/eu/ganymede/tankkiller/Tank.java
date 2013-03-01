package eu.ganymede.tankkiller;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeSense;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class Tank extends ActorSprite implements ICollidable, ITurnable
{
	private TurnManager _turnMan;
	private CollisionManager _collMan;
	private Controler _controler;
	private float _range = 0.0f;
	private boolean _canMove = false;
	private float _movX = 0.0f;
	private float _movY = 0.0f;
	private String _name;
	private float _initPosX = 0.0f;
	private float _initPosY = 0.0f;
	private float _initAngle = 0.0f;
	private float _destX = 0.0f;
	private float _destY = 0.0f;
	private float _spd = 500.0f;
	private float _energy = 100.0f;
	private CommandQueue _rcv;
	private Sprite _tower;
	private TankColor _color;
	
	private Flag _hasFlag = null;
	
	private int _score = 0;
//	private float _pointsMovement = 100.0f;
	
	public Tank(Material mat, Sprite tower, TankColor color, String name, float initPosX, float initPosY, float initAngle)
	{
		super(mat);
		_tower = tower;
		_name = name;
		_color = color;
		_initPosX = initPosX;
		_initPosY = initPosY;
		_initAngle = initAngle;
		resetPosition();
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void resetPosition()
	{
		setPosition(_initPosX, _initPosY);
		setAngle(_initAngle);
	}
	
	public void setSpeed(float v)
	{
		_spd = v;
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
		if(!_canMove)
			return;
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
		
		setAngle((float)(Math.toDegrees(MathHelper.vecDirectionXY(-deltaX, -deltaY)) - 90));
		_tower.setAngle(getAngleAlpha());
	}
	
	public float getEnergy()
	{
		return _energy;
	}
	
	public void setReceiver(CommandQueue cmds)
	{
		_rcv = cmds;
	}
	
	public CommandQueue getReceiver()
	{
		return _rcv;
	}
	
	public void onTurnChanged(boolean my)
	{
		_canMove = my;
	}
	
	@Override
	public void onUpdate(float dt)
	{
		//if(!_canMove)
		//	return;
		if(/*_pointsMovement > 0.0f &&*/ MathHelper.vecLength(_destX - _x, _destY - _y, 0) > _spd * dt)
		{
			setPosition(
				_x + _movX * _spd * dt,
				_y + _movY * _spd * dt
				);
				if (_hasFlag != null)
					_hasFlag.setPosition(_x,  _y-50.f);
//			_pointsMovement -= _spd * dt;
		}
	}
	
	@Override
	public void onDetach(ActorsManager m)
	{
		super.onDetach(m);
		if(_hasFlag != null)
		{
			_hasFlag.setTank(null);
			_hasFlag = null;
		}
		if(getCollisionManager() != null)
			getCollisionManager().detach(this);
		if(getTurnManager() != null)
			getTurnManager().removePlayerForced(this);
	}
	
	@Override
	public void setScene(Scene s)
	{
		super.setScene(s);
		if(s != null)
			s.attach(_tower);
		else if(_tower != null && _tower.getScene() != null)
			_tower.getScene().detach(_tower);	
	}
	
	public void onAttach(CollisionManager m)
	{
		_collMan = m;
	}
	
	public void onDetach(CollisionManager m)
	{
		_collMan = null;
	}
	
	public CollisionManager getCollisionManager()
	{
		return _collMan;
	}
	
	public void onAttach(TurnManager m)
	{
		_turnMan = m;
	}
	
	public void onDetach(TurnManager m)
	{
		_turnMan = null;
	}
	
	public TurnManager getTurnManager()
	{
		return _turnMan;
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
		 if (o instanceof Flag)
		 {
			 Flag f = (Flag)o;
			 if (f.getTank() == null)
			 {
				 _hasFlag = f;
				 f.setTank(this);
			 }
		}
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
	
	@Override
	public void setPosition(float x, float y, float z)
	{
		super.setPosition(x, y, z);
		_tower.setPosition(x, y, z);
	}
	
	public TankColor getColor()
	{
		return _color;
	}
	
	public void flagScored()
	{
		_score++;
		_hasFlag = null;
	}
	
	public int getScore()
	{
		return _score;
	}
	
	public boolean hasFlag()
	{
		return _hasFlag != null;
	}
	
	public Flag getFlag()
	{
		return _hasFlag;
	}
}

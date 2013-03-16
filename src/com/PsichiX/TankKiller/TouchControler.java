package com.PsichiX.TankKiller;

import com.PsichiX.TankKiller.R;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class TouchControler extends Controler
{
	private boolean _interrupted = false;
	private float[] _touchLoc;
	private float _shotAccum = 0.0f;
	private Sprite _radar;
	private Sprite _radarAim;
	private float _radarPhase = 0.0f;
	
	public TouchControler()
	{
		super();
		Material mat = (Material)MainActivity.app.getAssets().get(R.raw.radar_material, Material.class);
		_radarAim = new Sprite(mat);
		_radarAim.setOrder(-0.25f);
		_radarAim.setSize(2.0f, 2.0f);
		_radarAim.setOffsetFromSize(0.5f, 0.5f);
		_radarAim.setTextureOffset(-0.5f, -0.5f);
		_radarAim.getProperties().setVec("uNorm", new float[]{ 1.0f, 0.0f });
		_radarAim.getProperties().setVec("uCol", new float[]{ 1.0f, 0.0f, 0.0f, 1.0f});
		_radar = new Sprite(mat);
		_radar.setOrder(-0.25f);
		_radar.setSize(2.0f, 2.0f);
		_radar.setOffsetFromSize(0.5f, 0.5f);
		_radar.setTextureOffset(-0.5f, -0.5f);
		_radar.getProperties().setVec("uNorm", new float[]{ 1.0f, 0.0f });
		_radar.getProperties().setVec("uCol", new float[]{ 1.0f, 1.0f, 1.0f, 0.5f });
	}
	
	public void setRange(float v)
	{
		_radar.setScale(v, v);
	}
	
	public void setRangeAim(float v)
	{
		_radarAim.setScale(v, v);
	}
	
	@Override
	public void setTarget(IControlable c)
	{
		super.setTarget(c);
		_interrupted = true;
		if(getTarget() == null && _radar.getScene() != null)
			_radar.getScene().detach(_radar);
		if(getTarget() != null &&
			getTarget() instanceof Tank &&
			((Tank)getTarget()).getScene() != null)
			((Tank)getTarget()).getScene().attach(_radar);
		if(getTarget() == null && _radarAim.getScene() != null)
			_radarAim.getScene().detach(_radarAim);
		if(getTarget() != null &&
			getTarget() instanceof Tank &&
			((Tank)getTarget()).getScene() != null)
			((Tank)getTarget()).getScene().attach(_radarAim);
	}
	
	@Override
	public void onUpdate(float dt)
	{
		if(_touchLoc != null && !_interrupted)
			_shotAccum += dt;
		else
			_shotAccum = 0.0f;
		if(_touchLoc != null && _shotAccum > 0.5f && getTarget() != null)
		{
			Tank tank = (Tank)getTarget();
			if(MathHelper.vecLength(
				_touchLoc[0] - tank.getPositionX(),
				_touchLoc[1] - tank.getPositionY(),
				0.0f) < tank.getRangeAim()
				)
				tank.shot(_touchLoc[0], _touchLoc[1]);
			_interrupted = true;
		}
		if(getTarget() != null)
		{
			Tank tank = (Tank)getTarget();
			_radar.setPosition(
				tank.getPositionX(),
				tank.getPositionY()
				);
			_radarAim.setPosition(
				tank.getPositionX(),
				tank.getPositionY()
				);
			_radarPhase += dt;
			_radar.getProperties().setVec("uNorm", new float[]{
				(float)Math.cos(_radarPhase * 2.0f),
				(float)Math.sin(_radarPhase * 2.0f)
				});
			_radarAim.getProperties().setVec("uNorm", new float[]{
				(float)Math.cos(_radarPhase * 3.0f),
				(float)Math.sin(_radarPhase * 3.0f)
				});
		}
	}
	
	@Override
	public void onInput(Touches ev)
	{
		if(!(getTarget() instanceof Tank))
			return;
		Tank tank = (Tank)getTarget();
		Scene scn = tank.getScene();
		if(scn == null)
			return;
		Camera2D cam = (Camera2D)scn.getCamera();
		
		Touch t = ev.getTouchByState(Touch.State.UP);
		if(t != null)
		{
			if(!_interrupted)
			{
				float[] loc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
				tank.tryMoveToPos(loc[0], loc[1]);
			}
			_interrupted = false;
			_touchLoc= null;
		}
		t = ev.getTouchByState(Touch.State.IDLE);
		if(t != null)
		{
			if(_touchLoc != null)
			{
				float[] curLoc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
				if(MathHelper.vecLength(curLoc[0] - _touchLoc[0], curLoc[1] - _touchLoc[1], 0.0f) > 32.0f)
					_interrupted = true;
			}
		}
		t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			_interrupted = false;
			_touchLoc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			/*tank.setAngle((float)(Math.toDegrees(MathHelper.vecDirectionXY(
					_touchLoc[0] - tank.getPositionX(),
					_touchLoc[1] - tank.getPositionY()
					)) - 90));*/
		}
	}
}

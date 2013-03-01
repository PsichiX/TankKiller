package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class TouchControler extends Controler
{
	private boolean _interrupted = false;
	private float[] _touchLoc;
	private float _shotAccum = 0.0f;
	
	@Override
	public void onUpdate(float dt)
	{
		if(_touchLoc != null && !_interrupted)
			_shotAccum += dt;
		else
			_shotAccum = 0.0f;
		if(_shotAccum > 0.5f && getTarget() instanceof Tank)
		{
			Tank tank = (Tank)getTarget();
			tank.getReceiver().queueCommand(this, "Shot", _touchLoc);
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
				tank.moveToPos(loc[0],loc[1]);
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
				if(MathHelper.vecLength(curLoc[0] - _touchLoc[0], curLoc[1] - _touchLoc[1], 0) > 5)
					_interrupted = true;
			}
		}
		t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			_interrupted = false;
			_touchLoc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f); 
		}
	}
}

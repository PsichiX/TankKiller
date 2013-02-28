package eu.ganymede.tankkiller;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;
import com.PsichiX.XenonCoreDroid.Framework.Actors.*;
import com.PsichiX.XenonCoreDroid.XeUtils.*;
import com.PsichiX.XenonCoreDroid.XeAssets;
import com.PsichiX.XenonCoreDroid.XeApplication.*;

public class TouchControler extends Controler
{
	private int _tid = -1;
	private boolean interrupted = false;
	private float[] touch_loc;
	
	@Override
	public void onInput(Touches ev)
	{
		if(!(getTarget() instanceof Tank))
			return;
		Tank tank = (Tank)getTarget();
		Scene scn = tank.getScene();
		Camera2D cam = (Camera2D)scn.getCamera();
		
		Touch t = ev.getTouchByState(Touch.State.UP);
		if(t != null)
		{
			Log.d("UP", "  up ");
			if(!interrupted)
			{
				float[] loc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
				tank.moveToPos(loc[0],loc[1]);
			}
			interrupted = false;
			touch_loc= null;
		}
		t = ev.getTouchByState(Touch.State.IDLE);
		if(t != null)
		{
			Log.d("IDLE", " idle ");
			if(touch_loc != null)
			{
				float[] curLoc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
				if(MathHelper.vecLength(curLoc[0] - touch_loc[0],curLoc[1] - touch_loc[1], 0) > 5)
				{
					interrupted = true;
				}
			}
		}
		
		t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			Log.d("DOWN", " down ");
			interrupted = false;
			touch_loc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f); 
		}
	}
}

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
	
	@Override
	public void onInput(Touches ev)
	{
		Log.d("TOUCH INPUT","-==-");
//		if(!(getTarget() instanceof Tank))
//			return;
		Tank sw = (Tank)getTarget();
		Scene scn = sw.getScene();
		Camera2D cam = (Camera2D)scn.getCamera();
		
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		if(t != null)
		{
			float[] loc = cam.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			Log.d("TANKU", "");
		}
	}
}

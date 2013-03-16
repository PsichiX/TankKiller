package com.PsichiX.TankKiller;

import android.content.Context;
import android.os.Vibrator;

public class Vibration
{
	private static final long[][] _VIB_LIST = {
		{0, 50},
		{0, 100},
		{0, 200},
		{5, 100, 10, 100}
		};
	private static Vibration _instance;
	private static Vibrator _vib;
	
	public static Vibration getInstance()
	{
		if(_instance == null)
			_instance = new Vibration();
		return _instance;
	};
	
	public static void init(Context context)
	{
		_vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public void vibrate(int vibID)
	{
		if(_vib != null && vibID >= 0 && vibID < _VIB_LIST.length)
			_vib.vibrate(_VIB_LIST[vibID], -1);
	}
}

package com.PsichiX.TankKiller;

import com.PsichiX.XenonCoreDroid.XeApplication.Touches;

public abstract class Popup
{
	private PopupsManager _manager;
	
	public void onShow(PopupsManager man)
	{
		_manager = man;
	}
	
	public void onHide()
	{
		_manager = null;
	}
	
	public boolean onInput(Touches ev)
	{
		return false;
	}
	
	public void onBack()
	{
		if(_manager != null)
			_manager.pop();
	}
	
	public void onUpdate(float dt)
	{
	}
	
	public PopupsManager getOwner()
	{
		return _manager;
	}
}

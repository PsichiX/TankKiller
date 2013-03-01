package eu.ganymede.tankkiller;

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
	
	public PopupsManager getOwner()
	{
		return _manager;
	}
	
	public boolean onInput(Touches ev){ return false; }
}

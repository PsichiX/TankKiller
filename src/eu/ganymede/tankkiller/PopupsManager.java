package eu.ganymede.tankkiller;

import java.util.LinkedList;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Scene;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;

public class PopupsManager
{
	private Scene _scn;
	private LinkedList<Popup> _popups = new LinkedList<Popup>();
	
	public PopupsManager(Scene scn)
	{
		_scn = scn;
	}
	
	protected void finalize() throws Throwable
	{
		release();
		super.finalize();
	}
	
	public void release()
	{
		while(_popups.size() > 0)
			pop();
	}
	
	public void push(Popup p)
	{
		if(_popups.size() > 0)
			_popups.peek().onHide();
		_popups.offer(p);
		p.onShow(this);
	}
	
	public void pop()
	{
		if(_popups.size() > 0)
			_popups.poll().onHide();
		if(_popups.size() > 0)
			_popups.peek().onShow(this);
	}
	
	public int getCount()
	{
		return _popups.size();
	}
	
	public Scene getScene()
	{
		return _scn;
	}
	
	public boolean isShowing()
	{
		return _popups.size() > 0;
	}
	
	public boolean onInput(Touches ev)
	{
		if(_popups.size() > 0)
			return _popups.peek().onInput(ev);
		return false;
	}
}

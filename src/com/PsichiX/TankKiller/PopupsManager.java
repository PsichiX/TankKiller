package com.PsichiX.TankKiller;

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
		if(p == null)
			return;
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
	
	public void pop(Popup p)
	{
		if(p == null)
			return;
		if(_popups.size() <= 0)
			return;
		int idx = _popups.indexOf(p);
		if(idx == _popups.size() - 1)
			pop();
		else
			_popups.remove(idx);
	}
	
	public Popup get(Class<?> c)
	{
		if(c == null)
			return null;
		Popup p = null;
		for(int i = _popups.size() - 1; i >= 0; i--)
		{
			p = _popups.get(i);
			if(p.getClass() == c)
				return p;
		}
		return null;
	}
	
	public boolean has(Class<?> c)
	{
		return get(c) != null;
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
	
	public void onBack()
	{
		if(_popups.size() > 0)
			_popups.peek().onBack();
	}
	
	public void onUpdate(float dt)
	{
		if(_popups.size() > 0)
			_popups.peek().onUpdate(dt);
	}
}

package com.PsichiX.TankKiller;

import com.PsichiX.XenonCoreDroid.Framework.Actors.ActorSprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;

public class Base extends ActorSprite implements ICollidable
{
	private TankColor _color;
	private Text _text;
	
	public Base(Material mat, TankColor color, Text txt)
	{
		super(mat);
		setOrder(-0.1f);
		_color = color;
		_text = txt;
		setFlagsText(0);
	}

	public void onAttach(CollisionManager m) {
		// TODO Auto-generated method stub
		
	}

	public void onDetach(CollisionManager m) {
		// TODO Auto-generated method stub
		
	}

	public CollisionManager getCollisionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getRange() {
		// TODO Auto-generated method stub
		return 0.0f;
	}

	public void setRange(float val) {
		// TODO Auto-generated method stub
		
	}

	public void onCollision(ICollidable o) {
		if(o instanceof Tank)
		{
			Tank t = (Tank) o;
			if(t.getColor() == _color && t.hasFlag())
			{
				t.getFlag().resetPosition();
				t.flagScored();
				setFlagsText(t.getScore());
			}
		}
	}
	
	public TankColor getColor()
	{
		return _color;
	}
	
	public void setFlagsText(int v)
	{
		if(_text == null)
			return;
		_text.build("Flags:\n" + v, null, null,
			Font.Alignment.CENTER, Font.Alignment.MIDDLE,
			1.0f, 1.0f
			);
	}
}

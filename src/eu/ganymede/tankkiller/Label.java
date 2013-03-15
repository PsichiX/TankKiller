package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;

public class Label extends Text
{
	protected Sprite _bg;
	private float _paddingX = 0.0f;
	private float _paddingY = 0.0f;
	private float _minW = 0.0f;
	private float _minH = 0.0f;
	
	public Label(Material bgmat)
	{
		super();
		_bg = new Sprite(bgmat);
	}
	
	@Override
	public void release()
	{
		super.release();
		if(_bg != null && _bg.getScene() != null)
			_bg.getScene().detach(_bg);
		_bg = null;
	}

	@Override
	public void setScene(Scene s)
	{
		super.setScene(s);
		if(_bg != null)
		{
			if(s != null)
				s.attach(_bg);
			else if(_bg.getScene() != null)
				_bg.getScene().detach(_bg);
		}
	}
	
	@Override
	public void build(String text, Font font, Material mat, Font.Alignment halign, Font.Alignment valign, float sclX, float sclY)
	{
		super.build(text, font, mat, halign, valign, sclX, sclY);
		if(_bg != null)
		{
			float w = Math.max(_minW, getWidth() + _paddingX * 2.0f);
			float h = Math.max(_minH, getHeight() + _paddingY * 2.0f);
			float ox = (w - getWidth()) * 0.5f;
			float oy = (h - getHeight()) * 0.5f;
			_bg.setSize(w, h);
			_bg.setOffset(
				getOffsetX() + ox,
				getOffsetY() + oy
				);
		}
	}
	
	@Override
	public void setPosition(float x, float y, float z)
	{
		super.setPosition(x, y, z);
		if(_bg != null)
			_bg.setPosition(x, y, z + 0.001f);
	}
	
	@Override
	public void setScale(float x, float y, float z)
	{
		super.setScale(x, y, z);
		if(_bg != null)
			_bg.setScale(x, y, z);
	}
	
	public void setPadding(float x, float y)
	{
		_paddingX = x;
		_paddingY = y;
	}
	
	public float getPaddingX()
	{
		return _paddingX;
	}
	
	public float getPaddingY()
	{
		return _paddingY;
	}
	
	public void setMinSize(float x, float y)
	{
		_minW = x;
		_minH = y;
	}
	
	public float getMinWidth()
	{
		return _minW;
	}
	
	public float getMinHeight()
	{
		return _minH;
	}
}

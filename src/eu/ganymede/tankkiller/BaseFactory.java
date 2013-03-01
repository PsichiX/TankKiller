package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.XeUtils.MathHelper;

public class BaseFactory
{
	private static BaseFactory _instance;	
	private BaseFactory()
	{
		
	}
	
	public static BaseFactory getFactory()
	{
		if(_instance == null)
			_instance = new BaseFactory();
		return _instance;
	}
	
	public Base createBase(TankColor color)
	{
		Base base = null;
		Material mat;
		Image img;
		switch(color)
		{
		case RED:
		case ORANGE:
		case GREEN:
		case BLUE:
		default:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.base_material, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.base_build, Image.class);
			break;
		}
		base = new Base(mat,color);
		base.setSizeFromImage(img);
		base.setOffsetFromSize(0.5f, 0.6f);
		return base;
	}
}

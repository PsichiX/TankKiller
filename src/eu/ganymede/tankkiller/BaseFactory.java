package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Text;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Font;
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
		Material mat = (Material)MainActivity.app.getAssets().get(R.raw.badaboom_material, Material.class);
		Font fnt = (Font)MainActivity.app.getAssets().get(R.raw.badaboom_font, Font.class);
		Image img = null;
		Text txt = new Text();
		txt.build("", fnt, mat, Font.Alignment.CENTER, Font.Alignment.MIDDLE, 1.0f, 1.0f);
		switch(color)
		{
		case RED:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.red_base_material, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.red_base, Image.class);
			break;
		case ORANGE:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.yellow_base_material, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.yellow_base, Image.class);
			break;
		case GREEN:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.green_base_material, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.green_base, Image.class);
			break;
		case BLUE:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.blue_base_material, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.blue_base, Image.class);
			break;
		}
		base = new Base(mat, color, txt);
		base.setSizeFromImage(img);
		base.setOffsetFromSize(0.5f, 0.5f);
		return base;
	}
}

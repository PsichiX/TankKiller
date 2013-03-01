package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.XeUtils.MathHelper;

public class TankFactory {
	private static TankFactory _instance;
	public static final float AREA_PADDING = 256.0f;
	
	private TankFactory()
	{
		
	}
	
	public static TankFactory getFactory()
	{
		if(_instance == null)
			_instance = new TankFactory();
		return _instance;
	}
	
	public Tank createTank(TankColor color, float areaWidth, float areaHeight)
	{
		Tank tank = null;
		Material mat;
		Material matTower;
		Image imgTower;
		Image img;
		float x = areaWidth * 0.5f;
		float y = areaHeight * 0.5f;
		switch(color)
		{
		case RED:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_red, Material.class);
			matTower = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_tower_red, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.tank_base_red, Image.class);
			imgTower = (Image)MainActivity.app.getAssets().get(R.drawable.tank_tower_red, Image.class);
			x = AREA_PADDING;
			y = AREA_PADDING;
			break;
		case ORANGE:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_orange, Material.class);
			matTower = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_tower_orange, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.tank_base_orange, Image.class);
			imgTower = (Image)MainActivity.app.getAssets().get(R.drawable.tank_tower_orange, Image.class);
			x = areaWidth - AREA_PADDING;
			y = AREA_PADDING;
			break;
		case GREEN:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_green, Material.class);
			matTower = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_tower_green, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.tank_base_green, Image.class);
			imgTower = (Image)MainActivity.app.getAssets().get(R.drawable.tank_tower_green, Image.class);
			x = areaWidth - AREA_PADDING;
			y = areaHeight - AREA_PADDING;
			break;
		case BLUE:
		default:
			mat = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_blue, Material.class);
			matTower = (Material)MainActivity.app.getAssets().get(R.raw.tank_material_tower_blue, Material.class);
			img = (Image)MainActivity.app.getAssets().get(R.drawable.tank_base_blue, Image.class);
			imgTower = (Image)MainActivity.app.getAssets().get(R.drawable.tank_tower_blue, Image.class);
			x = AREA_PADDING;
			y = areaHeight - AREA_PADDING;
			break;
		}
		Sprite tower = new Sprite(matTower);
		tank = new Tank(mat, tower,color, color.toString(), x, y, MathHelper.vecDirectionXY((areaWidth * 0.5f) - x, (areaHeight * 0.5f) - y));
		tower.setSizeFromImage(imgTower);
		tower.setOffsetFromSize(0.5f, 0.75f);
		tank.setSizeFromImage(img);
		tank.setOffsetFromSize(0.5f, 0.6f);
		return tank;
	}
	
	public float[] getTankWSize()
	{
		Image img = (Image)MainActivity.app.getAssets().get(R.drawable.tank_base_red, Image.class);
		float[] size = new float[2];
		size[0] = img.getTexture().getWidth();
		size[1] = img.getTexture().getHeight();
		return size;
	}
}

package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;

public class Utils
{
	public static boolean hitTest(Sprite sprite, float posX, float posY)
	{
		
		return (posX > sprite.getPositionX() - sprite.getOffsetX() && 
				posX < sprite.getPositionX() - sprite.getOffsetX() + sprite.getWidth() &&
				posY > sprite.getPositionY() - sprite.getOffsetY() &&
				posY < sprite.getPositionY() - sprite.getOffsetY() + sprite.getHeight());
	}
}

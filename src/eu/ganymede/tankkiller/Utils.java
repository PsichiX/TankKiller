package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.*;

public class Utils
{
	public static boolean hitTest(Sprite sprite, float posX, float posY)
	{
		return (posX > sprite.getPositionX() - sprite.getOffsetX() && 
				posX < sprite.getPositionX() - sprite.getOffsetX() + sprite.getWidth() &&
				posY > sprite.getPositionY() - sprite.getOffsetY() &&
				posY < sprite.getPositionY() - sprite.getOffsetY() + sprite.getHeight());
	}
	
	public static boolean hitTest(Text text, float posX, float posY)
	{
		return (posX > text.getPositionX() - text.getOffsetX() && 
				posX < text.getPositionX() - text.getOffsetX() + text.getWidth() &&
				posY > text.getPositionY() - text.getOffsetY() &&
				posY < text.getPositionY() - text.getOffsetY() + text.getHeight());
	}
}

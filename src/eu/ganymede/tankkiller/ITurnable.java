package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Actors.IControlable;

public interface ITurnable extends IControlable
{
	public void onTurnChanged(boolean my);
}

package eu.ganymede.tankkiller;

import com.PsichiX.XenonCoreDroid.Framework.Actors.IControlable;

public interface ITurnable extends IControlable
{
	public void onAttach(TurnManager man);
	public void onDetach(TurnManager man);
	public TurnManager getTurnManager();
	public void onTurnChanged(boolean my);
}

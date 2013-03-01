package eu.ganymede.tankkiller;

import java.util.Dictionary;
import java.util.Hashtable;

import android.util.Log;

import com.PsichiX.XenonCoreDroid.Framework.Graphics.Camera2D;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Font;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Image;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Material;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Scene;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Sprite;
import com.PsichiX.XenonCoreDroid.Framework.Graphics.Text;
import com.PsichiX.XenonCoreDroid.XeApplication.State;
import com.PsichiX.XenonCoreDroid.XeApplication.Touch;
import com.PsichiX.XenonCoreDroid.XeApplication.Touches;

public class MenuState extends State {
	

	private Camera2D _camHud;
	private Scene _scnHud;
	
	Sprite background;
	
	private Text _chooseTankText;
	private Font _font;
	private Material _fontMaterial;
	
	private Sprite[] _tanks;
	
	private boolean[] _tanksChoosed;
	TankColor[] colors = new TankColor[4];
	
	@Override
	public void onEnter()
	{	
		_scnHud = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_camHud = (Camera2D)_scnHud.getCamera();
		_camHud.setViewPosition(
			_camHud.getViewWidth() * 0.5f,
			_camHud.getViewHeight() * 0.5f
			);
		
//		Material matBg = (Material) getApplication().getAssets().get(R.raw.menu_bg_mat, Material.class);
//		Image bgImg = (Image) getApplication().getAssets().get(R.drawable.menu_bg, Image.class);
//		
//		background = new Sprite(matBg);
//		background.setSize(cam.getViewWidth(), cam.getViewHeight());
//		background.setTextureScaleFromImageAspect(bgImg, true);
		
		_font = (Font)getApplication().getAssets().get(R.raw.badaboom_font, Font.class);
		_fontMaterial = (Material)getApplication().getAssets().get(R.raw.badaboom_material, Material.class);
		
		_chooseTankText = new Text();
		_chooseTankText.build("Choose tanks:", _font, _fontMaterial, Font.Alignment.LEFT, Font.Alignment.TOP, 1.0f, 1.0f);
		_scnHud.attach(_chooseTankText);
		
		_tanks = new Sprite[4];
		_tanksChoosed = new boolean[4];
		
		createTanks();
	}
	
	@Override
	public void onReload()
	{
		_camHud.setViewPosition(
				_camHud.getViewWidth() * 0.5f,
				_camHud.getViewHeight() * 0.5f
				);
	}

	@Override
	public void onUpdate()
	{
		float dt = getApplication().getTimer().getDeltaTime() * 0.001f;
		_scnHud.update(dt);
	}
	
	
	@Override
	public void onInput(Touches ev)
	{
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		
		
		if(t!= null){
			float[] loc = _scnHud.getCamera().convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			
			boolean tankHit = false;
			for(int i = 0; i < _tanks.length; i++)
			{
				if(hitTest(_tanks[i], loc[0], loc[1]))
				{
					tankHit = true;
					Log.d("TANK!",""+i);
					_tanksChoosed[i] = !_tanksChoosed[i];
				}
			}
			
			if(!tankHit)
			{
				getApplication().pushState(new GameState(prepearePlayerInfos()));	
			}
		}
	}
	
	@Override
	public void onExit()
	{
		_scnHud.releaseAll();
	}
	
	private void createTanks()
	{
		colors[0] = TankColor.RED;
		colors[1] = TankColor.ORANGE;
		colors[2] = TankColor.GREEN;
		colors[3] = TankColor.BLUE;
		
		float startX = 50.0f;
		float startY = 50.0f;
		
		for(int i = 0; i < 4; i++)
		{
			_tanks[i] = TankFactory.getFactory().createTank(colors[i], 0.0f, 0.0f);
			_scnHud.attach(_tanks[i]);
			_tanks[i].setPosition(startX + _tanks[i].getWidth() * i + 50, startY + _tanks[i].getHeight());
			_tanksChoosed[i] = false;
		}
	}
	
	private boolean hitTest(Sprite sprite, float posX, float posY)
	{
		
		boolean spriteHit = (posX > sprite.getPositionX() - sprite.getOffsetX() && 
				posX < sprite.getPositionX() - sprite.getOffsetX() + sprite.getWidth() &&
				posY > sprite.getPositionY() - sprite.getOffsetY() &&
				posY < sprite.getPositionY() - sprite.getOffsetY() + sprite.getHeight());
		
		return spriteHit;
	}
	
	private PlayerInfo[] prepearePlayerInfos()
	{
		int ctn = 0;
		for(int i = 0; i < _tanksChoosed.length; i++)
		{
			if(_tanksChoosed[i])
				ctn++;
		}
		
		PlayerInfo[] pis = new PlayerInfo[ctn];
		int j = 0;
		for(int i = 0; i < _tanksChoosed.length; i++)
		{
			if(_tanksChoosed[i])
			{
				pis[j] = new PlayerInfo(colors[i]);
				j++;
			}
		}
		return pis;
	}
}

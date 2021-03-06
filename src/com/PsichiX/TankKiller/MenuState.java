package com.PsichiX.TankKiller;

import com.PsichiX.TankKiller.R;
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
import com.PsichiX.XenonCoreDroid.XeUtils.Message;

public class MenuState extends State
{
	private Camera2D _camHud;
	private Scene _scnHud;
	private Sprite _bg;
	private Text _chooseTankText;
	private Font _font;
	private Material _fontMaterial;
	private Sprite _btnPlay;
	private Tank[] _tanks;
	private Sprite[] _shields;
	private boolean[] _tanksChoosed;
	TankColor[] colors = new TankColor[4];
	
	@Override
	public void onEnter()
	{
		//getApplication().getPhoton().setRenderMode(XePhoton.RenderMode.STREAM, true);
		/*WaitingScreen ws = (WaitingScreen)getApplication().getAssets().get(
			R.raw.waiting_screen,
			WaitingScreen.class
			);
		ws.setOrder(-0.9f);
		ws.start();*/
		
		getApplication().getPhoton().getRenderer().setClearBackground(true, 0.0f, 0.0f, 0.0f, 1.0f);
		
		_scnHud = (Scene)getApplication().getAssets().get(R.raw.hud_scene, Scene.class);
		_camHud = (Camera2D)_scnHud.getCamera();
		_camHud.setViewPosition(
			_camHud.getViewWidth() * 0.5f,
			_camHud.getViewHeight() * 0.5f
			);
		
		Material mat = (Material)getApplication().getAssets().get(R.raw.popup_material, Material.class);
		_bg = new Sprite(mat);
		_bg.setSize(_camHud.getViewWidth(), _camHud.getViewHeight());
		_scnHud.attach(_bg);
		_font = (Font)getApplication().getAssets().get(R.raw.badaboom_font, Font.class);
		_fontMaterial = (Material)getApplication().getAssets().get(R.raw.badaboom_material, Material.class);
		
		_chooseTankText = new Text();
		_chooseTankText.build("Select players", _font, _fontMaterial, Font.Alignment.CENTER, Font.Alignment.TOP, 1.0f, 1.0f);
		_chooseTankText.setPosition(_camHud.getViewWidth() * 0.5f, 24.0f);
		_scnHud.attach(_chooseTankText);
		
		mat = (Material)getApplication().getAssets().get(R.raw.play_btn_material, Material.class);
		Image img = (Image)getApplication().getAssets().get(R.drawable.btn_play, Image.class);
		
		_btnPlay = new Sprite(mat);
		_btnPlay.setSizeFromImage(img, 0.75f);
		_btnPlay.setOffsetFromSize(0.5f, 0.5f);
		_scnHud.attach(_btnPlay);
		
		_tanks = new Tank[4];
		_shields = new Sprite[4];
		_tanksChoosed = new boolean[4];
		
		createTanks();
		
		//ws.stop();
		//getApplication().getPhoton().setRenderMode(XePhoton.RenderMode.QUEUE, true);
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
		//float dt = getApplication().getTimer().getDeltaTime() * 0.001f;
		float dt = 1.0f / 30.0f;
		_scnHud.update(dt);
	}
	
	
	@Override
	public void onInput(Touches ev)
	{
		Touch t = ev.getTouchByState(Touch.State.DOWN);
		
		if(t != null)
		{
			float[] loc = _camHud.convertLocationScreenToWorld(t.getX(), t.getY(), -1.0f);
			
			boolean tankHit = false;
			for(int i = 0; i < _tanks.length; i++)
			{
				if(Utils.hitTest(_tanks[i], loc[0], loc[1]))
				{
					tankHit = true;
					_tanksChoosed[i] = !_tanksChoosed[i];
					_shields[i].setVisible(_tanksChoosed[i]);
				}
			}
			
//			if(!tankHit)
//			{
//				PlayerInfo[] infos = prepearePlayerInfos();
//				if(infos.length > 1)
//					getApplication().pushState(new GameState(infos));
//				else
//					Message.alert(getApplication().getContext(), "WARNING!", "SELECT MORE TANKS!", "OK", null);
//			}
			if(!tankHit && Utils.hitTest(_btnPlay, loc[0], loc[1]))
			{
				PlayerInfo[] infos = prepearePlayerInfos();
				if(infos.length > 1)
					getApplication().pushState(new GameState(infos));
				else
					Message.alert(getApplication().getContext(), "WARNING!", "SELECT MORE TANKS!", "OK", null);
			}
		}
	}
	
	@Override
	public void onExit()
	{
		_scnHud.releaseAll();
		getApplication().getPhoton().unregisterDrawCalls();
	}
	
	private void createTanks()
	{
		colors[0] = TankColor.RED;
		colors[1] = TankColor.ORANGE;
		colors[2] = TankColor.GREEN;
		colors[3] = TankColor.BLUE;
		
		final float offset = 128.0f;
		final float centerX = _camHud.getViewWidth() * 0.5f;
		final float centerY = (_camHud.getViewHeight() * 0.5f) + 32.0f;
		
		for(int i = 0; i < 4; i++)
		{
			_tanks[i] = TankFactory.getFactory().createTank(colors[i], 0.0f, 0.0f, false);
			_scnHud.attach(_tanks[i]);
			_tanksChoosed[i] = false;
			_shields[i] = createShield();
			_shields[i].setVisible(false);
			_scnHud.attach(_shields[i]);
		}
		_tanks[0].setPosition(centerX + ( -_tanks[0].getOffsetX() + _tanks[0].getWidth() * 0.5f) - offset, centerY + (-_tanks[0].getOffsetY() + _tanks[0].getHeight()* 0.5f) - offset);
		_tanks[1].setPosition(centerX + ( -_tanks[0].getOffsetX() + _tanks[0].getWidth() * 0.5f) + offset, centerY + (-_tanks[0].getOffsetY() + _tanks[0].getHeight()* 0.5f) - offset);
		_tanks[2].setPosition(centerX + ( -_tanks[0].getOffsetX() + _tanks[0].getWidth() * 0.5f) + offset, centerY + (-_tanks[0].getOffsetY() + _tanks[0].getHeight()* 0.5f) + offset);
		_tanks[3].setPosition(centerX + ( -_tanks[0].getOffsetX() + _tanks[0].getWidth() * 0.5f) - offset, centerY + (-_tanks[0].getOffsetY() + _tanks[0].getHeight()* 0.5f) + offset);
		
		for(int i = 0; i < 4; i++)
		{
			_shields[i].setPosition(
				_tanks[i].getPositionX() - _tanks[i].getOffsetX() + _tanks[i].getWidth() * 0.5f,
				_tanks[i].getPositionY() - _tanks[i].getOffsetY() + _tanks[i].getHeight() * 0.5f
				);
		}
		
		_btnPlay.setPosition(centerX, centerY);
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
	
	private Sprite createShield()
	{
		Material mat = (Material)MainActivity.app.getAssets().get(R.raw.life_shield_material, Material.class);
		Image img = (Image)MainActivity.app.getAssets().get(R.drawable.life_shield, Image.class);
		
		Sprite lifeShield = new Sprite(mat);
		lifeShield.setSizeFromImage(img, 0.6f);
		lifeShield.setOffsetFromSize(0.5f, 0.5f);
		return lifeShield;
	}
}

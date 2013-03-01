package eu.ganymede.tankkiller;

import java.util.ArrayList;

import com.PsichiX.XenonCoreDroid.Framework.Actors.ActorsManager;
import com.PsichiX.XenonCoreDroid.XeUtils.CommandQueue;

public class TurnManager
{
	private ArrayList<ITurnable> _players = new ArrayList<ITurnable>();
	private TouchControler _controler = new TouchControler();
	private CommandQueue _receiver;
	private int _turnPlayer = -1;
	private boolean _isRunning = false;
	private float _timePerTurn = 10.0f;
	private float _timeTurnLeft = 0.0f;
	
	public TurnManager(ActorsManager acts)
	{
		acts.attach(_controler);
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		release();
	}
	
	public void release()
	{
		clearPlayers();
		if(_controler != null && _controler.getManager() != null)
			_controler.getManager().detach(_controler);
	}
	
	public void setReceiver(CommandQueue cmds)
	{
		_receiver = cmds;
	}
	
	public void addPlayer(ITurnable p)
	{
		if(_isRunning)
			return;
		_players.add(p);
	}
	
	public void removePlayer(ITurnable p)
	{
		if(_isRunning)
			return;
		_players.remove(p);
	}
	
	public void clearPlayers()
	{
		stop();
		_players.clear();
	}
	
	public void resetTimer()
	{
		_timeTurnLeft = _timePerTurn;
	}
	
	public float getTimeLeft()
	{
		return _timeTurnLeft;
	}
	
	public void start()
	{
		if(_isRunning || _players.size() < 2)
			return;
		_isRunning = true;
		nextPlayer();
		resetTimer();
	}
	
	public void stop()
	{
		if(!_isRunning)
			return;
		_isRunning = false;
		_turnPlayer = -1;
		_controler.setTarget(null);
		resetTimer();
	}
	
	private void nextPlayer()
	{
		if(!_isRunning)
			return;
		ITurnable c = (ITurnable)_controler.getTarget();
		if(c != null)
			c.onTurnChanged(false);
		_turnPlayer = (_turnPlayer + 1) % _players.size();
		c = _players.get(_turnPlayer);
		if(c != null)
			c.onTurnChanged(true);
		_controler.setTarget(c);
		if(_receiver != null)
			_receiver.queueCommand(this, "NextPlayer", c);
	}
	
	public void update(float dt)
	{
		if(!_isRunning)
			return;
		_timeTurnLeft -= dt;
		if(_timeTurnLeft <= 0.0f)
		{
			nextPlayer();
			resetTimer();
		}
	}
}

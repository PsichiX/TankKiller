package eu.ganymede.tankkiller;

import java.util.ArrayList;

public class TurnManager
{
	private ArrayList<ITurnable> _players = new ArrayList<ITurnable>();
	private TouchControler _controler;
	private int _turnPlayer = -1;
	private boolean _isRunning = false;
	private boolean _isWaiting = false;
	
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
	
	public void start()
	{
		if(_isRunning || _players.size() < 2)
			return;
		_isRunning = true;
		_isWaiting = true;
		nextPlayer();
	}
	
	public void stop()
	{
		if(!_isRunning)
			return;
		_isRunning = false;
		_isWaiting = false;
		_turnPlayer = -1;
		_controler.setTarget(null);
	}
	
	private void nextPlayer()
	{
		if(!_isRunning || _isWaiting)
			return;
		ITurnable c = (ITurnable)_controler.getTarget();
		if(c != null)
			c.onTurnChanged(false);
		_turnPlayer = (_turnPlayer + 1) % _players.size();
		c = _players.get(_turnPlayer);
		if(c != null)
			c.onTurnChanged(true);
		_controler.setTarget(c);
		_isWaiting = c != null;
	}
	
	
}

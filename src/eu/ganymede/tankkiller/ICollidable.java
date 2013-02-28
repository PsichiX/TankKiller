package eu.ganymede.tankkiller;

public interface ICollidable
{
	public void onAttach(CollisionManager m);
	public void onDetach(CollisionManager m);
	public CollisionManager getCollisionManager();
	public float getRange();
	public void setRange(float val);
	public float getPositionX();
	public float getPositionY();
	public void setPosition(float x, float y);
	public void onCollision(ICollidable o);
}

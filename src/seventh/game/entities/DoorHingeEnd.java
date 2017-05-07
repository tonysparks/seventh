package seventh.game.entities;

import seventh.math.Vector2f;

public abstract class DoorHingeEnd {
	public abstract float getClosedOrientation();
	public abstract Vector2f getRearHandlePosition(Vector2f pos, Vector2f rearHandlePos);
	public abstract Vector2f getRearHingePosition(Vector2f hingePos, Vector2f facing, Vector2f rearHingePos);
	public abstract void doorOpen(Door door,Entity ent);
}

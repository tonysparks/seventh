package seventh.game.entities;

import seventh.math.Vector2f;

public class DoorEastEnd extends DoorHingeEnd{

	@Override
	public float getClosedOrientation() {
		return (float)Math.toRadians(180);
	}
	@Override
	public Vector2f getRearHandlePosition(Vector2f pos, Vector2f rearHandlePos) {
		rearHandlePos.set(pos);
		rearHandlePos.y += Door.getDoorWidth();
		return rearHandlePos;
	}

	@Override
	public Vector2f getRearHingePosition(Vector2f hingePos, Vector2f facing, Vector2f rearHingePos) {
		Vector2f hingeFacing = new Vector2f();
		Vector2f.Vector2fPerpendicular(facing, hingeFacing);
        Vector2f.Vector2fMA(hingePos, hingeFacing, Door.getDoorWidth(), rearHingePos);
		return rearHingePos;
	}

}

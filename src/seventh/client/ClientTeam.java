/*
 * see license.txt 
 */
package seventh.client;

/**
 * Represents the teams
 * 
 * @author Tony
 *
 */
public enum ClientTeam {
	NONE(0, 0xff443366, "Spectator"),
	ALLIES(2, 0xff8888ff, "Allies"),
	AXIS(4, 0xffff8888, "Axis"),
	;
	
	private int id;
	private int color;
	private String name;
	/**
	 * 
	 */
	private ClientTeam(int id, int color, String name) {
		this.id = id;
		this.color = color;
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public static ClientTeam fromId(int id) {
		for(ClientTeam t : values()) {
			if(t.id==id) {
				return t;
			}
		}
		return NONE;
	}
}

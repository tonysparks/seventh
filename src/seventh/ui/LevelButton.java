/*
**************************************************************************************
*Myriad Engine                                                                       *
*Copyright (C) 2006-2007, 5d Studios (www.5d-Studios.com)                            *
*                                                                                    *
*This library is free software; you can redistribute it and/or                       *
*modify it under the terms of the GNU Lesser General Public                          *
*License as published by the Free Software Foundation; either                        *
*version 2.1 of the License, or (at your option) any later version.                  *
*                                                                                    *
*This library is distributed in the hope that it will be useful,                     *
*but WITHOUT ANY WARRANTY; without even the implied warranty of                      *
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU                   *
*Lesser General Public License for more details.                                     *
*                                                                                    *
*You should have received a copy of the GNU Lesser General Public                    *
*License along with this library; if not, write to the Free Software                 *
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA      *
**************************************************************************************
*/
package seventh.ui;



/**
 * Represents a Level Button.
 * 
 * @author Tony
 *
 */
public class LevelButton extends Button {
	
	/**
	 * Label
	 */
	private Label label;
	
	/**
	 * If its highlighted
	 */
	private boolean highlighted;
	
	/**
	 * If the player has completed this level
	 */
	private boolean completed;
	
	/**
	 * Score if available
	 */
	private int score;		
	
	/**
	 * The levels name
	 */
	private String levelName;

	/**
	 * @param button
	 */
	public LevelButton() {
//		this.button = button;	
		this.label = new Label();		
		
//		this.addWidget(this.button);
		this.addWidget(this.label);
	}

		
	/**
	 * @return the highlighted
	 */
	public boolean isHighlighted() {
		return highlighted;
	}

	/**
	 * @param highlighted the highlighted to set
	 */
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	/**
	 * @return the completed
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the button
	 */
//	public Button getButton() {
//		return button;
//	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}


	/**
	 * @return the levelName
	 */
	public String getLevelName() {
		return levelName;
	}


	/**
	 * @param levelName the levelName to set
	 */
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}	
	
	
	
}

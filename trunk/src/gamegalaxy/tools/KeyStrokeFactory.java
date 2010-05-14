package gamegalaxy.tools;

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gjSet.
 *  
 *  gjSet is Copyright 2008, 2009 Joyce Murton
 *  
 *  The Set Game, card design, and basic game mechanics of the Set Game are
 *  registered trademarks of Set Enterprises. 
 *  
 *  This project is in no way affiliated with Set Enterprises, 
 *  but the authors of gjSet are very grateful for
 *  them creating such an excellent card game.
 *  
 *  gjSet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  gjSet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with gjSet.  If not, see <http://www.gnu.org/licenses/>.
 */


import javax.swing.KeyStroke;

/**
 * This program is being written to work on Windows, Linux, and Mac OS X.  Depending on which system
 * is actually being used, the keyboard shortcuts will be different in places.  This interface is used
 * to fix that problem.  The UI will pull shortcuts from this interface whenever they are needed.
 * Which class is set to implement this interface will determine which operating system's keystrokes to
 * provide.
 * 
 * @see GeneralKeyStrokeFactory
 * @see MacKeyStrokeFactory
 */
public interface KeyStrokeFactory
{

	/**
	 * 
	 * Return the {@link KeyStroke} that will cause a new game to start.
	 *
	 * @return The KeyStroke that starts a new game.
	 */
	public KeyStroke getNewGameAcceleratorKeyStroke();

	/**
	 * 
	 * Return the {@link KeyStroke} that will cause a player to be added to the game.
	 *
	 * @return The KeyStroke for adding a new player.
	 */
	public KeyStroke getAddPlayerAcceleratorKeyStroke();	
	
	/**
	 * 
	 * Return the {@link KeyStroke} that will quit the game.
	 *
	 * @return The KeyStroke that will quit the game.
	 */
	public KeyStroke getExitGameAcceleratorKeyStroke();
	
	public KeyStroke getUndoTurnAcceleratorKeyStroke();

}

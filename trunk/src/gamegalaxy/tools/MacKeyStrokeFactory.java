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


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * Implements the {@link KeyStrokeFactory} to create {@link KeyStroke} objects for the Mac OS X system.
 */
public class MacKeyStrokeFactory implements KeyStrokeFactory
{

	/**
	 * 
	 * Return the {@link KeyStroke} that will cause a new game to start in Mac OS X.
	 *
	 * @return The {@link KeyStroke} that will cause a new game to start in Mac OS X.
	 * @see gjset.gui.KeyStrokeFactory#getNewGameAcceleratorKeyStroke()
	 */
	public KeyStroke getNewGameAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK);
	}

	/**
	 * 
	 * Return the {@link KeyStroke} that will cause a player to be added to the game for Mac OS X.
	 *
	 * @return The {@link KeyStroke} that will cause a player to be added to the game for Mac OS X.
	 * @see gjset.gui.KeyStrokeFactory#getAddPlayerAcceleratorKeyStroke()
	 */
	public KeyStroke getAddPlayerAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.META_MASK);
	}	
	
	/**
	 * 
	 * Return the {@link KeyStroke} that will quit the game in Mac OS X.
	 *
	 * @return The {@link KeyStroke} that will quit the game in Mac OS X.
	 * @see gjset.gui.KeyStrokeFactory#getExitGameAcceleratorKeyStroke()
	 */
	public KeyStroke getExitGameAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.META_MASK);
	}
	
    /**
     * Return the (@link KeyStroke} that will undo the move in Mac OS X	
     */
	public KeyStroke getUndoTurnAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK);
	}

}

package gamegalaxy.tools;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

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

/**
 * Implements the {@link KeyStrokeFactory} with {@link KeyStroke} objects that will work for Linux and Windows.
 * 
 * @author Joyce Murton
 * @author Andrea Kilpatrick
 * @see KeyStrokeFactory
 */
public class GeneralKeyStrokeFactory implements KeyStrokeFactory
{
	/**
	 * 
	 * Return the {@link KeyStroke} that will quit the game in Linux or Windows.
	 *
	 * @author Joyce Murton
	 * @return The {@link KeyStroke} that will quit the game in Linux or Windows.
	 * @see gjset.gui.KeyStrokeFactory#getExitGameAcceleratorKeyStroke()
	 */
	public KeyStroke getExitGameAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK);
	}
	
	/**
	 * 
	 * Return the {@link KeyStroke} that will cause a player to be added to the game in Linux or Windows.
	 *
	 * @author Andrea Kilpatrick
	 * @return The {@link KeyStroke} that will cause a player to be added to the game in Linux or Windows.
	 * @see gjset.gui.KeyStrokeFactory#getAddPlayerAcceleratorKeyStroke()
	 */
	public KeyStroke getAddPlayerAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK);
	}

	/**
	 * 
	 * Return the {@link KeyStroke} that will quit the game in Linux or Windows.
	 *
	 * @author Joyce Murton
	 * @return The {@link KeyStroke} that will quit the game in Linux or Windows.
	 * @see gjset.gui.KeyStrokeFactory#getNewGameAcceleratorKeyStroke()
	 */
	public KeyStroke getNewGameAcceleratorKeyStroke()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK);
	}

}

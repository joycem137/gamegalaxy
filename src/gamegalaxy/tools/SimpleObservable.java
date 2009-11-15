
/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gjSet.
 *  
 *  gjSet is Copyright 2008-2009 Joyce Murton
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
package gamegalaxy.tools;

import java.util.Observable;

/**
 * class used to call <code>setChanged</code> whenever <code>notifyObservers</code> gets called.
 * 
 *
 */
public class SimpleObservable extends Observable
{
	public void notifyObservers()
	{
		super.setChanged();
		super.notifyObservers();
	}
}
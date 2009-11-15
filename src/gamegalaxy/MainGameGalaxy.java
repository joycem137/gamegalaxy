/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gamegalaxy.
 *  
 *  gamegalaxy is Copyright 2009 Joyce Murton
 *  
 *  Arimaa and other content here copyright their respective copyright holders.
 *  
 *  gamegalaxy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  gamegalaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with gamegalaxy.  If not, see <http://www.gnu.org/licenses/>.
 */

package gamegalaxy;

import gamegalaxy.games.arimaa.data.GameBoardData;
import gamegalaxy.games.arimaa.gui.BoardDisplay;
import gamegalaxy.gui.ApplicationFrame;

/**
 * 
 */
public class MainGameGalaxy
{

	/**
	 * TODO: Describe method
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		GameBoardData gameBoard = new GameBoardData();
		BoardDisplay boardDisplay = new BoardDisplay(gameBoard);
		
		new ApplicationFrame(boardDisplay);
	}

}

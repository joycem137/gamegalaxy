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

import gamegalaxy.arimaa.data.GameBoardData;
import gamegalaxy.arimaa.gui.BoardDisplay;

import java.awt.BorderLayout;

import javax.swing.JFrame;

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
		
		JFrame frame = new JFrame("Arimaa");

		//Create the main window.
		frame.setSize(1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		frame.add(boardDisplay);
		
		frame.setVisible(true);
	}

}

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

package gamegalaxy.arimaa.gui;

import gamegalaxy.arimaa.data.GameBoardData;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * This class contains all the information for displaying the Arimaa board.
 */
public class BoardDisplay extends JPanel implements Observer
{
	private SpaceDisplay spaces[][];
	private GameBoardData	board;
	
	/**
	 * 
	 * Create a display for the board.
	 *
	 */
	public BoardDisplay(GameBoardData board)
	{
		super();
		
		//Create our spaces array.
		spaces = new SpaceDisplay[8][8];
		setLayout(new GridLayout(8, 8));
		
		//Create our spaces
		for(int r = 0; r < 8; r++)
		{
			for(int c = 0; c < 8; c++)
			{
				spaces[r][c] = new SpaceDisplay();
				add(spaces[r][c]);
			}
		}
		
		//Store a link to the board
		this.board = board;
		
		//Observe the board
		board.addObserver(this);
		
		//Draw the board
		drawBoard();
	}
	
	/**
	 * TODO: Describe method
	 */
	private void drawBoard()
	{
		//Update all of our objects.
		for(int r = 0; r < 8; r++)
		{
			for(int c = 0; c < 8; c++)
			{
				spaces[r][c].updateData(board.getDataAt(r,c));
			}
		}
		
		repaint();
	}

	/**
	 * Redraw the board based on the latest data.
	 *
	 * @param o The new board
	 * @param arg null.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		drawBoard();
	}
}

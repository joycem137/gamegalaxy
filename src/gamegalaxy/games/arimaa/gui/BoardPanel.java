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

package gamegalaxy.games.arimaa.gui;

import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.tools.ResourceLoader;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

/**
 * This class contains all the information for displaying the Arimaa board.
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements PieceHolder
{
	//Store the background image
	private Image backgroundImage;
	
	//Store some useful contents about the nature of the board.
	private final static int BORDER_WIDTH = 27;
	private final static int SPACE_WIDTH = 59;
	private final static int SPACE_HEIGHT = 59;
	
	//Get the location of the upper left square:
	private static final int firstSquareX = BORDER_WIDTH + 1;
	private static final int firstSquareY = BORDER_WIDTH + 1;
	
	/**
	 * 
	 * Create a display for the board.
	 *
	 */
	public BoardPanel(ResourceLoader loader)
	{
		backgroundImage = loader.getResource("BoardBackground");

		setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
	}
	
	/**
	 * 
	 * Draw the board.
	 *
	 * @param g The graphics context for drawing the board.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		//Draw the background
		g.drawImage(backgroundImage, 0, 0, this);
	}

	/**
	 * Implements the dropPiece method in the {@link PieceHolder} interface.
	 * 
	 * This method currently figures out which square the user was attempting to drag
	 * the piece to and puts the piece in that square.
	 *
	 * @param piecePanel The piece to place on the board.
	 * @see gamegalaxy.games.arimaa.gui.PieceHolder#dropPiece(gamegalaxy.games.arimaa.gui.PiecePanel)
	 */
	public void dropPiece(PiecePanel piecePanel)
	{	
		//Get the piece's position relative to the upper left corner of this component.
		int relativeX = piecePanel.getX() - getX();
		int relativeY = piecePanel.getY() - getY();
		
		//Now find the relative position of the center of the piece:
		int centerX = relativeX + SPACE_WIDTH / 2;
		int centerY = relativeY + SPACE_HEIGHT / 2;
		
		BoardPosition space = identifyBoardPosition(centerX, centerY);
		
		//And now find the X,Y coordinates of the new row, col.
		int newX = space.getCol() * SPACE_WIDTH + firstSquareX;
		int newY = space.getRow() * SPACE_HEIGHT + firstSquareY;
		
		//Assign the piece to its new location, since we're not doing any validation yet.
		piecePanel.setLocation(getX() + newX, getY() + newY);
		
		//Assign the new holder.
		piecePanel.setHolder(this);
	}

	/**
	 * Implements the removePiece interface for the {@link PieceHolder} interface.
	 * 
	 * Currently just instructs the piece to delete its holder.
	 *
	 * @param piecePanel
	 * @see gamegalaxy.games.arimaa.gui.PieceHolder#removePiece(gamegalaxy.games.arimaa.gui.PiecePanel)
	 */
	public void removePiece(PiecePanel piecePanel)
	{
		piecePanel.removeHolder();
	}

	/**
	 * TODO: Describe method
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public BoardPosition identifyBoardPosition(int x, int y)
	{
		//Find out what row and col this piece is near:
		int col = (x - firstSquareX) / SPACE_WIDTH;
		int row = (y - firstSquareY) / SPACE_HEIGHT;
		
		//Correct for boundary conditions
		col = Math.max(0, Math.min(7, col));
		row = Math.max(0, Math.min(7, row));
		
		return new BoardPosition(row, col);
	}
}

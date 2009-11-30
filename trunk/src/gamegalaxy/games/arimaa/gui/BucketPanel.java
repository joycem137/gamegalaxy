/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gamegalaxy.
 *  
 *  gamegalaxy is Copyright 2009 Joyce Murton and Andrea Kilpatrick
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

import gamegalaxy.tools.ResourceLoader;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Stores all of the code necessary for displaying the "buckets" on the screen.  These
 * serve two purposes in the game.  They are used at the beginning of the game to show
 * what pieces are still left to arrange on the board.  And they are used during the game
 * to store captured pieces from the opponent.
 */
@SuppressWarnings("serial")
public class BucketPanel extends JPanel implements PieceHolder
{
	//Store the background image for the bucket.
	private Image	backgroundImage;
	private int		numberOfPieces;
	
	private final static int SPACE_WIDTH = 59;
	private final static int SPACE_HEIGHT = 59;
	private final static int BORDER_WIDTH = 16;

	public BucketPanel(ResourceLoader loader)
	{
		backgroundImage = loader.getResource("BucketBackground");
		setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
		numberOfPieces = 0;
	}
	
	/**
	 * Paint the bucket.  This should display any extra elements within the bucket.
	 *
	 *	@param g The graphics context for drawing this panel
	 */
	public void paintComponent(Graphics g)
	{
		//Draw the background
		g.drawImage(backgroundImage, 0, 0, this);
	}

	/**
	 * Implements the dropPiece method of the {@link PieceHolder} interface.  Currently
	 * does not do any validation and just drops the piece here.
	 *
	 * @param piecePanel
	 * @see gamegalaxy.games.arimaa.gui.PieceHolder#dropPiece(gamegalaxy.games.arimaa.gui.PiecePanel)
	 */
	public void dropPiece(PiecePanel piecePanel)
	{
		int baseX = getX() + BORDER_WIDTH;
		int baseY = getY() + BORDER_WIDTH;
		int col = numberOfPieces / 8;
		int row = numberOfPieces % 8;
		piecePanel.setLocation(baseX + col * SPACE_WIDTH, baseY + row * SPACE_HEIGHT);
		piecePanel.setHolder(this);
		numberOfPieces += 1;
	}

	/**
	 * Implements the remove piece method of the {@link PieceHolder} interface.  Currently
	 * does not do anything except tell the piece to remove this object as its holder.
	 *
	 * @param piecePanel
	 * @see gamegalaxy.games.arimaa.gui.PieceHolder#removePiece(gamegalaxy.games.arimaa.gui.PiecePanel)
	 */
	public void removePiece(PiecePanel piecePanel)
	{
		piecePanel.removeHolder();
		numberOfPieces -= 1;
	}

	public int numberOfPieces()
	{
		return numberOfPieces;
	}

	/**
	 * reset the number of pieces in the bucket to 0.
	 *
	 */
	public void resetCount()
	{
		numberOfPieces = 0;
	}	
}

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
	private final static int BORDER_WIDTH = 18;

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
		piecePanel.setLocation(baseX + col*SPACE_WIDTH, baseY + row*SPACE_HEIGHT);
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
}

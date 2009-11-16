package gamegalaxy.games.arimaa.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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

	public BucketPanel()
	{
		try
		{
			backgroundImage = ImageIO.read(new File("resources/Arimaa Bucket.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
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
		piecePanel.setLocation(getX() + 97, 174);
		piecePanel.setHolder(this);
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
	}

}

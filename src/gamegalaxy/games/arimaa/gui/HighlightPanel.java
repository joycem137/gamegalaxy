package gamegalaxy.games.arimaa.gui;

import gamegalaxy.games.arimaa.data.PiecePosition;
import gamegalaxy.tools.ResourceLoader;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * This class provides the information for highlighting and graphical cues to be placed
 * over the board on the fly.
 */
@SuppressWarnings("serial")
public class HighlightPanel extends JPanel
{
	public static final int BLUE 	= 1;
	public static final int OFF		= 0;
	
	//store a copy of the ResourceLoader, since highlight panels can change images.
	private ResourceLoader loader;
	
	//color of this highlighter.
	private int color = OFF;

	//store the highlight image being displayed.
	private Image highlightImage;

	//BoardPosition where the highlightPanel is located.
	private PiecePosition boardPosition;

	
	public HighlightPanel(ResourceLoader loader)
	{
		//Store the resource loader information first.
		this.loader = loader;
		
		//Store the information about this highlighter.
		this.boardPosition = new PiecePosition(0, 0);
	}
	
	/**
	 * 
	 * Draws the highlighter.
	 *
	 * @param g The graphics context for this paint operation.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		g.drawImage(highlightImage, 0, 0, this);
	}

	/**
	 * Provides the current board position of the highlighter.
	 *
	 * @return BoardPosition(col,row) where the highlighter is displayed.
	 */
	public PiecePosition getBoardPosition()
	{
		return boardPosition;
	}

	/**
	 * Changes the board position (col,row) where the highlighter is displayed.
	 *
	 * @param position	BoardPosition(col,row) we're moving the highlighter to.
	 */	
	public void setBoardPosition(PiecePosition position)
	{
		this.boardPosition = position;
	}
	
	/**
	 * Changes the "color" of the highlighter.  0 is OFF and will also change the highlighter's
	 * setVisible to false; otherwise this updates the image from the ResourceLoader and updates
	 * setVisible to true.
	 * 
	 * @param color	int corresponding to the highlighter's "color".
	 */		
	public void setColor(int color)
	{
		if (this.color == color) return;
		this.color = color;
		if (color == OFF)
		{
			setVisible(false);
		}
		else
		{
			String highlightName = getColorString() + "Highlight";
			highlightImage = loader.getResource(highlightName);
			setSize(highlightImage.getWidth(this), highlightImage.getHeight(this));
			setVisible(true);
		}
	}

	/**
	 * Retrieves a String corresponding to the highlighter's color.  Mostly used for parsing
	 * the image name for the ResourceLoader.
	 * 
	 * return	String corresponding to the highlighter's color
	 */		
	public String getColorString()
	{
		switch(color)
		{
			case BLUE: return "Blue";
			default: return "";
		}
	}
}

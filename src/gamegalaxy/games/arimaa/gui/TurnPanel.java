package gamegalaxy.games.arimaa.gui;

import java.awt.Graphics;
import java.awt.Image;

import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.tools.ResourceLoader;

import javax.swing.JPanel;

/**
 * 
 */
public class TurnPanel extends JPanel
{

	private Image	backgroundImage;
	private Image 	goldIndicator;
	private Image	silverIndicator;
	
	private int turnState;

	/**
	 * TODO: Describe constructor
	 *
	 * @param loader
	 */
	public TurnPanel(ResourceLoader loader)
	{
		backgroundImage = loader.getResource("TurnBackground");
		
		goldIndicator = loader.getResource("GoldIndicator");
		silverIndicator = loader.getResource("SilverIndicator");
		
		turnState = GameConstants.GOLD;
		
		setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
	}
	
	public void setTurnState(int turnState)
	{
		this.turnState = turnState;
		repaint();
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
		
		//Draw the turn indicator
		if(turnState == GameConstants.GOLD)
		{
			g.drawImage(goldIndicator, 5, 10, this);
		}
		else
		{
			g.drawImage(silverIndicator, 407, 10, this);
		}
	}

}

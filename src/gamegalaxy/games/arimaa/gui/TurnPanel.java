package gamegalaxy.games.arimaa.gui;

import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.tools.ResourceLoader;
import gamegalaxy.tools.ShadowedLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 */
public class TurnPanel extends JPanel
{
	private Image	backgroundImage;
	private Image 	goldIndicator;
	private Image	silverIndicator;
	
	private JButton endTurnButton;
	
	private int turnState;
	private ShadowedLabel	winnerLabel;

	/**
	 * TODO: Describe constructor
	 *
	 * @param loader
	 */
	@SuppressWarnings("serial")
	public TurnPanel(final ArimaaUI gui, ResourceLoader loader)
	{
		//Set the layout to none.
		setLayout(null);
		
		//Create the winner field.
		winnerLabel = new ShadowedLabel();
		winnerLabel.setLocation(0, 0);
		winnerLabel.setFont(new Font("Serif", Font.BOLD, 35));
		add(winnerLabel);
		
		//Create the end turn button, but do not add it to the screen just yet.
		endTurnButton = new JButton(new AbstractAction("End Turn")
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.endTurn();
			}
		});
		endTurnButton.setLocation(148, 17);
		endTurnButton.setSize(164, 33);
		add(endTurnButton);
		
		//Get our images.
		backgroundImage = loader.getResource("TurnBackground");
		goldIndicator = loader.getResource("GoldIndicator");
		silverIndicator = loader.getResource("SilverIndicator");
		
		//Set the turn state.
		turnState = GameConstants.GOLD;
		
		//Set the size of this panel.
		setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				winnerLabel.setVisible(false);
				endTurnButton.setVisible(false);
			}
		});
	}
	
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		winnerLabel.setSize(width, height);
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

	/**
	 * TODO: Describe method
	 *
	 * @param b
	 */
	public void showEndTurnButton(boolean b)
	{
		endTurnButton.setVisible(b);
	}

	/**
	 * TODO: Describe method
	 *
	 * @param player
	 */
	public void setWinner(int player)
	{
		if(player == GameConstants.GOLD)
		{
			winnerLabel.setForeground(Color.YELLOW);
			winnerLabel.setText("Gold has won!");
		}
		else
		{
			winnerLabel.setForeground(Color.GRAY);
			winnerLabel.setText("Silver has won!");
		}
		
		endTurnButton.setVisible(false);
		turnState = player;
		winnerLabel.setVisible(true);
		repaint();
	}

}

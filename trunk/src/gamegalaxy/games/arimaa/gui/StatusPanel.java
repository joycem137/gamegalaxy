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

import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.engine.ArimaaEngine;
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
 * This class stores all of the handling for the "status panel" located at the bottom of the
 * board.
 */
@SuppressWarnings("serial")
public class StatusPanel extends JPanel
{
	private Image	backgroundImage;
	private Image 	goldIndicator;
	private Image	silverIndicator;
	
	private JButton endTurnButton;
	
	private int turnState;
	private ShadowedLabel	turnLabel;

	/**
	 * Construct this status panel.
	 *
	 * @param loader
	 */
	public StatusPanel(final ArimaaEngine engine, ResourceLoader loader)
	{
		//Set the layout to none.
		setLayout(null);
		
		//Create the winner field.
		turnLabel = new ShadowedLabel();
		turnLabel.setLocation(0, 0);
		turnLabel.setFont(new Font("Serif", Font.BOLD, 35));
		add(turnLabel);
		
		//Create the end turn button, but do not add it to the screen just yet.
		endTurnButton = new JButton(new AbstractAction("End Turn")
		{
			public void actionPerformed(ActionEvent e)
			{
				engine.endMove();
			}
		});
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
				endTurnButton.setVisible(false);
			}
		});
	}
	
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		
		int centerX = (getWidth() - endTurnButton.getWidth()) / 2;
		endTurnButton.setLocation(centerX, 17);
		
		turnLabel.setSize(width, height);
	}
	
	public void setTurnState(int turnState)
	{
		this.turnState = turnState;

		if(turnState == GameConstants.GOLD)
		{
			turnLabel.setForeground(Color.YELLOW);
			turnLabel.setText("Gold's Turn");
		}
		else
		{
			turnLabel.setForeground(Color.GRAY);
			turnLabel.setText("Silver's Turn");
		}

		turnLabel.setVisible(true);
		
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
			g.drawImage(silverIndicator, getWidth() - silverIndicator.getWidth(this) - 7, 10, this);
		}
	}

	/**
	 * Either show or don't show the end turn button, depending on b.
	 * Set the turn text label to the inverse of this value.
	 *
	 * @param b
	 */
	public void showEndTurnButton(boolean b)
	{
		endTurnButton.setVisible(b);
		turnLabel.setVisible(!b);
	}

	/**
	 * Sets the status panel into the winning state for the indicated player.
	 *
	 * @param player
	 */
	public void setWinner(int player)
	{
		//Set the text of the turn label to the appropriate text.
		if(player == GameConstants.GOLD)
		{
			turnLabel.setForeground(Color.YELLOW);
			turnLabel.setText("Gold has won!");
		}
		else
		{
			turnLabel.setForeground(Color.GRAY);
			turnLabel.setText("Silver has won!");
		}
		
		//Hide the end turn button.
		endTurnButton.setVisible(false);
		turnState = player;
		turnLabel.setVisible(true);
		repaint();
	}

}

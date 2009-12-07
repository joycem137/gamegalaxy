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

import gamegalaxy.tools.ResourceLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * This class stores all of the data for displaying how many moves 
 * are remaining for a given player. 
 */
@SuppressWarnings("serial")
public class RemainingMovesPanel extends JPanel
{
	private Image moveImage;
	private Image offScreenImage;
	
	private static int SPACER_LENGTH = 30;
	
	public RemainingMovesPanel(ResourceLoader loader)
	{
		moveImage = loader.getResource("MoveIndicator");
		
		createOffScreenBuffer();
		
		setSize(offScreenImage.getWidth(this), offScreenImage.getHeight(this));
	}
	
	/**
	 * Create the offscreen buffer to be used to draw the screen.
	 *
	 * @return
	 */
	private void createOffScreenBuffer()
	{
		offScreenImage = new BufferedImage(4 * moveImage.getWidth(this) + 3 * SPACER_LENGTH, 
				moveImage.getHeight(this), BufferedImage.TYPE_INT_ARGB_PRE);
	}
	
	/**
	 * 
	 * Redraw the panel using the indicated number of moves.
	 *
	 * @param numberOfMoves
	 */
	public void setMoves(int numberOfMoves)
	{
		//Recreate the off screen buffer.
		createOffScreenBuffer();
		
		Graphics g = offScreenImage.getGraphics();
		
		//Draw one turn image for each turn image.
		for(int i = 0; i < numberOfMoves; i++)
		{
			g.drawImage(moveImage, i * (moveImage.getWidth(this) + SPACER_LENGTH), 0, this);
		}
		
		//Flush the image.
		offScreenImage.flush();
		
		//Repaint the screen.
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(offScreenImage, 0, 0, this);
	}
}

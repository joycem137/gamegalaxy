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

import gamegalaxy.games.arimaa.data.GameBoardData;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class contains all the information for displaying the Arimaa board.
 */
public class BoardPainter implements ImageObserver
{
	private Image backgroundImage;
	
	private final static int BORDER_WIDTH = 27;
	private final static int GRID_WEIGHT = 3;
	private final static int BOARD_WIDTH = 527;
	private final static int BOARD_HEIGHT = 527;
	
	/**
	 * 
	 * Create a display for the board.
	 *
	 */
	public BoardPainter()
	{
		try
		{
			backgroundImage = ImageIO.read(new File("resources/Arimaa Board.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Image drawBoard(GameBoardData board)
	{
		BufferedImage boardImage = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT,
				BufferedImage.TYPE_INT_ARGB_PRE);
		
		//Get the image's graphics context.
		Graphics2D g = boardImage.createGraphics();
		
		//Draw the background
		g.drawImage(backgroundImage, 0, 0, this);
		
		return boardImage;
	}

	/**
	 * TODO: Describe overridden method
	 *
	 * @param img
	 * @param infoflags
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
	 */
	public boolean imageUpdate(Image img, int infoflags, int x, int y,
			int width, int height)
	{
		//The image should be loaded by the time we get to this point.  We should return false.
		return false;
	}
}

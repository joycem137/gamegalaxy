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
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.tools.ResourceLoader;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Contains all of the information and code necessary for displaying a single playing piece
 * in the game Arimaa.  Should eventually implement the native Java Drag and Drop API.
 */
@SuppressWarnings("serial")
public class PiecePanel extends JPanel
{
	//Store a link to the UI.
	private ArimaaUI	gui;
	
	//Store the image affiliated with this particular piece.
	private Image pieceImage;
	private Image chitImage;

	//Store the object that is currently holding this piece.
	private PieceHolder	holder;

	private PieceData	data;
	
	public PiecePanel(final ArimaaUI gui, PieceData data, ResourceLoader loader)
	{
		//Store the information about this piece.
		this.data = data;
		this.gui = gui;
		
		//Load the image related to this piece.
		String pieceName = data.getColorString() + data.getNameString();
		pieceImage = loader.getResource(pieceName);
		
		drawChitImage(loader);
		
		//configure the size of this panel
		setSize(pieceImage.getWidth(this), pieceImage.getHeight(this));
	}
	
	/**
	 * Draw the chit image.
	 *
	 * @param loader
	 */
	private void drawChitImage(ResourceLoader loader)
	{
		Image singleChitImage = loader.getResource("Chit");
		
		//Initialize the image.
		int chitWidth = singleChitImage.getWidth(this);
		int chitHeight = singleChitImage.getHeight(this);
		int multiChitHeight = data.getValue() * chitHeight;
		chitImage = new BufferedImage(chitWidth, multiChitHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		
		Graphics g = chitImage.getGraphics();
		
		//Draw the chits
		for(int i = 0; i <= data.getValue(); i++)
		{
			g.drawImage(singleChitImage, 0, (i - 1) * chitHeight, this);
		}
		
		chitImage.flush();
	}

	/**
	 * 
	 * Draws the image associated with this piece, with a transparent background.
	 *
	 * @param g The graphics context for this paint operation.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		g.drawImage(pieceImage, 0, 0, this);
		if(data.getColor() == GameConstants.GOLD)
		{
			g.drawImage(chitImage, 5, 5, this);
		}
		else
		{
			g.drawImage(chitImage, getWidth() - chitImage.getWidth(this) - 5, 5, this);
		}
	}

	/**
	 * Get the object that is currently holding this piece.
	 *
	 * @return The object that is holding this piece.
	 */
	public PieceHolder getHolder()
	{
		return holder;
	}

	/**
	 * Set the object that is currently holding this piece.
	 *
	 * @param pieceHolder The object that is holding this piece.
	 */
	public void setHolder(PieceHolder pieceHolder)
	{
		holder = pieceHolder;
	}

	/**
	 * Remove this piece from its holder.  Nothing is holding it right now.
	 *
	 */
	public void removeHolder()
	{
		holder = null;
	}

	/**
	 * Return the {@link PieceData} object attached to this piece.
	 *
	 * @return
	 */
	public PieceData getData()
	{
		return data;
	}
}

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
 *  
 */

package gamegalaxy.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JComponent;

/**
 * A special utility class that implements some JLabel functionality and displays a nice shadow 
 * under the text.
 */
@SuppressWarnings("serial")
public class ShadowedLabel extends JComponent
{
	private String text;
	private BufferedImage image;
	
	/**
	 * Creates a blank shadowed label.
	 *
	 */
	public ShadowedLabel()
	{
		super();
		this.text = "";
	}
	
	/**
	 * Set the text of the label.
	 *
	 * @param text
	 */
	public void setText(String text)
	{
		this.text = text;
		redrawImage();
	}
	
	/**
	 * Called whenever the text changes, this recreates the offscreen image buffer based
	 * on the current text.
	 *
	 */
	private void redrawImage()
	{
		image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = image.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
        TextLayout textLayout = new TextLayout(text, getFont(), g.getFontRenderContext());
		
		// Determine where to draw the message.
        Rectangle2D messageSize = textLayout.getBounds();
		Rectangle2D bounds = getBounds();
		
		//Find the center point to place the text.
		int centerX = (int)(bounds.getCenterX() - messageSize.getCenterX());
		int offsetY = (int)(getHeight() - messageSize.getHeight()) / 2 + 4;
		int centerY = getHeight() - offsetY;

		int SHADOW_OFFSET = 4;
		
		//Draw the shadow
		g.setColor(Color.black);
		textLayout.draw(g, centerX + SHADOW_OFFSET, centerY + SHADOW_OFFSET);
		
        //blur the shadow: result is sorted in image2
        float ninth = 1.0f / 9.0f;
        float[] kernel = {ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth};
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel), ConvolveOp.EDGE_NO_OP, null);
        image = op.filter(image, null);
		
		//Draw the proper text.
        g = image.createGraphics();
		g.setColor(getForeground());
        textLayout.draw(g, centerX, centerY);
		
		repaint();
	}
	
	/**
	 * Draw the offscreen buffer to the screen.
	 *
	 * @param g
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		g.drawImage(image, 0, 0, this);
	}
}

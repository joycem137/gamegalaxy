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
 * 
 */
public class ShadowedLabel extends JComponent
{
	private String text;
	private BufferedImage image;
	
	public ShadowedLabel()
	{
		super();
		this.text = "";
	}
	
	public void setText(String text)
	{
		this.text = text;
		redrawImage();
	}
	
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
	
	public void paintComponent(Graphics g)
	{
		g.drawImage(image, 0, 0, this);
	}
}

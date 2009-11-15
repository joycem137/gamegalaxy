package gamegalaxy.games.arimaa.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 */
public class BucketPainter implements ImageObserver
{
	private Image	backgroundImage;
	private static final int BUCKET_WIDTH = 172;
	private static final int BUCKET_HEIGHT = 462;

	public BucketPainter()
	{
		try
		{
			backgroundImage = ImageIO.read(new File("resources/Arimaa Bucket.png"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public Image drawBucket()
	{
		BufferedImage image = new BufferedImage(BUCKET_WIDTH, BUCKET_HEIGHT,
				BufferedImage.TYPE_INT_ARGB_PRE);
		
		//Get the image's graphics context.
		Graphics2D g = image.createGraphics();
		
		//Draw the background
		g.drawImage(backgroundImage, 0, 0, this);
		
		return image;
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
		return false;
	}

}

package gamegalaxy.games.arimaa.gui;

import gamegalaxy.games.arimaa.engine.ArimaaEngine;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 */
public class ArimaaUI extends JPanel
{
	private ArimaaEngine	engine;
	private BoardPainter	boardPainter;
	private BucketPainter	bucketPainter;
	
	//Store the images that we are drawing to the screen.
	private Image 			backgroundImage;
	private Image 			boardImage;
	private Image			goldBucketImage;
	private Image			silverBucketImage;

	public ArimaaUI(ArimaaEngine engine)
	{
		this.engine = engine;
		
		//Configure this panel
		setLayout(null);

		//Create the backgroundimage.
		try
		{
			backgroundImage = ImageIO.read(new File("resources/Arimaa Background.png"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Create other components
		boardPainter = new BoardPainter();
		boardImage = boardPainter.drawBoard(engine.getBoard());
		
		//Create buckets
		bucketPainter = new BucketPainter();
		goldBucketImage = bucketPainter.drawBucket();
		silverBucketImage = bucketPainter.drawBucket();
		
		setPreferredSize(new Dimension(1024, 768));
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		//Paint the background
		g.drawImage(backgroundImage, 0, 0, this);
		
		//Paint the board
		g.drawImage(boardImage, 248, 120, this);
		
		//Paint the buckets.
		g.drawImage(goldBucketImage, 38, 153, this);
		g.drawImage(silverBucketImage, 808, 153, this);
	}
}

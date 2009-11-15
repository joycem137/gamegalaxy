package gamegalaxy.games.arimaa.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 */
public class PiecePanel extends JPanel
{
	private Image pieceImage;
	
	private static final int PIECE_WIDTH = 59;
	private static final int PIECE_HEIGHT = 59;
	
	public PiecePanel()
	{
		try
		{
			pieceImage = ImageIO.read(new File("resources/Arimaa Rabitt.png"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setSize(PIECE_WIDTH, PIECE_HEIGHT);
		setLocation(0, 0);
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(pieceImage, 0, 0, this);
	}
}

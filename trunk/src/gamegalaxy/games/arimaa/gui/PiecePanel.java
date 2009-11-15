package gamegalaxy.games.arimaa.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
		MouseAdapter ma = new MouseAdapter()
		{
			private int	dragXStart;
			private int	dragYStart;

			public void mousePressed(MouseEvent me)
			{
				dragXStart = me.getXOnScreen();
				dragYStart = me.getYOnScreen();
			}
			
			public void mouseReleased(MouseEvent me)
			{
			}
			
			public void mouseDragged(MouseEvent me)
			{
				//Determine the updated position from the deltas.
				int newX = getX() + (me.getXOnScreen() - dragXStart);
				int newY = getY() + (me.getYOnScreen() - dragYStart);
				
				//Reset the start position
				dragXStart = me.getXOnScreen();
				dragYStart = me.getYOnScreen();
				
				//Set the new location
				setLocation(newX, newY);
			}
		};
		
		addMouseListener(ma);
		addMouseMotionListener(ma);
		
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

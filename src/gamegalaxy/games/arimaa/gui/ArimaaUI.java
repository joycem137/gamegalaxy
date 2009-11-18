package gamegalaxy.games.arimaa.gui;

import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.engine.ArimaaEngine;
import gamegalaxy.tools.ResourceLoader;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * The main UI for the Arimaa game implementation within the galaxy of games.
 * 
 * Eventually, this should be a subclass of a generic UI type of system.  For now,
 * it's a class unto itself.
 */
@SuppressWarnings("serial")
public class ArimaaUI extends JPanel
{
	private BoardPanel		boardPanel;
	private BucketPanel		goldBucketPanel;
	private BucketPanel		silverBucketPanel;
	
	//Store the background image that we want to draw.
	private Image 				backgroundImage;
	private List<PiecePanel>	piecePanels;
	private TurnPanel			turnPanel;
	private ArimaaEngine		engine;

	/**
	 * 
	 * Construct the UI, load any files needed for it, set the layout, etc.
	 * @param loader 
	 *
	 */
	public ArimaaUI(ArimaaEngine engine, ResourceLoader loader)
	{	
		this.engine = engine;
		
		//Configure this panel
		setLayout(null);

		//Create the background image.
		backgroundImage = loader.getResource("AppBackground");
		
		createPieces(engine, loader);
		
		//Create other components
		boardPanel = new BoardPanel(loader);
		add(boardPanel);
		boardPanel.setLocation(248, 120);
		
		//Create buckets
		goldBucketPanel = new BucketPanel(loader);
		add(goldBucketPanel);
		goldBucketPanel.setLocation(38, 153);
		
		silverBucketPanel = new BucketPanel(loader);
		add(silverBucketPanel);
		silverBucketPanel.setLocation(808, 153);
		
		turnPanel = new TurnPanel(loader);
		add(turnPanel);
		turnPanel.setLocation(282, 659);
		
		setPreferredSize(new Dimension(1024, 768));
		
		//Start with the pieces in the buckets.
		initializePieceLocations();
		turnPanel.setTurnState(GameConstants.GOLD);
	}
	
	/**
	 * TODO: Describe method
	 *
	 */
	private void initializePieceLocations()
	{
		Iterator<PiecePanel> piecePanelIterator = piecePanels.iterator();
		while(piecePanelIterator.hasNext())
		{
			PiecePanel tempPiece = piecePanelIterator.next();
			if(tempPiece.getData().getColor() == GameConstants.GOLD)
			{
				goldBucketPanel.dropPiece(tempPiece);
			}
			else
			{
				silverBucketPanel.dropPiece(tempPiece);
			}
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param engine
	 * @param loader
	 */
	private void createPieces(ArimaaEngine engine, ResourceLoader loader)
	{
		List<PieceData> pieceDataList = engine.getPieces(); 
		Iterator<PieceData> iterator = pieceDataList.iterator();
		piecePanels = new Vector<PiecePanel>(pieceDataList.size());
		while(iterator.hasNext())
		{
			PieceData pieceData = iterator.next();
			PiecePanel tempPiece = new PiecePanel(this, pieceData, loader);
			add(tempPiece);
			piecePanels.add(tempPiece);
		}
	}

	/**
	 * 
	 * Draw the background image for this panel.  Do not draw anything else.
	 *
	 * @param g The graphics context for drawing on this panel.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		//Paint the background
		g.drawImage(backgroundImage, 0, 0, this);
	}

	/**
	 * Since this is the main UI class, it is responsible for figuring out what
	 * objects at the top level want to respond to a drag and drop event.
	 * 
	 * This should be replaced when we refactor this to use Java's built in
	 * Drag-n-drop functionality.
	 *
	 * @param x The x coordinate, relative to the UI, of the place we are interested
	 * 			in looking for a drop target.
	 * @param y The y coordinate, relative to the UI, of the place we are interested
	 * 			in looking for a drop target.
	 * @return The drop target associated with this location. 
	 */
	private PieceHolder getHolderAt(int x, int y)
	{
		Component[] components = getComponents();
		for(int i = 0; i < components.length; i++)
		{
			//Store a link to the component in question.
			Component component = components[i];
			Rectangle bounds = component.getBounds();
			
			if(x >= bounds.getMinX() 
				&& x <= bounds.getMaxX() 
				&& y >= bounds.getMinY()
				&& y <= bounds.getMaxY())
			{
				/*
				 * This is BAD BAD BAD.  You should never use a try/catch when you can
				 * use a conditional.  However, I don't know a better way to do it without
				 * access to the internet to do research on the subject.
				 * 
				 * Airplanes make for terrible places to get research done.
				 */
				try
				{
					return (PieceHolder)component;
				}
				catch(ClassCastException cce)
				{
					//Nothing to do, just don't do the return.
				}
			}
		}
	
		//We couldn't find anything.
		return null;
	}
	
	public boolean canDragPiece(PiecePanel piecePanel)
	{
		return engine.isPiecePlaceable(piecePanel.getData());
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piecePanel
	 * @param holder
	 * @param dropLocation
	 */
	public void droppedPiece(PiecePanel piecePanel, PieceHolder holder,
			Point dropLocation)
	{
		PieceHolder dropPanel = getHolderAt(dropLocation.x, dropLocation.y); 
		
		if(dropPanel == boardPanel)
		{	
			//Determine what square we dropped this on:
			int relativeDropX = dropLocation.x - boardPanel.getX();
			int relativeDropY = dropLocation.y - boardPanel.getY();
			BoardPosition space = boardPanel.identifyBoardPosition(relativeDropX, relativeDropY);
			
			//Check with the engine to see if that's a valid spot or not.
			if(engine.isValidPiecePlacement(piecePanel.getData(), space))
			{	
				//Identify the piece's old location on the board, if applicable, and remove it
				if(holder ==  boardPanel)
				{
					//Identify the location on the board.
					int oldX = piecePanel.getOriginalX() - boardPanel.getX();
					int oldY = piecePanel.getOriginalY() - boardPanel.getY();
					BoardPosition originalSpace = boardPanel.identifyBoardPosition(oldX, oldY);
					engine.removePiece(originalSpace);
				}
				
				//Remove the piece from its previous location
				holder.removePiece(piecePanel);
				
				//Place the piece in its new location;
				boardPanel.placePiece(piecePanel, space);
				engine.placePiece(piecePanel.getData(), space);
			}
			else
			{
				piecePanel.resetPosition();
			}
		}
		else if(dropPanel == goldBucketPanel || dropPanel == silverBucketPanel)
		{
			//Identify the piece's old location on the board, if applicable, and remove it
			if(holder ==  boardPanel)
			{
				//Identify the location on the board.
				int oldX = piecePanel.getOriginalX() - boardPanel.getX();
				int oldY = piecePanel.getOriginalY() - boardPanel.getY();
				BoardPosition originalSpace = boardPanel.identifyBoardPosition(oldX, oldY);
				engine.removePiece(originalSpace);
			}
			
			//Move the piece into the bucket.
			holder.removePiece(piecePanel);
			dropPanel.dropPiece(piecePanel);
		}
		else
		{
			//We didn't go anywhere.  Just reset the position of the object.
			piecePanel.resetPosition();
		}
	}
}

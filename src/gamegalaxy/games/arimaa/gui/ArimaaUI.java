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

import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.BucketPosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.GameState;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.PiecePosition;
import gamegalaxy.games.arimaa.data.StepData;
import gamegalaxy.games.arimaa.engine.ArimaaEngine;
import gamegalaxy.tools.ResourceLoader;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

/**
 * The main UI for the Arimaa game implementation within the galaxy of games.
 * 
 * Eventually, this should be a subclass of a generic UI type of system.  For now,
 * it's a class unto itself.
 */
@SuppressWarnings("serial")
public class ArimaaUI extends JPanel
{
	//Store the background image that we want to draw.
	private Image 				backgroundImage;
	
	//Store some basic data.
	private List<PiecePanel>	piecePanels;
	private ArimaaEngine		engine;
	
	//Store all of the objects attached to this panel.
	private JButton	goldRandomSetupButton;
	private JButton	silverRandomSetupButton;
	private BoardPanel		boardPanel;
	private BucketPanel		goldBucketPanel;
	private BucketPanel		silverBucketPanel;
	private StatusPanel		statusPanel;
	private RemainingMovesPanel	remainingMovesPanel;
	
	private HighlightPanel		highlight;
	
	//Store the resource loader
	private ResourceLoader	loader;

	//Store information about the piece in hand.
	protected PiecePanel	pieceInHand;
	protected Point	pieceInHandOriginalLocation;

	
	/**
	 * 
	 * Construct the UI, load any files needed for it, set the layout, etc.
	 * @param loader 
	 *
	 */
	public ArimaaUI(final ArimaaEngine engine, ResourceLoader loader)
	{	
		//Store a link to the engine
		this.engine = engine;
		
		//Store the resource loader
		this.loader = loader;
		
		//Configure this panel
		setLayout(null);
		piecePanels = new Vector<PiecePanel>(16);

		//Create the background image.
		backgroundImage = loader.getResource("AppBackground");
		
		createChildrenPanels();
		
		addMouseListeners();
		
		setPreferredSize(new Dimension(891, 640));
		
		//Link the engine and GUI.
		engine.linkGUI(this);
	}

	/**
	 * Global level mouse behavior to capture all mouse behaviors for this interface. To ensure correct behavior,
	 * all children components should not capture any mouse events.
	 *
	 */
	private void addMouseListeners()
	{
		//Create the mouse listener to listen for mouse events.
		MouseInputAdapter ma = new MouseInputAdapter()
		{	
			//Used for determining if the mouse moved or not.
			private boolean mouseDragged;

			public void mousePressed(MouseEvent me)
			{	
				//for now, do nothing unless the mouse press was a left-click.
				if (me.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}
				
				if(pieceInHand != null)
				{
					//We already have a piece in hand.  Drop it.
					Point mousePosition = new Point(me.getX(), me.getY());
					dropPieceInHand(mousePosition);
				}
				else
				{
					//We don't have a piece in hand.  See if there's one under the mouse to pick up:
					
					/*
					 * NOTE: There is a dependency here.  If the PiecePanel is not the topmost Component object
					 * at this location, this method will not return the PiecePanel even if the mouse is right
					 * over it.  Be sure to refactor this later if necessary. 
					 */
					Component component = getComponentAt(me.getX(), me.getY());
					
					if(component instanceof PiecePanel)
					{
						//We have a piece panel!  Recast the variable.
						PiecePanel piecePanel = (PiecePanel)component;
					
						if(engine.canPieceBeMoved(piecePanel.getData()))
						{
							//Reset the "mouse moved" variable
							mouseDragged = false;

							Point mousePosition = new Point(me.getX(), me.getY());
							pickUpPiece(piecePanel, mousePosition);
						}
					}
				}
			}

			public void mouseReleased(MouseEvent me)
			{
				//for now, do nothing unless the mouse press was a left-click.
				if (me.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}

				if(pieceInHand != null && mouseDragged)
				{
					//Drop the piece, if appropriate.
					Point mousePosition = new Point(me.getX(), me.getY());
					dropPieceInHand(mousePosition);
				}
				
			}
			
			public void mouseMoved(MouseEvent me)
			{
				if(pieceInHand != null)
				{
					//Move the piece so that it is centered on the mouse.
					Point mousePosition = new Point(me.getX(), me.getY());
					movePieceInHand(mousePosition);
				}
			}
			
			public void mouseDragged(MouseEvent me)
			{
				mouseDragged = true;
				if(pieceInHand != null)
				{
					//Move the piece so that it is centered on the mouse.
					Point mousePosition = new Point(me.getX(), me.getY());
					movePieceInHand(mousePosition);
				}
			}
		};
		
		//Now add the mouse listeners
		addMouseListener(ma);
		addMouseMotionListener(ma);
	}

	private void pickUpPiece(PiecePanel piecePanel, Point mousePosition)
	{
		//Pick up the piece.
		pieceInHand = piecePanel;
		
		//ensure that the dragged piece is topmost and visible.
		setComponentZOrder(piecePanel, 0);
		
		//Identify the original location of the piece.
		pieceInHandOriginalLocation = piecePanel.getLocation();
		
		//Move the piece so that it is centered on the mouse.
		movePieceInHand(mousePosition);
	}
	
	/**
	 * 
	 * Reposition the piece to be in the indicated location.  Also notify the UI about the position of the piece for
	 * highlighting purposes.
	 *
	 * @param mouseInFrame
	 */
	private void movePieceInHand(Point mousePosition)
	{
		//Move the piece itself to the center of the mouse position.
		pieceInHand.setLocation(mousePosition.x - pieceInHand.getWidth() / 2, mousePosition.y - pieceInHand.getHeight() / 2);

		//Find out where we're coming from
		PieceHolder mouseOverPanel = getHolderAt(mousePosition.x, mousePosition.y); 	

		if(mouseOverPanel == boardPanel)
		{
			//Determine what square we're over:
			int relativeDragX = mousePosition.x - boardPanel.getX();
			int relativeDragY = mousePosition.y - boardPanel.getY();
			BoardPosition mouseOverPosition = boardPanel.identifyBoardPosition(relativeDragX, relativeDragY);
			
			//Set the highlight, if appropriate
			if(engine.isValidStep(new StepData(pieceInHand.getData(), mouseOverPosition)) || 
					engine.isValidSwap(pieceInHand.getData(), pieceInHand.getData().getPosition(), mouseOverPosition))
			{
				highlightSpace(mouseOverPosition);
			}
			else
			{
				clearHighlight();
			}
		}
		else
		{
			clearHighlight();
		}
	}

	/**
	 * Highlight the indicated board position
	 *
	 * @param mouseOverPosition
	 */
	private void highlightSpace(BoardPosition mouseOverPosition)
	{
		//Set the color of the highlight.
		highlight.setColor(HighlightPanel.BLUE);
		
		//get coords of upper-left corner of this square:
		Point coords = boardPanel.identifyCoordinates(mouseOverPosition);
		
		//place our highlighter over this square:
		highlight.setLocation(coords.x + boardPanel.getX(), coords.y + boardPanel.getY());
	}

	/**
	 * Clear the highlight by turning it off and moving it off the screen.
	 *
	 */
	private void clearHighlight()
	{
		highlight.setLocation(0, 0);
		highlight.setColor(HighlightPanel.OFF);
	}

	/**
	 * 
	 * Drop the piece whereever it has found itself.
	 *
	 */
	private void dropPieceInHand(Point dropLocation)
	{
		//Remove the piece in hand
		PiecePanel pieceToDrop = pieceInHand;
		pieceInHand = null;
		
		//allow other dragged pieces to display over this one.
		setComponentZOrder(pieceToDrop, 1);	
		
		//Turn off highlighting.
		clearHighlight();
		
		//Determine where we're going to drop the piece.
		PiecePosition dropPosition = getPiecePositionAt(dropLocation);
		
		//Now see if it makes sense to move this piece.
		if(engine.isValidStep(new StepData(pieceToDrop.getData(), dropPosition)))
		{	
			//Now update the engine.
			engine.takeStep(new StepData(pieceToDrop.getData(), dropPosition));
		}
		else if(engine.isValidSwap(pieceToDrop.getData(), pieceToDrop.getData().getPosition(), dropPosition))
		{
			//need to identify and grab the piecePanel at dropPosition.
			PiecePanel targetPanel = getPieceAt(dropPosition);
	
			//Update the engine.
			engine.swapPieces(pieceToDrop.getData(), targetPanel.getData(), pieceToDrop.getData().getPosition(), dropPosition);
		}
		else
		{
			pieceToDrop.setLocation(pieceInHandOriginalLocation);
		}	
	}

	/**
	 * Create all of the children panels that are attached to this UI.
	 */
	private void createChildrenPanels()
	{
		//Create highlight panel.
		highlight = new HighlightPanel(loader);
		add(highlight);
		
		//Create the panel for displaying the remaining number of moves.
		remainingMovesPanel = new RemainingMovesPanel(loader);
		add(remainingMovesPanel);
		
		//Create other components
		boardPanel = new BoardPanel(loader);
		add(boardPanel);
		boardPanel.setLocation(182, 15);

		//Set the location of the "remaining moves" panel
		remainingMovesPanel.setLocation(boardPanel.getX() + 
				(boardPanel.getWidth() - remainingMovesPanel.getWidth()) / 2, 
				boardPanel.getY() + boardPanel.getHeight() - 20);
		
		//Create buckets
		goldBucketPanel = new BucketPanel(loader);
		add(goldBucketPanel);
		goldBucketPanel.setLocation(15, 27);
		
		silverBucketPanel = new BucketPanel(loader);
		add(silverBucketPanel);
		silverBucketPanel.setLocation(724, 27);
		
		//Create the "random setup" buttons.
		goldRandomSetupButton = new JButton(new AbstractAction("Place randomly")
		{
			public void actionPerformed(ActionEvent e)
			{
				engine.doRandomSetup();
			}
			
		});
		goldRandomSetupButton.setLocation(goldBucketPanel.getX(), 
				goldBucketPanel.getY() + goldBucketPanel.getHeight() + 15);
		goldRandomSetupButton.setSize(goldBucketPanel.getWidth(), 33);
		add(goldRandomSetupButton);
		
		silverRandomSetupButton = new JButton(new AbstractAction("Place randomly")
		{
			public void actionPerformed(ActionEvent e)
			{
				engine.doRandomSetup();
			}
			
		});
		silverRandomSetupButton.setLocation(silverBucketPanel.getX(), 
				silverBucketPanel.getY() + silverBucketPanel.getHeight() + 15);
		silverRandomSetupButton.setSize(silverBucketPanel.getWidth(), 33);
		add(silverRandomSetupButton);
		
		//Create the status panel
		statusPanel = new StatusPanel(engine, loader);
		add(statusPanel);
		statusPanel.setLocation(182, 558);
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
				if(component instanceof PieceHolder)
				{
					return (PieceHolder)component;
				}
			}
		}
	
		//We couldn't find anything.
		return null;
	}

	/**
	 * Finds the PiecePosition corresponding to a particular (x,y) gui location.
	 *
	 * @param location	A point (x,y) on the gui.
	 * @return	A PiecePosition which is either a board position (0~7, 0~7), GOLD_BUCKET,
	 * 			SILVER_BUCKET, or null.
	 */
	private PiecePosition getPiecePositionAt(Point location)
	{
		PieceHolder dropPanel = getHolderAt(location.x, location.y); 
		if(dropPanel == boardPanel)
		{	
			//Determine what square we dropped this on:
			int relativeDropX = location.x - boardPanel.getX();
			int relativeDropY = location.y - boardPanel.getY();
			return boardPanel.identifyBoardPosition(relativeDropX, relativeDropY);
		}
		else if(dropPanel == goldBucketPanel)
		{
			return new BucketPosition(GameConstants.GOLD);
		}
		else if(dropPanel == silverBucketPanel)
		{
			return new BucketPosition(GameConstants.SILVER);
		}
		return null;
	}

	/**
	 * Finds the PiecePanel located at a specific PiecePosition on the board.
	 * Since PiecePosition can be either a board location, bucket, edge, or null,
	 * this will automatically return a null PiecePanel unless the PiecePosition
	 * specified is on the board.
	 *
	 * @param position	the PiecePosition we are wanting to check for pieces.
	 * @return	the PiecePanel at this position if it exists and is unique; null if
	 * 			there is no piece here or it is not a board location.
	 */
	public PiecePanel getPieceAt(PiecePosition position)
	{
		PiecePanel targetPiece = null;
		
		if (position instanceof BoardPosition)
		{
			//we'll use an Iterator to scan the piecePanels for the one we want.
			Iterator<PiecePanel> iterator = piecePanels.iterator();
			while(iterator.hasNext())
			{
				PiecePanel piecePanel = iterator.next();
				PiecePosition piecePosition = piecePanel.getData().getPosition();
				
				//And compare.
				if(piecePosition != null)
				{
					if(piecePosition.equals(position))
					{
						//This is the piece we want.
						targetPiece = piecePanel;
					}
				}
			}
			//A piece that has already been removed by the engine will be missed by the first scan.
			//In this case, scan graphically as well.
			if(targetPiece == null)
			{
				Iterator<PiecePanel> iterator2 = piecePanels.iterator();
				while(iterator2.hasNext())
				{
                    //Get the x,y coords of this piece panel
                	PiecePanel piecePanel = iterator2.next();
                	Point location = piecePanel.getLocation();
                    
                    //Get the corresponding piece position
                	PiecePosition piecePosition = getPiecePositionAt(location);
                    
                    //And compare.
                    if(piecePosition.equals(position))
                	{
                    	//This is the piece we want.
                    	targetPiece = piecePanel;
                    }
				}
			}
		}
		
		return targetPiece;
	}

	/**
	 * Modifies the UI to represent the passed in game state.
	 *  
	 * @param gameState
	 */
	public void displayGameState(GameState gameState)
	{	
		//Remove all pieces.
		clearPieces();
		
		//Then populate the buckets.
		populateBucket(goldBucketPanel, gameState.getGoldBucket());
		populateBucket(silverBucketPanel, gameState.getSilverBucket());
		
		//Populate the board
		populateBoard(gameState.getBoardData());
		
		//Now check if the game is over.
		if(gameState.isGameOver())
		{
			statusPanel.setWinner(gameState.getGameWinner());
		}
		
		//Display the turn state.
		statusPanel.setTurnState(gameState.getCurrentPlayer());
		statusPanel.showEndTurnButton(gameState.canPlayerEndTurn());
		
		//Show the number of turns remaining
		remainingMovesPanel.setMoves(gameState.getRemainingMoves());
		
		//Determine whether to show the setup button or not.
		goldRandomSetupButton.setVisible(gameState.isSetupPhase() && 
				gameState.getCurrentPlayer() == GameConstants.GOLD && 
				gameState.getGoldBucket().size() > 0);
		
		silverRandomSetupButton.setVisible(gameState.isSetupPhase() && 
				gameState.getCurrentPlayer() == GameConstants.SILVER &&
				gameState.getSilverBucket().size() > 0);
	}

	/**
	 * Remove all pieces from the board and buckets.
	 *
	 */
	private void clearPieces()
	{
		goldBucketPanel.resetCount();
		silverBucketPanel.resetCount();
		
		Iterator<PiecePanel> iterator = piecePanels.iterator();
		while(iterator.hasNext())
		{
			PiecePanel piecePanel = iterator.next();
			
			//Remove the piece panel from the UI.
			remove(piecePanel);
		}
	
		//Now clear the list
		piecePanels.clear();
	}

	/**
	 * Takes a BoardData object and populates the UI's board with the appropriate data.
	 *
	 * @param boardData
	 */
	private void populateBoard(BoardData boardData)
	{
		for(int row = 0; row < 8; row++)
		{
			for(int col = 0; col < 8; col++)
			{
				//Get the current position
				BoardPosition position = new BoardPosition(col, row);

				if(boardData.isOccupied(position))
				{
					//Create a new piece panel.
					PiecePanel piecePanel = new PiecePanel(this, boardData.getPieceAt(position), loader);
					
					//Add the piece to the board
					addPiece(piecePanel);
					
					//And now put it on the board.
					boardPanel.placePiece(piecePanel, position);
				}
			}
		}
	}

	/**
	 * Since this action is actually a little complicated, this method is used
	 * to add a {@link PiecePanel} object to the UI.
	 *
	 * @param piecePanel The PiecePanel to add to the UI.
	 */
	private void addPiece(PiecePanel piecePanel)
	{
		//Add the piece to the screen.
		add(piecePanel);
		
		//Add the piece to the piece panel list.
		piecePanels.add(piecePanel);
		
		//Move the piece to the front.
		setComponentZOrder(piecePanel, 1);
	}

	/**
	 * Takes the indicated bucket panel and populates it with the pieces 
	 * stored in the bucket data.
	 *
	 * @param bucketPanel
	 * @param bucketData
	 */
	private void populateBucket(BucketPanel bucketPanel,
			List<PieceData> bucketData)
	{
		Iterator<PieceData> bucketIterator = bucketData.iterator();
		while(bucketIterator.hasNext())
		{
			//Create the piece.
			PiecePanel piecePanel = new PiecePanel(this, bucketIterator.next(), loader);
			
			//Add it to the screen.
			addPiece(piecePanel);
			
			//Then add it to the bucket.
			bucketPanel.dropPiece(piecePanel);
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
}

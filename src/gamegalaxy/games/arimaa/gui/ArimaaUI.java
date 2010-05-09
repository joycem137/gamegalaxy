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
import gamegalaxy.games.arimaa.data.HandPosition;
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
import java.util.Observable;
import java.util.Observer;
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
public class ArimaaUI extends JPanel implements Observer
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

	private SoundEffectPlayer audioPlayer;
	
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
		
		audioPlayer = new SoundEffectPlayer();
		
		//Configure this panel
		setLayout(null);
		piecePanels = new Vector<PiecePanel>(16);

		//Create the background image.
		backgroundImage = loader.getResource("AppBackground");
		
		createChildrenPanels();
		
		addMouseListeners();
		
		setPreferredSize(new Dimension(891, 640));
		
		//Link the engine and GUI.
		engine.addObserver(this);
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
							Point mousePosition = new Point(me.getX(), me.getY());
							pickUpPiece(piecePanel, mousePosition);
						}
						else
						{
							audioPlayer.playBzztSound();
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

				//Don't do anything if the mouse hasn't moved at all.
				if(pieceInHand != null && mouseDragged)
				{
					//Drop the piece, if appropriate.
					Point mousePosition = new Point(me.getX(), me.getY());
					dropPieceInHand(mousePosition);
				}
				
				//Reset the "mouse moved" flag.
				mouseDragged = false;
			}
			
			public void mouseMoved(MouseEvent me)
			{
				if(pieceInHand != null)
				{
					//Move the piece so that it is centered on the mouse.
					Point mousePosition = new Point(me.getX(), me.getY());
					movePieceInHand(mousePosition);
				}
				else
				{
					boolean hasHighlight = false;
					Component component = getComponentAt(me.getX(), me.getY());
					if (component instanceof HighlightPanel)
					{
						hasHighlight = true;
					}
					if (component instanceof PiecePanel)
					{
						PiecePanel pieceAt = (PiecePanel)component;
						//Highlight if this piece can be moved
						if (engine.canPieceBeMoved(pieceAt.getData()))
						{
							PiecePosition position = pieceAt.getData().getPosition();
							if (position instanceof BoardPosition)
							{
								highlightSpace((BoardPosition)position, HighlightPanel.BLUE);
								hasHighlight = true;
						}
							
						//Highlight if this is a frozen piece
							//FIXME: Marker
						}else if (engine.getCurrentGameState().isGameOn()){
							if (engine.getCurrentGameState().getBoardData().isPieceFrozen(pieceAt.getData()))
							{
								PiecePosition position = pieceAt.getData().getPosition();
								highlightSpace((BoardPosition)position, HighlightPanel.FROZEN);
								hasHighlight = true;
							}
						}
						
						
						
					}
					if (hasHighlight == false)
					{
						clearHighlight();
					}
				}
			}
			
			public void mouseDragged(MouseEvent me)
			{
				//for now, do nothing unless the mouse press was a left-click.
				if (me.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}
				
				//keep track of the fact that the mouse has been moved.
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
		//Play the pickup audio
		audioPlayer.playPickupSound();
		
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
		
		boolean hasValidHighlight = false;

		if(mouseOverPanel == boardPanel)
		{
			//Determine what square we're over:
			int relativeDragX = mousePosition.x - boardPanel.getX();
			int relativeDragY = mousePosition.y - boardPanel.getY();
			BoardPosition mouseOverPosition = boardPanel.identifyBoardPosition(relativeDragX, relativeDragY);
			
			if (mouseOverPosition != null)
			{
				//Set the highlight, if appropriate
				StepData step = new StepData(pieceInHand.getData(), mouseOverPosition);
				if(engine.isValidStep(step))
				{
					hasValidHighlight = true;
					highlightSpace(mouseOverPosition, HighlightPanel.BLUE);
				}
				//Highlight if player is trying to setup pieces on the wrong side of board
				else if (engine.getCurrentGameState().isSetupPhase()){
					if (engine.isEnemyRow(step)){
						hasValidHighlight = true;
						highlightSpace(mouseOverPosition, HighlightPanel.RED);
					}
				}
			}
		}
		
		if (!hasValidHighlight)
		{
			clearHighlight();
		}
	}

	/**
	 * Highlight the indicated board position
	 *
	 * @param mouseOverPosition
	 */
	private void highlightSpace(BoardPosition mouseOverPosition, int highlightColor)
	{
		//Set the color of the highlight.
		highlight.setColor(highlightColor);
		
		//get coords of upper-left corner of this square:
		Point coords = boardPanel.identifyCoordinates(mouseOverPosition);
		
		//place our highlighter over this square:
		highlight.setLocation(coords.x + boardPanel.getX(), coords.y + boardPanel.getY());
		
		
		setComponentZOrder(highlight, (getComponentZOrder(boardPanel) -1));
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
		if(pieceToDrop.getData().getPosition().equals(dropPosition))
		{
			//Set the piece in its original location.
			pieceToDrop.setLocation(pieceInHandOriginalLocation);
			audioPlayer.playDropSound();
		}
		else
		{
			StepData step = new StepData(pieceToDrop.getData(), dropPosition);
			if(engine.isValidStep(step))
			{	
				//Now update the engine.
				engine.takeStep(step);
	
				//Play an effect
				if(engine.lastStepWasCapture())
				{
					audioPlayer.playTrapSound();
				}
				else
				{
					audioPlayer.playDropSound();
				}
			}
			else
			{
				audioPlayer.playBzztSound();
				if(pieceToDrop.getData().getPosition() instanceof HandPosition)
				{
					//Do a bit of a hack to allow the piece to not drop incorrectly.
					pieceInHand = pieceToDrop;
					
					//allow other dragged pieces to display over this one.
					setComponentZOrder(pieceInHand, 0);	
				}
				else
				{
					pieceToDrop.setLocation(pieceInHandOriginalLocation);
				}
			}
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
				audioPlayer.playDropSound();
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
				audioPlayer.playDropSound();
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
	 * Modifies the UI to represent the passed in game state.
	 *  
	 * @param gameState
	 */
	private void displayGameState(GameState gameState)
	{	
		//Remove all pieces.
		clearPieces();
		
		//Then populate the buckets.
		populateBucket(goldBucketPanel, gameState.getGoldBucket());
		populateBucket(silverBucketPanel, gameState.getSilverBucket());
		
		//Populate the board
		populateBoard(gameState.getBoardData());
		
		//Place the piece in the hand.
		if(gameState.getPieceInHand() != null)
		{
			forcePieceInHand(gameState.getPieceInHand());
		}
		else
		{
			pieceInHand = null;
		}
		
		if(gameState.isGameOver())
		{
			//Display the Game winner results.
			statusPanel.setWinner(gameState.getGameWinner());
		}
		else
		{
			//Display the turn state.
			statusPanel.setTurnState(gameState.getCurrentPlayer());
			statusPanel.showEndTurnButton(gameState.canPlayerEndTurn(), gameState.isGameOn() && gameState.getNumSteps() > 0);
		}
		
		//Show the number of turns remaining
		remainingMovesPanel.setMoves(gameState.getRemainingMoves());
		
		//Determine whether to show the setup button or not.
		goldRandomSetupButton.setVisible(gameState.isSetupPhase() && 
				gameState.getCurrentPlayer() == GameConstants.GOLD && 
				gameState.getGoldBucket().size() > 0);
		
		silverRandomSetupButton.setVisible(gameState.isSetupPhase() && 
				gameState.getCurrentPlayer() == GameConstants.SILVER &&
				gameState.getSilverBucket().size() > 0);
		
		//Finally, repaint the gui.
		repaint();
	}

	/**
	 * Used when the game engine dictates that there is currently a piece in hand.
	 *
	 * @param pieceInHand2
	 */
	private void forcePieceInHand(PieceData piece)
	{
		//Create the piece.
		PiecePanel piecePanel = new PiecePanel(this, piece, loader);
		
		//Add it to the screen.
		addPiece(piecePanel);
		
		//Pick up the piece.
		pieceInHand = piecePanel;
		
		//ensure that the dragged piece is topmost and visible.
		setComponentZOrder(piecePanel, 0);
		
		//Set the original location to the hand.
		pieceInHandOriginalLocation = null;
		
		//Move the piece so that it is centered on the mouse.
		movePieceInHand(getMousePosition());
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

	/**
	 * TODO: Describe overridden method
	 *
	 * @param o
	 * @param arg
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		displayGameState(engine.getCurrentGameState());
	}
}

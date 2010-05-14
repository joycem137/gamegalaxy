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
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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
	
	private HighlightPanel		highlightMouse;
	private HighlightPanel		highlightKeyboard;
	private HighlightPanel		highlightOrigin;
	
	//Store the resource loader
	private ResourceLoader	loader;

	//Store information about the piece in hand.
	protected PiecePanel	pieceInHand;
	protected Point	pieceInHandOriginalLocation;
	
	//These points represent the Gold and Silver Buckets, for returning pieces
	private static Point goldRow = new Point(90,456);
	private static Point silverRow = new Point(799,456);

	//The upper-left corner of the board, in mouse coordinates
	private static Point boardTopLeft = new Point(210,43);

	//Offset beteen the mouse coordinates and the on-board coordinates of the upper left corner
	//Value = mouse location (210) minus board location (43)
	private static int boardEdgeOffset = 167; 
	
	//Size of the tiles
	private static int tileSize = 59;
	
	//Marks which square is currently highlighted by the keyboard movements
	private Point keyboardSelection = new Point();
	
	private SoundEffectPlayer audioPlayer;
	
	private boolean useMouse = true;
	
	//FIXME
	public ArimaaMenu menuBar;
	
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
				
		//Create the soundplayer
		audioPlayer = new SoundEffectPlayer();
		
		//Configure this panel
		setLayout(null);
		piecePanels = new Vector<PiecePanel>(16);

		//Create the background image.
		backgroundImage = loader.getResource("AppBackground");
		
		//Creates all of our panels, as well as the "Place Randomly" Buttons
		//The "End Turn / Undo Turn" buttons are created by StatusPanel instead
		createChildrenPanels();

		//Create the keyboard input
		createListener();
		
		//Create the mouse input
		addMouseListeners();
		
		//Set the window size
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
				//Also do nothing if we are not using mouse input
				if (me.getButton() != MouseEvent.BUTTON1  || !useMouse)
				{
					return;
				}
				
				//Pass the click to our input handlers
				Point mousePosition = new Point(me.getX(), me.getY());
				handleInputClick(mousePosition);				
			}

			public void mouseReleased(MouseEvent me)
			{
				//for now, do nothing unless the mouse press was a left-click.
				//Also do nothing if we are not using mouse input
				if (me.getButton() != MouseEvent.BUTTON1 || !useMouse)
				{
					return;
				}

				//Don't do anything if the mouse hasn't moved at all.
				if(pieceInHand != null && mouseDragged)
				{
					//Pass the click to our input handlers
					Point mousePosition = new Point(me.getX(), me.getY());
					handleInputClick(mousePosition);				
					
					//Reset the "mouse moved" flag.
					mouseDragged = false;
				}
			}
			
			public void mouseMoved(MouseEvent me)
			{
				//Only do this if we are using mouse input
				if(useMouse){
					//Pass the movement to our input handlers
					Point mousePosition = new Point(me.getX(), me.getY());
					handleInputHover(mousePosition);
				}
			}
			
			public void mouseDragged(MouseEvent me)
			{
				//Only do this if we are using mouse input
				if(useMouse){
					//Indicate that yes, the piece has been dragged
					//FIXME: Occurs even if moved 1 pixel
					mouseDragged = true;
					
					//Pass the movement to our input handlers
					Point mousePosition = new Point(me.getX(), me.getY());
					handleInputHover(mousePosition);
				}
			}
		};
		
		//Now add the mouse listeners
		addMouseListener(ma);
		addMouseMotionListener(ma);
	}
	
	/**
	 * Handles an input event that picks up or drops a piece
	 * 
	 * @param input - the location we are acting on
	 */
	protected void handleInputClick(Point input){
		if(pieceInHand != null)
		{
			//We already have a piece in hand.  Drop it.
			dropPieceInHand(input);
		}
		else
		{
			//We don't have a piece in hand.  See if there's one under the mouse to pick up:
			Component component = getComponentAt((int) input.getX(), (int) input.getY());

			if(component instanceof PiecePanel)
			{
				//We have a piece panel!  Recast the variable.
				PiecePanel piecePanel = (PiecePanel)component;

				if(engine.canPieceBeMoved(piecePanel.getData()))
				{						
					pickUpPiece(piecePanel, input);
				}
				else
				{
					audioPlayer.playBzztSound();
				}
			}
		}
	}//End handleInputClick

	/**
	 * Handles an input event that moves the selection cursor
	 * This highlights whether the move is valid
	 * And moves the piece in hand to hover over that spot
	 * 
	 * @param input - the location we are selecting
	 */
	protected void handleInputHover(Point input){
		if(pieceInHand != null)
		{
			movePieceInHand(input);
		}
		//Formerly else
		if (pieceInHand == null || !useMouse)
		{
			boolean hasHighlight = false;
			Component component = getComponentAt((int) input.getX(), (int) input.getY());
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
				}else if (engine.getCurrentGameState().isGameOn()){
					if (engine.getCurrentGameState().getBoardData().isPieceFrozen(pieceAt.getData()))
					{
						PiecePosition position = pieceAt.getData().getPosition();
						highlightSpace((BoardPosition)position, HighlightPanel.FROZEN);
						hasHighlight = true;
					}
				}
			}
			
			//If we are using keyboard input
			if (useMouse == false)
			{
				//If no other highlight, show the select-square highlight
				if (hasHighlight == false){
					BoardPosition mouseOverPosition = boardPanel.identifyBoardPosition((int) input.getX()-boardEdgeOffset, (int) input.getY());
					
					if (mouseOverPosition != null)
					{	
						highlightSelection(mouseOverPosition);
					}
				}else{
					//Otherwise, hide the previous select-square highlight
					//This ensures only one highlight is ever shown at a time
					hideKeyboardSelector();
				}
			}
			
			//If no square is highlighted, ensure the previous highlight is cleared
			if (hasHighlight == false)
			{
				clearHighlight();
			}
		}
	}//End of handleInputHover
		
	
	private void pickUpPiece(PiecePanel piecePanel, Point mousePosition)
	{
		//Play the pickup audio
		audioPlayer.playPickupSound();
		
		//Pick up the piece.
		pieceInHand = piecePanel;
		
		//ADD DOC
		if (!engine.getCurrentGameState().isSetupPhase() && engine.getCurrentGameState().getPushPosition() == null){
			//Mark that we have picked up the piece
			BoardPosition mouseOverPosition = boardPanel.identifyBoardPosition((int) mousePosition.getX()-boardEdgeOffset, (int) mousePosition.getY());
			highlightOrigin(mouseOverPosition);
		}
		
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
		//Offsets the location if using keyboard input
		if (!useMouse){
			mousePosition = new Point ((int) mousePosition.getX()+30, (int) mousePosition.getY()+30);
		}
		
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
	 * Update the origin highlighting
	 */
	
	private void highlightOrigin(BoardPosition mouseOverPosition){
		setHighlight(highlightOrigin, mouseOverPosition, HighlightPanel.GREEN);		
	}
	
	/**
	 * Update the highlight used for the keyboard selection cursor
	 */
	private void highlightSelection(BoardPosition mouseOverPosition)
	{
		//Set highlight color based on active player
		int highlightColor;
		if (engine.getCurrentGameState().getCurrentPlayer() == GameConstants.GOLD){
			highlightColor = HighlightPanel.GOLD;
		}else{
			highlightColor = HighlightPanel.SILVER;			
		}
		setHighlight(highlightKeyboard, mouseOverPosition, highlightColor);
	}
	
	/**
	 * Update the main highlight
	 *
	 * @param mouseOverPosition
	 */
	private void highlightSpace(BoardPosition mouseOverPosition, int highlightColor)
	{
		setHighlight(highlightMouse, mouseOverPosition, highlightColor);
	}

	/**
	 * This should be called via highlightSpace or highlight Selection
	 * Sets the specified HighlightPanel's position and image
	 * 
	 * @param thisHighlight     - the HighlightPanel to modify
	 * @param mouseOverPosition - where we want the panel to be
	 * @param highlightColor    - Integer index for color/image used
	 */
	private void setHighlight(HighlightPanel thisHighlight, BoardPosition mouseOverPosition, int highlightColor){
		//Set the color of the highlight.
		thisHighlight.setColor(highlightColor);
		
		//get coords of upper-left corner of this square:
		Point coords = boardPanel.identifyCoordinates(mouseOverPosition);
		
		//place our highlighter over this square:
		thisHighlight.setLocation(coords.x + boardPanel.getX(), coords.y + boardPanel.getY());
		
		setComponentZOrder(thisHighlight, (getComponentZOrder(boardPanel) -1));		
	}
	
	private void clearHighlightOrigin()
	{
		highlightOrigin.setLocation(0, 0);
		highlightOrigin.setColor(HighlightPanel.OFF);
	}
	
	/**
	 * Clear the highlight by turning it off and moving it off the screen.
	 *
	 */
	private void clearHighlight()
	{
		highlightMouse.setLocation(0, 0);
		highlightMouse.setColor(HighlightPanel.OFF);
	}
	
	/**
	 * ADD DOC
	 */
	private void hideKeyboardSelector(){
		highlightKeyboard.setLocation(0, 0);
		highlightKeyboard.setColor(HighlightPanel.OFF);		
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
		
		//Determine where we're going to drop the piece.
		PiecePosition dropPosition = getPiecePositionAt(dropLocation);
		
		//Check if we are moving the piece back to it's original location
		if(pieceToDrop.getData().getPosition().equals(dropPosition))
		{
			//Clear the origin highlight
			clearHighlightOrigin();
			
			//Set the piece in its original location.
			pieceToDrop.setLocation(pieceInHandOriginalLocation);
			audioPlayer.playDropSound();
		}
		else
		{
			//Check that this is a valid move
			StepData step = new StepData(pieceToDrop.getData(), dropPosition);
			if(engine.isValidStep(step))
			{	
				//Now update the engine.
				engine.takeStep(step);
	
				//ADD DOC
				if (pieceInHand == null && engine.getCurrentGameState().getPushPosition() == null)
					//Clear the origin highlight
					clearHighlightOrigin();
				
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
			//Otherwise, if not a valid move...
			else
			{
				audioPlayer.playBzztSound();
				
				//Check if we can put the piece back in our hand
				if(pieceToDrop.getData().getPosition() instanceof HandPosition)
				{
					//Do a bit of a hack to allow the piece to not drop incorrectly.
					pieceInHand = pieceToDrop;
					
					//allow other dragged pieces to display over this one.
					setComponentZOrder(pieceInHand, 0);	
				}
				//Otherwise just drop it back to it's original location
				else
				{
					//Clear the origin highlight
					clearHighlightOrigin();
					
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
		highlightMouse = new HighlightPanel(loader);
		highlightKeyboard = new HighlightPanel(loader);
		highlightOrigin = new HighlightPanel(loader);
		add(highlightMouse);
		add(highlightKeyboard);
		add(highlightOrigin);
		
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
		//If this is the setup phase, ensure we are using mouse input
		if (gameState.isSetupPhase())
		{
			setUseMouse(true);
		}
		
		//Ensure this isn't run during setup
		if (menuBar != null){
			//Toggle whether the menu's keyboardInput option is shown
			menuBar.setKeyboardEnabled(canUseKeyboard());
		}
		
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
		
		//If we are using keyboard input, refresh the position display
		//This ensures the cursor shows as the correct color for the new turn
		if (!useMouse){
			handleInputHover(keyboardSelection);
		}
		
		/*
		 * Reset focus on to this window
		 * 
		 * This must be done because focus is otherwise null
		 * and thus the keyboard listeners will never trigger
		 * (the focus having moved to the now-vanished EndTurn button)
		 */
		this.requestFocus();
		
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
		
		//Move the piece so that it is centered on the mouse, if using mouse input
		if (useMouse){
			movePieceInHand(getMousePosition());
		}else{
			movePieceInHand(keyboardSelection);			
		}
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
	
	
	
	

	
	/**
	 * Creates input/action maps for keyboard input
	 */
	
	/*
	 * These work in both Mouse and Keyboard input modes
	 * ESC    = Return pieceInHand to it's original spot on board
	 * DELETE = Return pieceInHand to the bucket (only works during Setup Phase)
	 * ENTER  = Enable/Disable Keyboard Input
	 * F1     = (DEBUG) Outputs mouse location and origin of pieceInHand to console
	 * 
	 * These commands only work in Keyboard input mode:
	 * ARROWS = Move selection
	 * SPACE  = Grab/Drop piece at this location
	 */
	
	private void createListener(){
		//Escape key
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "keyDrop");
		getActionMap().put("keyDrop", actionKeyDrop);
		
		//Delete key (treats Backspace as same key)
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "keyReturnPiece");
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "keyReturnPiece");
		getActionMap().put("keyReturnPiece", actionKeyReturn);
		
		//Slash key : keyToggle
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "keyToggleInput");
		getActionMap().put("keyToggleInput", actionKeyToggle);
		
		//F1 Key : keyDebug
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "keyDebug");
		getActionMap().put("keyDebug", actionKeyDebug);
		
		//Arrow keys
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "pressedUp");
		getActionMap().put("pressedUp", actionKeyUp);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "pressedDown");
		getActionMap().put("pressedDown", actionKeyDown);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "pressedLeft");
		getActionMap().put("pressedLeft", actionKeyLeft);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "pressedRight");
		getActionMap().put("pressedRight", actionKeyRight);
		
		//Spacebar key : keyClick
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "keyClick");
		getActionMap().put("keyClick", actionKeyClick);
		
		//Enter key : endTurn
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "keyEndTurn");
		getActionMap().put("keyEndTurn", actionEndTurn);
		
		//'Z' key : undoTurn
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "keyUndoTurn");
		getActionMap().put("keyUndoTurn", actionUndoTurn);
	}

	/**
	 *
	 */
	
	private void keyboardClick(){
		if (!useMouse){
			handleInputClick(keyboardSelection);
    	}
	}
	
	/**
	 * 
	 */
	private void returnPieceToBucket(){
		//Check that we have held piece to return
    	if (pieceInHand != null){
    		
    		//Returns the piece to the bucket it came from
    		if(pieceInHand.getData().getColor() == GameConstants.GOLD){
    			handleInputClick(goldRow);
    		}else{
    			handleInputClick(silverRow);
    		}
    	}
	}
	
	/**
	 * 
	 */
	private void dropHeldPiece(){
		//Check that we are holding a piece AND that it has a location to return to
		//The latter will only be false in the case of swaps
		//FIXME: Add support to Undo Swaps (blocked on issue 135)
    	if (pieceInHand != null && pieceInHandOriginalLocation != null){
    		handleInputClick(pieceInHandOriginalLocation);
    	}
	}
	
	/**
	 * 
	 * 
	 * @param xOffset
	 * @param yOffset
	 */
	private void movePiece(int xOffset, int yOffset){
		//Create the new location based on the offset
		Point newLocation = new Point((int) keyboardSelection.getX() + xOffset, (int) keyboardSelection.getY() + yOffset);

		//Ensure this moves to a valid BoardPosition
		BoardPosition newPosition = boardPanel.identifyBoardPosition((int) newLocation.getX()-boardEdgeOffset, (int) newLocation.getY());
		if (newPosition != null)
		{	
			keyboardSelection = newLocation;
			handleInputHover(keyboardSelection);
		}		
	}
	
	/**
	 * Check if we are using the mouse for input
	 * 
	 * @return true if using mouse, false if using keyboard
	 */
	
	public boolean isUsingMouse(){
		return useMouse;
	}
	
	/**
	 * TODO
	 * 
	 */
	public boolean canUseKeyboard(){
		//FIXME: Mouse doesn't currently support the setup phase,
		// as it cannot select from the bucket yet
		return !engine.getCurrentGameState().isSetupPhase();
	}
	
	/**
	 * Set whether or not we are using mouse input
	 * Will force this to "true" if we are still in the setup phase
	 * 
	 * @param willUseMouse
	 */
	public void setUseMouse(boolean willUseMouse){	
		//If we cannot currently use the mouse, then always set to true
		if (!canUseKeyboard()){
			willUseMouse = true;
		}
		
    	if (willUseMouse){
    		//Set the input to mouse
    		useMouse = true;
    		
    		//Hide the keyboard selection indicator
    		hideKeyboardSelector();
    		
    		//If possible, drop any piece that was picked up
    		dropHeldPiece();
    	}else{
    		//Set the input to keyboard (i.e. disable mouse)
    		useMouse = false;
    		
    		//Start with the selected square being the top left of the board
    		keyboardSelection = boardTopLeft;
    		
    		//Highlight the selected square
    		handleInputHover(keyboardSelection);
    	}
	}
	
	private void toggleInputMode(){
    	//Toggle mouse input
    	if (useMouse){
    		setUseMouse(false);
    	}else{
    		setUseMouse(true);
    	}
	}
	
	/*
	 * ACTION DEFINITIONS FOR KEYBOARD INPUT
	 */
	
	//NOTE: This action is solely for debugging purposes
	Action actionKeyDebug = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {			
			if (pieceInHandOriginalLocation != null){
				Point fromPosition = pieceInHandOriginalLocation;	
				System.out.println("Piece is from: " + fromPosition.getX() + ":" + fromPosition.getY());
			}

			//This outputs the mouse location
			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			System.out.println("Mouse is at: " + mousePosition.getX() + ":" + mousePosition.getY());
		}
	};
	
	Action actionKeyReturn = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	returnPieceToBucket();
	    }
	};
	
	Action actionKeyToggle = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	//Toggle between mouse and keyboard input
	    	toggleInputMode();
	    }
	};
	
	Action actionKeyDrop = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	//Return any held piece to it's original location
	    	dropHeldPiece();
	    }
	};
	
	/*
	 * THESE KEYS ONLY GET USED WHEN IN KEYBOARD INPUT MODE
	 */

	Action actionKeyRight = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	movePiece(+tileSize,0);
	    }
	};
	
	Action actionKeyLeft = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	movePiece(-tileSize,0);
	    }
	};

	Action actionKeyDown = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	movePiece(0,+tileSize);
	    }
	};
	

	Action actionKeyUp = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	movePiece(0,-tileSize);
	    }
	};
	

	Action actionKeyClick = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	//Click the selected space
	    	keyboardClick();
	    }
	};
	

	Action actionEndTurn = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	//Clicks the "End Turn" button
	    	statusPanel.clickEndTurn();
	    }
	};
	
	Action actionUndoTurn = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	    	//Clicks the "Undo Turn" button
	    	statusPanel.clickUndoTurn();
	    }
	};	

}

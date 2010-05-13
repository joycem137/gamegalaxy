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
package gamegalaxy.games.arimaa.data;

import gamegalaxy.games.arimaa.engine.MoveGenerator;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This class stores all of the information required to completely recreate the state of
 * the game at any point in time.
 */
public final class GameState
{
	private BoardData	board;
	private List<PieceData>	goldBucket;
	private List<PieceData>	silverBucket;
	
	private int	playerTurn;
	private int	numSteps;
	private int	phase;
	
	private BoardPosition	pushPosition;
	private BoardPosition	pullPosition;
	private StepData	lastStep;
	private int	winner;
	
	private List<PieceData>	pieces;
	private boolean	lastStepWasCapture;
	private MoveGenerator	moveGenerator;
	private PieceData	pieceInHand;

	public GameState() 
	{
		pushPosition = null;
		pullPosition = null;
		lastStep = null;
		
		board = null;
		pieces = null;
		goldBucket = null;
		silverBucket = null;

		phase = 0;
		playerTurn = 0;
		numSteps = 0;
		
		lastStepWasCapture = false;
		
		moveGenerator = new MoveGenerator(this);
	}
	
	/**
	 * Creates and returns a copy of this GameState.
	 * @return
	 */
	public GameState copy()
	{
		GameState newState = new GameState();
		
		newState.board = new BoardData();
		newState.pieces = new Vector<PieceData>(32);
		newState.goldBucket = new Vector<PieceData>(16);
		newState.silverBucket = new Vector<PieceData>(16);
		
		Iterator<PieceData> iterator = pieces.iterator();
		while (iterator.hasNext())
		{
			PieceData newPiece = iterator.next().copy();
			newState.pieces.add(newPiece);
			
			if (newPiece.getPosition() instanceof BoardPosition)
			{
				newState.board.placePiece(newPiece, (BoardPosition)newPiece.getPosition());
			}
			if (newPiece.getPosition() instanceof BucketPosition)
			{
				BucketPosition newBucket = (BucketPosition)newPiece.getPosition();
				if (newBucket.getColor() == GameConstants.SILVER)
				{
					newState.silverBucket.add(newPiece);
				}
				else
				{
					newState.goldBucket.add(newPiece);
				}
			}
			if (newPiece.getPosition() instanceof HandPosition)
			{
				newState.pieceInHand = newPiece;
			}
		}
		
		newState.playerTurn = playerTurn;
		newState.numSteps = numSteps;
		newState.phase = phase;
		
		newState.pushPosition = pushPosition;
		newState.pullPosition = pullPosition;
		
		if (lastStep != null)
		{
			newState.lastStep = lastStep.copy();
		}
			
		newState.winner = winner;
		
		newState.lastStepWasCapture = lastStepWasCapture;
		
		newState.moveGenerator = new MoveGenerator(newState);
				
		return newState;
	}
	
	/**
	 * Initializes a GameState to beginning-of-new-game settings; clears the board, resets
	 *  the buckets, and sets the GameState to the Setup phase.
	 */
	public void initializeGameState()
	{
		// Start with no forced push/pull scenario.
		pushPosition = null;
		pullPosition = null;
		lastStep = null;

		// Start in the setup phase.
		phase = GameConstants.SETUP_PHASE;

		//The first player is gold.
		playerTurn = GameConstants.GOLD;
		
		//Nobody was recently captured:
		lastStepWasCapture = false;

		//Create the board.
		board = new BoardData();

		//Create all of the pieces for the board.
		createPieces();

		// Initialize our buckets.
		createBuckets();

		//Reset the number of moves to 0
		numSteps = 0;
		
		//Reset the move generator
		moveGenerator.reset();
	}
	
	/**
	 * Creates the buckets for the game and adds all pieces to the buckets for
	 * initialization
	 * 
	 */
	private void createBuckets()
	{
		// Initialize our lists.
		goldBucket = new Vector<PieceData>(16);
		silverBucket = new Vector<PieceData>(16);

		//Create positions for both buckets.
		BucketPosition goldBucketPosition = new BucketPosition(GameConstants.GOLD);
		BucketPosition silverBucketPosition = new BucketPosition(GameConstants.SILVER);
		
		// Populate our lists.
		Iterator<PieceData> iterator = pieces.iterator();
		while (iterator.hasNext())
		{
			PieceData pieceData = iterator.next();
			if (pieceData.getColor() == GameConstants.GOLD)
			{
				goldBucket.add(pieceData);
				pieceData.setPosition(goldBucketPosition);
			}
			else
			{
				silverBucket.add(pieceData);
				pieceData.setPosition(silverBucketPosition);
			}
		}
	}

	/**
	 * Create all of the pieces for the game.
	 * 
	 */
	private void createPieces()
	{
		pieces = new Vector<PieceData>(32);

		// Create 8 rabbits of each color
		for (int i = 0; i < 8; i++)
		{
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.RABBIT));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.RABBIT));
		}

		// Create 2 each of horses, cats, and dogs.
		for (int i = 0; i < 2; i++)
		{
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.CAT));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.CAT));
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.DOG));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.DOG));
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.HORSE));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.HORSE));
		}

		// Create 1 camel and elephant of each color
		pieces.add(new PieceData(GameConstants.GOLD, PieceData.CAMEL));
		pieces.add(new PieceData(GameConstants.SILVER, PieceData.CAMEL));
		pieces.add(new PieceData(GameConstants.GOLD, PieceData.ELEPHANT));
		pieces.add(new PieceData(GameConstants.SILVER, PieceData.ELEPHANT));
	}

	/**
	 * Does a deep copy of the indicated list.
	 *
	 * @param goldBucket
	 * @return
	 */
	private List<PieceData> copyList(List<PieceData> bucketData)
	{
		List<PieceData> newList = new Vector<PieceData>(bucketData.size());
		Iterator<PieceData> iterator = bucketData.iterator();
		while(iterator.hasNext())
		{
			newList.add(iterator.next().copy());
		}
		return newList;
	}

	/**
	 * Returns the value of the current player.
	 *
	 * @return
	 */
	public int getCurrentPlayer()
	{
		return playerTurn;
	}

	/**
	 * Returns true if the game has been won.
	 *
	 * @return
	 */
	public boolean isGameOver()
	{
		return phase == GameConstants.GAME_WON;
	}

	/**
	 * Return the winner of the game, if applicable.
	 *
	 * @return
	 */
	public int getGameWinner()
	{
		return winner;
	}

	/**
	 * Return the list of pieces in the gold bucket.
	 *
	 * @return
	 */
	public List<PieceData> getGoldBucket()
	{
		return goldBucket;
	}

	/**
	 * Returns true if we are currently in the setup phase of the game.
	 *
	 * @return
	 */
	public boolean isSetupPhase()
	{
		return phase == GameConstants.SETUP_PHASE;
	}

	/**
	 * Returns the list of pieces in the silver bucket.
	 *
	 * @return
	 */
	public List<PieceData> getSilverBucket()
	{
		return silverBucket;
	}

	/**
	 * Returns the representation of the board in the current state.
	 *
	 * @return
	 */
	public BoardData getBoardData()
	{
		return board;
	}

	public int getNumSteps() 
	{
		return numSteps;
	}

	public boolean isGameOn() 
	{
		return phase == GameConstants.GAME_ON;
	}

	public BoardPosition getPushPosition()
	{
		return pushPosition;
	}
	
	public BoardPosition getPullPosition()
	{
		return pullPosition;
	}
	
	/**
	 * Returns true if the current player is able to end their turn.
	 *
	 * @return
	 */
	public boolean canPlayerEndTurn()
	{
		if(phase == GameConstants.SETUP_PHASE)
		{
			List<PieceData> bucket;
			if(playerTurn == GameConstants.GOLD)
			{
				bucket = goldBucket;
			}
			else
			{
				bucket = silverBucket;
			}
			return bucket.isEmpty();
		}
		else if(phase == GameConstants.GAME_ON)
		{
			return numSteps >= 1 && pushPosition == null && pieceInHand == null;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns true if the last step was a capture step.
	 * Used for handling playing the correct audio.
	 *
	 * @return
	 */
	public boolean lastStepWasCapture()
	{
		return lastStepWasCapture;
	}

	/**
	 * Return the last step made.
	 *
	 * @return
	 */
	public StepData getLastStep()
	{
		return lastStep;
	}

	/**
	 * Return the piece that should be placed in the hand.
	 *
	 * @return
	 */
	public PieceData getPieceInHand()
	{
		return pieceInHand;
	}

	/**
	 * Return the number of moves remaining in this turn.
	 * 
	 * @return
	 */
	public int getRemainingMoves()
	{
		if(phase == GameConstants.GAME_ON)
		{
			return 4 - numSteps;
		}
		else
		{
			return 0;
		}
	}

	public void doRandomSetup()
	{
		// Abort if this isn't the setup phase.
		if (phase != GameConstants.SETUP_PHASE) return;

		// Grab the appropriate bucket.
		List<PieceData> bucket;
		int firstValidRow;
		if (playerTurn == GameConstants.GOLD)
		{
			bucket = goldBucket;
			firstValidRow = 6;
		}
		else
		{
			bucket = silverBucket;
			firstValidRow = 0;
		}

		// Randomly distribute the pieces using the most straight forward
		// algorithm.
		Iterator<PieceData> iterator = bucket.iterator();
		while (iterator.hasNext())
		{
			PieceData piece = iterator.next();

			// Find a valid position.
			BoardPosition position;
			do
			{
				int randomRow = firstValidRow + getRandomInt(0, 1);
				int randomCol = getRandomInt(0, 7);
				position = new BoardPosition(randomRow, randomCol);
			} while (board.isOccupied(position));

			// Now place the piece.
			board.placePiece(piece, position);
		}

		// Remove all the pieces from the bucket.
		bucket.clear();

	}

	/**
	 * Returns true if the indicated piece is capable of moving.
	 *
	 * @param piece
	 * @return
	 */
	public boolean canPieceBeMoved(PieceData piece)
	{
		List<StepData> steps = moveGenerator.generateSteps(piece);
		return steps.size() > 0;
	}

	/**
	 * Returns true if this is a possible step to take
	 *
	 * @param step
	 * @return
	 */
	public boolean isValidStep(StepData step)
	{
		List<StepData> steps = moveGenerator.generateSteps(step.getPiece());
		return steps.contains(step);
	}
	
	/**
	 * Returns true if the player is trying to place a piece on the enemy's row instead of theirs
	 * 
	 * @param step
	 * @return
	 */
	public boolean isEnemyRow(StepData step)
	{
		List<StepData> steps = moveGenerator.listEnemyRows(step.getPiece());
		return steps.contains(step);		
	}

	/**
	 * Submit the indicated move to the game engine for implementation. Upon completion of the move, the engine will ask the GUI to update to the latest game
	 * state.
	 * 
	 * @param piece
	 * @param originalSpace
	 * @param newSpace
	 */
	public void takeStep(StepData step)
	{
		//Abort if this is not a valid step to take.
		if(!isValidStep(step)) return;
		
		lastStepWasCapture = false;
		
		PieceData piece = step.getPiece();
		PiecePosition destination = step.getDestination();
		
		//Check if we are placing a piece from the hand
		if(piece.equals(pieceInHand))
		{
			//Moving the piece in hand to a new location.
			board.placePiece(piece, (BoardPosition) destination);
			
			//Clear the piece in hand
			pieceInHand = null;
			
			//Increment the number of moves
			numSteps++;
		}
		//Check if we are placing a piece from the setup buckets on to the board
		else if (piece.getPosition() instanceof BucketPosition && destination instanceof BoardPosition)
		{
			// Moving from bucket to board.
			BoardPosition boardDestination = (BoardPosition) destination;
			BucketPosition bucketSource = (BucketPosition) piece.getPosition();
			
			// First remove the piece from the bucket.
			removePieceFromBucket(piece, bucketSource);

			//Swap pieces if there's already one on the board.
			if(board.isOccupied(boardDestination))
			{
				PieceData swapPiece = board.getPieceAt(boardDestination);
				
				//Remove the piece from the board.
				board.removePiece(boardDestination);
				
				//Place it in the bucket

				addToBucket(swapPiece, bucketSource);
			}
			
			// Now place the piece on the board in the correct space.
			board.placePiece(piece, (BoardPosition) destination);
		}
		//Check if we are placing a piece on the board
		else if (destination instanceof BoardPosition)
		{
			// Moving from board to board
			BoardPosition originalPosition = (BoardPosition) piece.getPosition();
			BoardPosition newPosition = (BoardPosition) destination;
			
			//Remove the piece from the original location
			board.removePiece(originalPosition);
			
			//Handle occupied spaces
			PieceData swapPiece = null;
			if(board.isOccupied(newPosition))
			{
				//Get the piece
				swapPiece = board.getPieceAt(newPosition);
				
				//Remove it from the board
				board.removePiece(newPosition);
			}

			//Move the piece to its new location.
			board.placePiece(piece, newPosition);
			
			//Process the effects of the move.
			if(phase == GameConstants.GAME_ON)
			{
				// Increment the number of moves
				numSteps++;
				
				//Handle the piece that already was in this spot, if appropriate
				if(swapPiece != null)
				{
					//Put the piece in hand.
					pieceInHand = swapPiece;
					
					//Reset the piece's old position
					swapPiece.setPosition(new HandPosition(newPosition));
					
					//Clear the push/pull values for normal moves
					pushPosition = null;
					pullPosition = null;
				}
				else
				{
					//Handle "normal" push/pull moves.
					if (piece.getColor() != playerTurn)
					{
						if (pullPosition != null && pullPosition.equals(newPosition))
						{
							// This cannot be a push or a pull.
							pullPosition = null;
							pushPosition = null;
						}
						else
						{
							// This is a push.
							pushPosition = originalPosition;
						}
					}
					else
					{
						// This is a move of this player's color.
						if (pushPosition == null)
						{
							// The last move was *not* a push position. You can
							// record the piece movement.
							pullPosition = originalPosition;
						}
						else
						{
							// The last move was a push. Reset it.
							pushPosition = null;
						}
					}
				}
				
			}
			else if(phase == GameConstants.SETUP_PHASE && swapPiece != null)
			{
				//Handle the piece to swap.
				board.placePiece(swapPiece, originalPosition);
			}
			
			lastStep = step;
		}
		else
		{
			// Moving from board to bucket.
			BoardPosition originalPosition = (BoardPosition) piece.getPosition();
			BucketPosition newPosition = (BucketPosition) destination;

			// Remove the piece from the board.
			board.removePiece(originalPosition);

			// Add the piece to the bucket.
			addToBucket(piece, newPosition);
		}
		
		if (phase == GameConstants.GAME_ON)
		{
			// Check the traps to see if there are pieces in them and if
			// they are dead, as long as we are not in the middle of a push
			// or pull move.
			if (canPlayerEndTurn())
			{
				checkTheTraps();
			}
		}
		
		//Reset the move generator.
		moveGenerator.reset();
	}

	public void endMove()
	{	
		// Check to be sure ending the move is legal.
		if (canPlayerEndTurn())
		{
			assert pushPosition != null;
		
			// Reset the pulled pieces.
			pullPosition = null;
			lastStep = null;
		
			// Check to see if this ends the game.
			if (phase == GameConstants.GAME_ON)
			{
				checkForWinner();
			}
				
			// Switch the turn.
			playerTurn = (playerTurn + 1) % 2;
		
			// If we're back to gold, the game phase has changed.
			if (playerTurn == GameConstants.GOLD && phase == GameConstants.SETUP_PHASE)
			{
				phase = GameConstants.GAME_ON;
			}
		
			numSteps = 0;
			
			moveGenerator.reset();
			
			// If the player has no valid moves at the beginning of the turn, they lose.
			if (phase == GameConstants.GAME_ON && !playerHasValidMoves())
			{
				setGameWinner((playerTurn + 1) % 2);
			}
		}
	}

	/**
	 * Evaluate the current game situation to determine if there has been a winner.
	 *  This is performed after a player ends their turn, but before the opponent's turn
	 *  begins.
	 */
	private void checkForWinner()
	{
		boolean aliveGoldRabbit = false;
		boolean aliveSilverRabbit = false;
		boolean winningGoldRabbit = false;
		boolean winningSilverRabbit = false;
		
		Iterator<PieceData> iterator = pieces.iterator();
		while (iterator.hasNext())
		{
			PieceData piece = iterator.next();
	
			// Check through our pieces for rabbits.
			if (piece.getValue() == PieceData.RABBIT)
			{
				if (piece.getPosition() != null)
				{
					// Check if the rabbit is still on the board.
					if (piece.getPosition() instanceof BoardPosition)
					{
						BoardPosition position = (BoardPosition) piece.getPosition();
						if (piece.getColor() == GameConstants.GOLD)
						{
							aliveGoldRabbit = true;
							if (position.getRow() == 0)
							{
								winningGoldRabbit = true;
							}
						}
						else
						{
							aliveSilverRabbit = true;
							if (position.getRow() == 7)
							{
								winningSilverRabbit = true;
							}
						}
					}
				}
			}
		}
	
		// Now that we've checked through our rabbits, see if the game is over.
		
		// If both players have advanced rabbits to the other side, the current player wins.	
		if (winningGoldRabbit && winningSilverRabbit)
		{
			setGameWinner(playerTurn);
		}
		else
		{
			// If only one rabbit has made it across, the player whose color it is wins.
			if (winningGoldRabbit)
			{
				setGameWinner(GameConstants.GOLD);
			}
			
			if (winningSilverRabbit)
			{
				setGameWinner(GameConstants.SILVER);
			}
		}
		
		if (!winningGoldRabbit && !winningSilverRabbit)
		{
			// If all sixteen rabbits have been captured, the current player wins.
			if (!aliveGoldRabbit && !aliveSilverRabbit)
			{
				setGameWinner(playerTurn);
			}
			else
			{
				// If only one player's rabbits have been captured, the other player wins.
				if (!aliveGoldRabbit)
				{
					setGameWinner(GameConstants.SILVER);
				}
				
				if (!aliveSilverRabbit)
				{
					setGameWinner(GameConstants.GOLD);
				}
			}
		}
	}

	/**
	 * Checks to see if the current player has any valid moves at all.
	 * 
	 * @return <code>true</code> if at least one piece can be moved.
	 */
	public boolean playerHasValidMoves()
	{
		boolean validMoveFound = false;
		Iterator<PieceData> iterator = pieces.iterator();
		
		while (iterator.hasNext() && !validMoveFound)
		{
			PieceData piece = iterator.next();
			if (canPieceBeMoved(piece))
			{
				validMoveFound = true;
			}
		}
		
		return validMoveFound;
	}
	
	/**
	 * Check all of the traps to see if there are any pieces that need to be captured.
	 * 
	 */
	private void checkTheTraps()
	{
		Iterator<BoardPosition> iterator = BoardData.getTrapPosition().iterator();
		while (iterator.hasNext())
		{
			BoardPosition trapPosition = iterator.next();
			if (board.isOccupied(trapPosition))
			{
				PieceData trappedPiece = board.getPieceAt(trapPosition);
				boolean pieceIsDead = true;
	
				// Check for allies in each direction:
				List<BoardPosition> adjacentSpaces = trapPosition.getAdjacentSpaces();
				Iterator<BoardPosition> adjacentIterator = adjacentSpaces.iterator();
				while (adjacentIterator.hasNext())
				{
					BoardPosition space = adjacentIterator.next();
					if (board.isOccupied(space))
					{
						PieceData adjacentPiece = board.getPieceAt(space);
						if (adjacentPiece.getColor() == trappedPiece.getColor())
						{
							// We found an ally! We're safe!
							pieceIsDead = false;
						}
					}
				}
	
				// Now kill the piece.
				if (pieceIsDead)
				{
					// Remove the piece from the board.
					board.removePiece(trapPosition);
	
					// Move the piece to the appropriate bucket.
					if(trappedPiece.getColor() == GameConstants.GOLD)
					{
						addToBucket(trappedPiece, new BucketPosition(GameConstants.SILVER));
					}
					else
					{
						addToBucket(trappedPiece, new BucketPosition(GameConstants.GOLD));
					}
					
					//Set the flag
					lastStepWasCapture = true;
				}
			}
		}
	}

	private void addToBucket(PieceData piece, BucketPosition position)
	{
		List<PieceData> bucket;
		if (position.getColor() == GameConstants.GOLD)
		{
			bucket = goldBucket;
		}
		else
		{
			bucket = silverBucket;
		}
	
		bucket.add(piece);
		piece.setPosition(position);
	}

	private void removePieceFromBucket(PieceData piece, BucketPosition bucketPosition)
	{
		List<PieceData> bucket;
		if (bucketPosition.getColor() == GameConstants.GOLD)
		{
			bucket = goldBucket;
		}
		else
		{
			bucket = silverBucket;
		}
	
		int pieceIndex = bucket.indexOf(piece);
		bucket.remove(pieceIndex);
		piece.setPosition(null);
	}

	/**
	 * Return a random integer from min to max, inclusive.
	 * 
	 * @param min
	 *            The lowest possible integer this method can return.
	 * @param max
	 *            The highest possible integer this method can return.
	 * @return
	 */
	private int getRandomInt(int min, int max)
	{
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	/**
	 * Set the game phase to GAME_WON and set the winner to the indicated player.
	 * 
	 * @param player
	 */
	private void setGameWinner(int player)
	{
		phase = GameConstants.GAME_WON;
		winner = player;
	}

}

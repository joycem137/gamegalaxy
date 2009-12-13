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
	private int	numMoves;
	private int	phase;
	
	private BoardPosition	pushPosition;
	private BoardPosition	pullPosition;
	private PieceData	lastPieceMoved;
	private int	winner;
	
	private List<PieceData>	pieces;

	public GameState() 
	{
		// Start with no forced push/pull scenario.
		pushPosition = null;
		pullPosition = null;
		lastPieceMoved = null;

		// Start in the setup phase.
		phase = GameConstants.SETUP_PHASE;

		//The first player is gold.
		playerTurn = GameConstants.GOLD;

		//Create the board.
		board = new BoardData();

		//Create all of the pieces for the board.
		createPieces();

		// Initialize our buckets.
		createBuckets();

		//Reset the number of moves to 0
		numMoves = 0;
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

	public int getNumMoves() 
	{
		return numMoves;
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

	public PieceData getLastPieceMoved()
	{
		return lastPieceMoved;
	}
	
	public void endMove()
	{	
		// You can't end the turn in the middle of a push
		assert pushPosition != null;

		// Reset the pulled pieces.
		pullPosition = null;
		lastPieceMoved = null;

		// Switch the turn.
		playerTurn = (playerTurn + 1) % 2;

		// If we're back to gold, the game phase has changed.
		if (playerTurn == GameConstants.GOLD)
		{
			phase = GameConstants.GAME_ON;
		}

		numMoves = 0;
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
				position = new BoardPosition(randomCol, randomRow);
			} while (board.isOccupied(position));

			// Now place the piece.
			board.placePiece(piece, position);
		}

		// Remove all the pieces from the bucket.
		bucket.clear();

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
	 * Submit the indicated move to the game engine for implementation. Upon completion of the move, the engine will ask the GUI to update to the latest game
	 * state.
	 * 
	 * @param piece
	 * @param originalSpace
	 * @param newSpace
	 */
	public void takeStep(StepData step)
	{
		PieceData piece = step.getPiece();
		PiecePosition destination = step.getDestination();
		
		if (piece.getPosition() instanceof BucketPosition && destination instanceof BoardPosition)
		{
			// Moving from bucket to board.
			BoardPosition boardDestination = (BoardPosition) destination;
			BucketPosition bucketSource = (BucketPosition) piece.getPosition();
			
			// First remove the piece from the bucket.
			removePieceFromBucket(piece, bucketSource);

			//Determine if there is a piece already on the board.
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
		else if (destination instanceof BoardPosition)
		{
			// Moving from board to board
			BoardPosition originalPosition = (BoardPosition) piece.getPosition();
			BoardPosition newPosition = (BoardPosition) destination;
			
			//Remove the piece from the original location
			board.removePiece(originalPosition);
			
			//Do a swap (Must be changed when handling pushes and pulls by dropping pices onto others)
			if(board.isOccupied(newPosition))
			{
				//Get the piece
				PieceData swapPiece = board.getPieceAt(newPosition);
				
				//Remove it from the board
				board.removePiece(newPosition);
				
				//Place it on the original location
				board.placePiece(swapPiece, originalPosition);
			}

			board.placePiece(piece, newPosition);

			if (phase == GameConstants.GAME_ON)
			{
				// Increment the number of moves
				numMoves++;

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
			lastPieceMoved = piece;
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
			// they are dead.
			checkTheTraps();

			// Check to see if anyone has won the game.
			checkForWinner();
		}
	}

	/**
	 * Evaluate the current game situation to determine if there has been a winner.
	 * 
	 */
	private void checkForWinner()
	{
		boolean foundGoldRabbit = false;
		boolean foundSilverRabbit = false;
		Iterator<PieceData> iterator = pieces.iterator();
		while (iterator.hasNext())
		{
			PieceData piece = iterator.next();

			// If we find a rabbit piece, check to see if it is in the back row.
			if (piece.getValue() == PieceData.RABBIT)
			{
				// Check if the rabbit is in the back row.
				if (piece.getPosition() != null)
				{
					if (piece.getPosition() instanceof BoardPosition)
					{
						BoardPosition position = (BoardPosition) piece.getPosition();
						if (piece.getColor() == GameConstants.GOLD)
						{
							foundGoldRabbit = true;
							if (position.getRow() == 0)
							{
								// Gold won!!!
								setGameWinner(GameConstants.GOLD);
							}
						}
						else
						{
							foundSilverRabbit = true;
							if (position.getRow() == 7)
							{
								// Silver won!
								setGameWinner(GameConstants.SILVER);
							}
						}
					}
				}
			}
		}

		// If we made it here, make sure we found rabbits of each color.
		if (!foundGoldRabbit)
		{
			setGameWinner(GameConstants.SILVER);
		}
		else if (!foundSilverRabbit)
		{
			setGameWinner(GameConstants.GOLD);
		}
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
					addToBucket(trappedPiece, new BucketPosition(trappedPiece.getColor()));
				}
			}
		}
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
			return numMoves >= 1 && pushPosition == null;
		}
		else
		{
			return false;
		}
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
			return 4 - numMoves;
		}
		else
		{
			return 0;
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

}

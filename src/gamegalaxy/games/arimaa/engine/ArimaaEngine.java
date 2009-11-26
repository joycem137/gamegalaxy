package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.PiecePosition;
import gamegalaxy.games.arimaa.gui.ArimaaUI;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class ArimaaEngine
{
	private static final int	SETUP_PHASE	= 0;
	private static final int	GAME_ON = 1;
	
	//Store our data.
	private List<PieceData>	pieces;
	private int	playerTurn;
	private BoardData	board;
	private int	phase;

	private List<PieceData>	goldBucket;
	private List<PieceData> silverBucket;

	//Store a link to the UI
	private ArimaaUI	gui;

	private int	numMoves;
	private PiecePosition	pushPosition;

	public ArimaaEngine()
	{
		//Start with no forced push/pull scenario.
		pushPosition = null;
		
		phase = SETUP_PHASE;
		
		playerTurn = GameConstants.GOLD;
	
		board = new BoardData();
		
		createPieces();
		
		//Initialize our buckets.
		createBuckets();
		
		numMoves = 0;
	}
	
	public void linkGUI(ArimaaUI gui)
	{
		this.gui = gui;
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void createBuckets()
	{
		//Initialize our lists.
		goldBucket = new Vector<PieceData>(16);
		silverBucket = new Vector<PieceData>(16);
		
		//Populate our lists.
		Iterator<PieceData> iterator = pieces.iterator();
		while(iterator.hasNext())
		{
			PieceData pieceData = iterator.next();
			if(pieceData.getColor() == GameConstants.GOLD)
			{
				goldBucket.add(pieceData);
			}
			else
			{
				silverBucket.add(pieceData);
			}
		}
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void createPieces()
	{
		pieces = new Vector<PieceData>(32);
		
		//Create 8 rabbits of each color
		for(int i = 0; i < 8; i++)
		{
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.RABBIT));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.RABBIT));
		}
		
		//Create 2 each of horses, cats, and dogs.
		for(int i = 0; i < 2; i++)
		{
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.CAT));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.CAT));
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.DOG));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.DOG));
			pieces.add(new PieceData(GameConstants.GOLD, PieceData.HORSE));
			pieces.add(new PieceData(GameConstants.SILVER, PieceData.HORSE));
		}
		
		//Create 1 camel and elephant of each color
		pieces.add(new PieceData(GameConstants.GOLD, PieceData.CAMEL));
		pieces.add(new PieceData(GameConstants.SILVER, PieceData.CAMEL));
		pieces.add(new PieceData(GameConstants.GOLD, PieceData.ELEPHANT));
		pieces.add(new PieceData(GameConstants.SILVER, PieceData.ELEPHANT));
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public List<PieceData> getPieces()
	{
		return pieces;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @return
	 */
	public boolean canPieceBeMoved(PieceData piece)
	{
		//If we've exceeded our moves, we can't do this.
		if(numMoves >= 4) return false;
		
		if(phase == SETUP_PHASE)
		{
			//If we've got the wrong color, we can't move this piece.
			return piece.getColor() == playerTurn;
		}
		else if(phase == GAME_ON)
		{	
			//Get the position of the piece we're looking at.
			PiecePosition piecePosition = piece.getPosition();
			
			//Handle the case where our piece does not have a position.
			if(piecePosition == null) return false;
			
			//Make sure that this piece has somewhere to move
			if(!board.pieceHasSpaceToMoveInto(piece)) return false;
			
			//Check the color of this piece
			if(piece.getColor() != playerTurn)
			{
				//If we have a forced move, you can't do this.
				if(pushPosition != null) return false;
				
				//The piece can only be moved if there are at least two moves left.
				if(numMoves > 2) return false;
				
				//Get a list of all the pieces next to this one.
				List<PieceData> adjacentPieces = board.getAdjacentPieces(piece);
				
				//You can't select a piece of the opposite color if it has no adjacent pieces.
				if(adjacentPieces.size() == 0) return false;
				
				//This piece must be next to a piece of the opposite color and of a higher value
				Iterator<PieceData> iterator = adjacentPieces.iterator();
				while(iterator.hasNext())
				{
					PieceData adjacentPiece = iterator.next();
					
					//Check to see if we have a piece of the appropriate properties next to us.
					if(adjacentPiece.getColor() != piece.getColor() &&
						adjacentPiece.getValue() > piece.getValue())
					{
						//Make sure the adjacent piece is not frozen.
						if(!board.isPieceFrozen(adjacentPiece))
						{
							return true;
						}
					}	
				}
				
				//If we made it this far, that means that none of the adjacent pieces are correct.
				return false;
			}
			else
			{
				//Verify that the piece is not frozen
				if(board.isPieceFrozen(piece)) return false;
				
				//if we have a pushPosition, verify that we can move into that spot.
				if(pushPosition != null)
				{
					//This piece can move into that space.
					return piece.getPosition().distanceFrom(pushPosition) == 1;
				}
				else
				{
					return true;
				}
			}
		}
		else
		{
			//Cannot move piece during other phases of the game.
			return false;
		}
	}

	/**
	 * Checks to see if the selected piece can be legally moved from their original space to
	 * the new space selected.
	 *
	 * @param piece			PieceData of the piece being checked.
	 * @param originalSpace	PiecePosition where the selected piece started from.
	 * @param newSpace		target PiecePosition we want to move to.
	 * @return	true if a valid move can be made, otherwise false.
	 */
	public boolean isValidMove(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
		//Abort stupid cases.
		if(newSpace == null) return false;
		
		//And cases where the piece isn't allowed to move
		if(!canPieceBeMoved(piece)) return false;
		
		//Let's actually see if the move is valid.
		if(phase == SETUP_PHASE)
		{
			//Handle the bucket case:
			if(newSpace.isABucket())
			{
				//Verify that the piece's color matches the color of the bucket.
				if(originalSpace.isOnBoard())
				{
					return piece.getColor() == newSpace.getBucketColor();
				}
				else
				{
					//We should be returning the piece to the original bucket.
					return originalSpace.equals(newSpace);
				}
			}
			
			//Return true if this is the same space we are starting from.
			if(originalSpace.equals(newSpace))
			{
				return true;
			}
			
			//If the space is occupied, a move is invalid. (swaps may be okay though.)
			if(board.isOccupied(newSpace))
			{
				return false;
			}
			
			//Otherwise, verify that the pair of rows is okay.
			if(piece.getColor() == GameConstants.GOLD)
			{
				return newSpace.getRow() >= 6;
			}
			else
			{
				return newSpace.getRow() <= 1;
			}
		}
		else if(phase == GAME_ON)
		{
			//You can't move things to buckets here.
			if(newSpace.isABucket()) return false;
			
			//Return true if this is the same space we are starting from.
			if(originalSpace.equals(newSpace)) return true;
			
			//Otherwise, if the space is occupied, return false.
			/*
			 * Note that this is temporary.  We have to fix this when we can push/pull pieces.
			 */
			if(board.isOccupied(newSpace)) return false;
			
			//Verify that we have moves remaining
			if(numMoves >= 4) return false;
			
			//Now check what color we're working with.
			if(piece.getColor() == playerTurn)
			{
				//If we have to move into the pushPosition, we require the space to match
				if(pushPosition != null)
				{
					return newSpace.equals(pushPosition);
				}
				
				//This is a piece of the current player's color
				if(piece.getValue() == PieceData.RABBIT)
				{
					//Movement is only okay forward, left, and right.
					if(newSpace.equals(originalSpace.moveLeft())) return true;
					if(newSpace.equals(originalSpace.moveRight())) return true;
					if(piece.getColor() == GameConstants.GOLD)
					{
						if(newSpace.equals(originalSpace.moveUp())) return true;
					}
					else
					{
						if(newSpace.equals(originalSpace.moveDown())) return true;
					}
					return false;
				}
				else
				{
					//Okay!  Since we are good with that, we can move one space.
					return originalSpace.distanceFrom(newSpace) == 1;
				}
			}
			else
			{
				//We're dealing with an opponent's piece.  That means we're pushing or pulling.

				//Since we know the piece movement is valid, we just need to check how many spaces
				return originalSpace.distanceFrom(newSpace) == 1;
			}
		}
		
		//We have no moves to do if we're not playing the game or setting things up.
		return false;
	}
	
	/**
	 * Checks to see if the pieces in the original and target squares can be swapped.
	 * Swaps are only legal during the Setup phase.
	 *
	 * @param piece			PieceData of the piece in the original space.
	 * @param originalSpace	PiecePosition where the selected piece started from.
	 * @param newSpace		target PiecePosition we want to swap with.
	 * @return	true if a valid swap can be made, otherwise false.
	 */
	public boolean isValidSwap(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
		if(newSpace == null) return false;

		//We can only swap during the Setup phase, with pieces of the same color.
		if(phase == SETUP_PHASE)
		{
			//Return false if trying to put the piece back in the bucket.
			if(newSpace.isABucket())
			{
				return false;
			}

			//Return false if this is the same space we are starting from.
			if(originalSpace.equals(newSpace))
			{
				return false;
			}

			//Check if the target piece is the same color.
			if(board.isOccupied(newSpace))
			{
				PieceData targetPiece = board.getPieceAt(newSpace);
				return piece.getColor() == targetPiece.getColor();
			}
		}
		
		//Return false under all other circumstances.
		return false;
	}
	
	/**
	 * TODO: Describe method
	 *
	 */
	public void endTurn()
	{
		//Switch the turn.
		playerTurn = (playerTurn + 1) % 2;
		gui.setTurnState(playerTurn);
		
		//If we're back to gold, the game phase has changed.
		if(playerTurn == GameConstants.GOLD)
		{
			phase = GAME_ON;
		}
		
		numMoves = 0;
		
		gui.setEndofTurn(false);
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 * @param space2 
	 */
	public void movePiece(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
		//Validate the move and don't move onto the same space.
		if(isValidMove(piece, originalSpace, newSpace) && !originalSpace.equals(newSpace))
		{
			if(originalSpace.isABucket() && newSpace.isOnBoard())
			{
				//Moving from bucket to board.				
				//First remove the piece from the bucket.
				List<PieceData> bucket;
				if(piece.getColor() == GameConstants.GOLD)
				{
					bucket = goldBucket;
				}
				else
				{
					bucket = silverBucket;
				}
				
				bucket.remove(piece);
				
				//Now place the piece on the board in the correct space.
				board.placePiece(piece, newSpace);
			}
			else if(newSpace.isOnBoard())
			{
				//Moving from board to board.
				board.removePiece(originalSpace);
				
				board.placePiece(piece, newSpace);
				
				if(phase == GAME_ON) 
				{
					if(piece.getColor() != playerTurn)
					{
						pushPosition = originalSpace;
					}
					else if(pushPosition != null)
					{
						pushPosition = null;
					}
					
					//Increment the number of moves
					numMoves++;
				}
			}
			else
			{
				//Moving from board to bucket.

				//Remove the piece from the board.
				board.removePiece(originalSpace);
				
				//Add the piece to the bucket.
				List<PieceData> bucket;
				if(newSpace.getBucketColor() == GameConstants.GOLD)
				{
					bucket = goldBucket;
				}
				else
				{
					bucket = silverBucket;
				}
				
				bucket.add(piece);
			}
			
			//And the end of the move, see if the user can end their turn.
			checkForEndOfTurn();

			if (phase == GAME_ON)
			{
				//Check the traps to see if there are pieces in them and if they are dead.
				checkTheTraps();
				
				//Check to see if anyone has won the game.
				checkForWinner();
			}
		}
	}
	
	/**
	 * Swaps the location of two pieces.  This is used only during the Setup phase of the game.
	 *
	 * @param piece1	PieceData of the first piece.
	 * @param piece2	PieceData of the second piece.
	 * @param space1	PiecePosition of the first piece (either a bucket or board location)
	 * @param space2 	PiecePosition of the second piece (board location only)
	 */
	public void swapPieces (PieceData piece1, PieceData piece2, PiecePosition space1, PiecePosition space2)
	{
		//Remove the second piece from the board.
		board.removePiece(space2);
		
		//Check if the first piece came from a bucket.
		if (space1.isABucket())
		{		
			//Grab the correct bucket first.
			List<PieceData> bucket;
			if(piece1.getColor() == GameConstants.GOLD)
			{
				bucket = goldBucket;
			}
			else
			{
				bucket = silverBucket;
			}
			
			//remove the first piece and add the second piece to the bucket.
			bucket.remove(piece1);
			bucket.add(piece2);
		}
		//Otherwise both pieces came from the board.
		else
		{
			//remove the first piece and put the second piece in its place.
			board.removePiece(space1);
			board.placePiece(piece2, space1);
		}
		//finally, put the first piece where the second one was.
		board.placePiece(piece1, space2);
	}
	
	/**
	 * TODO: Describe method
	 *
	 */
	private void checkForWinner()
	{
		Iterator<PieceData> iterator = pieces.iterator();
		while(iterator.hasNext())
		{
			PieceData piece = iterator.next();
			if(piece.getValue() == PieceData.RABBIT)
			{
				if(piece.getPosition() != null)
				{
					if(piece.getPosition().isOnBoard())
					{
						if(piece.getColor() == GameConstants.GOLD)
						{
							if(piece.getPosition().getRow() == 0)
							{
								//Gold won!
								gui.setGameWinner(GameConstants.GOLD);
							}
						}
						else if(piece.getPosition().getRow() == 7)
						{
							//Silver won!
							gui.setGameWinner(GameConstants.SILVER);
						}
					}
				}
			}
		}
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void checkTheTraps()
	{
		Iterator<PiecePosition> iterator = BoardData.getTrapPosition().iterator();
		while(iterator.hasNext())
		{
			PiecePosition trapPosition = iterator.next();
			if(board.isOccupied(trapPosition))
			{
				PieceData trappedPiece = board.getPieceAt(trapPosition);
				boolean pieceIsDead = true;
				
				//Check for allies in each direction:
				List<PiecePosition> adjacentSpaces = trapPosition.getAdjacentSpaces();
				Iterator<PiecePosition> adjacentIterator = adjacentSpaces.iterator();
				while(adjacentIterator.hasNext())
				{
					PiecePosition space = adjacentIterator.next();
					if(board.isOccupied(space))
					{
						PieceData adjacentPiece = board.getPieceAt(space);
						if(adjacentPiece.getColor() == trappedPiece.getColor())
						{
							//We found an ally!  We're safe!
							pieceIsDead = false;
						}
					}
				}
				
				//Now kill the piece.
				if(pieceIsDead)
				{
					//Remove the piece from the board.
					board.removePiece(trapPosition);
					
					//Move the piece to the appropriate bucket.
					if(trappedPiece.getColor() == GameConstants.GOLD)
					{
						silverBucket.add(trappedPiece);
					}
					else
					{
						goldBucket.add(trappedPiece);
					}
					
					//And now tell the GUI to do the same.
					gui.movePieceToBucket(trapPosition);
				}
			}
		}
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void checkForEndOfTurn()
	{
		if(phase == SETUP_PHASE)
		{
			//Check to see if the current player's pieces are all out of the bucket.
			List<PieceData> bucket;
			if(playerTurn == GameConstants.GOLD)
			{
				bucket = goldBucket;
			}
			else
			{
				bucket = silverBucket;
			}
			
			gui.setEndofTurn(bucket.size() == 0);
		}
		else if(phase == GAME_ON)
		{
			//We can set the end of turn if the number of moves is greater than or equal to 1.
			gui.setEndofTurn(numMoves >= 1);
		}
	}

}

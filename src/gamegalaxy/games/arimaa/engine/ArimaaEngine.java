package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.PiecePosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.PieceData;
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

	public ArimaaEngine()
	{
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
	public boolean isPieceSelectable(PieceData data)
	{
		return data.getColor() == playerTurn && numMoves < 4;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 * @return
	 */
	public boolean isValidPiecePlacement(PieceData data, PiecePosition space)
	{
		if(board.isOccupied(space))
		{
			return false;
		}
		
		if(data.getColor() == GameConstants.GOLD)
		{
			return space.getRow() >= 6;
		}
		else
		{
			return space.getRow() <= 1;
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param originalSpace
	 * @param space
	 * @return
	 */
	public boolean isValidMove(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
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
			
			//Otherwise, if the space is occupied, return false.
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
			
			//Okay!  Since we are good with that, we can move one space.
			return originalSpace.distanceFrom(newSpace) == 1;
		}
		
		//We have no moves to do if we're not playing the game or setting things up.
		return false;
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void checkforEndOfTurn()
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
		
		checkforEndOfTurn();
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
				
				//Get the piece on the original space
				
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
				
				if(phase == GAME_ON) numMoves++;
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
			
			//And the end of the move, see if its the end of the turn.
			checkforEndOfTurn();
		}
	}

}

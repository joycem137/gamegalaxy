package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.BoardPosition;
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
	
	//Store our data.
	private List<PieceData>	pieces;
	private int	playerTurn;
	private BoardData	board;
	private int	phase;

	private List<PieceData>	goldBucket;
	private List<PieceData> silverBucket;

	//Store a link to the UI
	private ArimaaUI	gui;

	public ArimaaEngine()
	{
		
		phase = SETUP_PHASE;
		
		playerTurn = GameConstants.GOLD;
	
		board = new BoardData();
		
		createPieces();
		
		//Initialize our buckets.
		createBuckets();
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
	public boolean isPiecePlaceable(PieceData data)
	{
		return data.getColor() == playerTurn;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 * @return
	 */
	public boolean isValidPiecePlacement(PieceData data, BoardPosition space)
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
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piecePanel
	 * @param bucketColor
	 * @return
	 */
	public boolean isValidToDropInBucket(PieceData pieceData, int bucketColor)
	{
		return pieceData.getColor() == bucketColor && phase == SETUP_PHASE;
	}

	/**
	 * TODO: Describe method
	 *
	 */
	public void endTurn()
	{
		playerTurn = (playerTurn + 1) % 2;
		gui.setTurnState(playerTurn);
		checkforEndOfTurn();
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 * @param space2 
	 */
	public void movePiece(BoardPosition originalSpace, BoardPosition newSpace)
	{
		PieceData piece = board.getPieceAt(originalSpace);
		
		board.removePiece(originalSpace);
		
		board.placePiece(piece, newSpace);
		
		checkforEndOfTurn();
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 */
	public void movePieceFromBucketToBoard(PieceData data, BoardPosition space)
	{
		//First remove the piece from the bucket.
		List<PieceData> bucket;
		if(data.getColor() == GameConstants.GOLD)
		{
			bucket = goldBucket;
		}
		else
		{
			bucket = silverBucket;
		}
		
		bucket.remove(data);
		
		//Now place the piece on the board in the correct space.
		board.placePiece(data, space);
		
		checkforEndOfTurn();
	}

	/**
	 * TODO: Describe method
	 *
	 * @param originalSpace
	 */
	public void movePieceFromBoardToBucket(BoardPosition space)
	{
		
		//Get the piece from the board.
		PieceData piece = board.getPieceAt(space);
		
		//Remove the piece from the board.
		board.removePiece(space);
		
		//Add the piece to the bucket.
		List<PieceData> bucket;
		if(piece.getColor() == GameConstants.GOLD)
		{
			bucket = goldBucket;
		}
		else
		{
			bucket = silverBucket;
		}
		
		bucket.add(piece);
		
		checkforEndOfTurn();
	}

}

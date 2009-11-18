package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.PieceData;

import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class ArimaaEngine
{
	private List<PieceData>	pieces;
	private int	playerTurn;
	private BoardData	board;

	public ArimaaEngine()
	{
		playerTurn = GameConstants.GOLD;
	
		board = new BoardData();
		
		createPieces();
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
			return space.getCol() <= 1;
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 */
	public void placePiece(PieceData data, BoardPosition space)
	{
		board.placePiece(data, space);
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param originalSpace
	 */
	public void removePiece(BoardPosition space)
	{
		board.removePiece(space);
	}

}

package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.PieceData;

import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class ArimaaEngine
{
	private List<PieceData>	pieces;

	public ArimaaEngine()
	{
		pieces = new Vector<PieceData>(32);
		
		createPieces();
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void createPieces()
	{
		//Create 8 rabbits of each color
		for(int i = 0; i < 8; i++)
		{
			pieces.add(new PieceData(PieceData.GOLD, PieceData.RABBIT));
			pieces.add(new PieceData(PieceData.SILVER, PieceData.RABBIT));
		}
		
		//Create 2 each of horses, cats, and dogs.
		for(int i = 0; i < 2; i++)
		{
			pieces.add(new PieceData(PieceData.GOLD, PieceData.CAT));
			pieces.add(new PieceData(PieceData.SILVER, PieceData.CAT));
			pieces.add(new PieceData(PieceData.GOLD, PieceData.DOG));
			pieces.add(new PieceData(PieceData.SILVER, PieceData.DOG));
			pieces.add(new PieceData(PieceData.GOLD, PieceData.HORSE));
			pieces.add(new PieceData(PieceData.SILVER, PieceData.HORSE));
		}
		
		//Create 1 camel and elephant of each color
		pieces.add(new PieceData(PieceData.GOLD, PieceData.CAMEL));
		pieces.add(new PieceData(PieceData.SILVER, PieceData.CAMEL));
		pieces.add(new PieceData(PieceData.GOLD, PieceData.ELEPHANT));
		pieces.add(new PieceData(PieceData.SILVER, PieceData.ELEPHANT));
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

}

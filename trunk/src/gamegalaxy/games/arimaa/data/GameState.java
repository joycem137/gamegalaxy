package gamegalaxy.games.arimaa.data;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 */
public final class GameState
{

	private BoardData	board;
	private List<PieceData>	goldBucket;
	private List<PieceData>	silverBucket;
	
	private int	playerTurn;
	private int	numMoves;
	private int	phase;
	
	private PiecePosition	pushPosition;
	private PieceData	pullPiece;
	private PiecePosition	pullPosition;
	private int	winner;

	/**
	 * TODO: Describe constructor
	 *
	 * @param goldBucket
	 * @param silverBucket
	 * @param board
	 * @param playerTurn
	 * @param numMoves
	 * @param phase
	 * @param pushPosition
	 * @param pullPosition
	 * @param pullPiece
	 */
	public GameState(List<PieceData> goldBucket, List<PieceData> silverBucket,
			BoardData board, int playerTurn, int numMoves, int phase, int winner,
			PiecePosition pushPosition, PiecePosition pullPosition,
			PieceData pullPiece)
	{
		this.goldBucket = copyList(goldBucket);
		this.silverBucket = copyList(silverBucket);
		this.board = board.copy();
		
		//Store the easy data.
		this.playerTurn = playerTurn;
		this.numMoves = numMoves;
		this.phase = phase;
		this.winner = winner;
		
		//And now the rest of the hard stuff.
		this.pushPosition = pushPosition;
		this.pullPosition = pullPosition;
		if(pullPiece != null)
			this.pullPiece = pullPiece.copy();
	}

	/**
	 * TODO: Describe method
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
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getCurrentPlayer()
	{
		return playerTurn;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public boolean isGameOver()
	{
		return phase == GameConstants.GAME_WON;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getGameWinner()
	{
		return winner;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public List<PieceData> getGoldBucket()
	{
		return goldBucket;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public boolean isSetupPhase()
	{
		return phase == GameConstants.SETUP_PHASE;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public List<PieceData> getSilverBucket()
	{
		return silverBucket;
	}

	/**
	 * TODO: Describe method
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
	 * TODO: Describe method
	 *
	 * @return
	 */
	public BoardData getBoardData()
	{
		return board;
	}

}

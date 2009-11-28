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
	
	private PiecePosition	pushPosition;
	private PieceData	pullPiece;
	private PiecePosition	pullPosition;
	private int	winner;

	/**
	 * Constructs the game state from the provided information
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
	 * Returns the representation of the board in the current state.
	 *
	 * @return
	 */
	public BoardData getBoardData()
	{
		return board;
	}

}

package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.GameBoardData;

/**
 * 
 */
public class ArimaaEngine
{

	private GameBoardData	gameBoard;

	public ArimaaEngine()
	{
		gameBoard = new GameBoardData();
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public GameBoardData getBoard()
	{
		return gameBoard;
	}

}

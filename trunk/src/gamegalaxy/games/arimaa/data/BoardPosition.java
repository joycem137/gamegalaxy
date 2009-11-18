package gamegalaxy.games.arimaa.data;

/**
 * 
 */
public class BoardPosition
{

	private int	row;
	private int	col;

	/**
	 * TODO: Describe constructor
	 *
	 * @param row
	 * @param col
	 */
	public BoardPosition(int row, int col)
	{
		this.row = row;
		this.col = col;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getCol()
	{
		return col;
	}
	
	public int getRow()
	{
		return row;
	}

}

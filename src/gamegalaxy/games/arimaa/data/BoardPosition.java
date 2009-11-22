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
	public BoardPosition(int col, int row)
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
	
	public boolean equals(Object o)
	{
		if(o instanceof BoardPosition)
		{
			BoardPosition other = (BoardPosition)o;
			return other.row == row && other.col == col;
		}
		else
		{
			return false;
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param newSpace
	 * @return
	 */
	public int distanceFrom(BoardPosition otherSpace)
	{
		return Math.abs(row - otherSpace.row + col - otherSpace.col);
	}

}

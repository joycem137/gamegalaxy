package gamegalaxy.games.arimaa.data;

import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class PiecePosition
{
	public static final PiecePosition	GOLD_BUCKET	= new PiecePosition(-1, GameConstants.GOLD);
	public static final PiecePosition 	SILVER_BUCKET = new PiecePosition(-1, GameConstants.SILVER);
	
	private int	row;
	private int	col;

	/**
	 * TODO: Describe constructor
	 *
	 * @param row
	 * @param col
	 */
	public PiecePosition(int col, int row)
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
		if(o instanceof PiecePosition)
		{
			PiecePosition other = (PiecePosition)o;
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
	public int distanceFrom(PiecePosition otherSpace)
	{
		return Math.abs(row - otherSpace.row + col - otherSpace.col);
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public boolean isOnBoard()
	{
		return col >= 0;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getBucketColor()
	{
		return row;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public boolean isABucket()
	{
		return col == -1;
	}
	
	public String toString()
	{
		return "" + row + ", " + col;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public List<PiecePosition> getAdjacentSpaces()
	{
		List<PiecePosition> spaces = new Vector<PiecePosition>(4);
		spaces.add(new PiecePosition(col - 1, row));
		spaces.add(new PiecePosition(col + 1, row));
		spaces.add(new PiecePosition(col, row - 1));
		spaces.add(new PiecePosition(col, row + 1));
		return spaces;
	}

}

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gamegalaxy.
 *  
 *  gamegalaxy is Copyright 2009 Joyce Murton
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
		return Math.abs(row - otherSpace.row) + Math.abs(col - otherSpace.col);
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
		return "" + col + ", " + row;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public List<PiecePosition> getAdjacentSpaces()
	{
		List<PiecePosition> spaces = new Vector<PiecePosition>(4);
		PiecePosition temp = moveUp();
		if(temp != null) spaces.add(temp);
		
		temp = moveDown();
		if(temp != null) spaces.add(temp);
		
		temp = moveLeft();
		if(temp != null) spaces.add(temp);
		
		temp = moveRight();
		if(temp != null) spaces.add(temp);
		
		return spaces;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public PiecePosition moveLeft()
	{
		if(col - 1 >= 0)
		{
			return new PiecePosition(col - 1, row);
		}
		else
		{
			return null;
		}
	}
	
	public PiecePosition moveUp()
	{
		if(row - 1 >= 0)
		{
			return new PiecePosition(col, row - 1);
		}
		else
		{
			return null;
		}
	}

	public PiecePosition moveRight()
	{
		if(col + 1 <= 7)
		{
			return new PiecePosition(col + 1, row);
		}
		else
		{
			return null;
		}
	}
	
	public PiecePosition moveDown()
	{
		if(row + 1 <= 7)
		{
			return new PiecePosition(col, row + 1);
		}
		else
		{
			return null;
		}
	}
}

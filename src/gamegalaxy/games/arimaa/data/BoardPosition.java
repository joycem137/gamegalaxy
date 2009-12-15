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
 */
package gamegalaxy.games.arimaa.data;

import java.util.List;
import java.util.Vector;

/**
 * 
 */
public final class BoardPosition implements PiecePosition
{	
	private final int	row;
	private final int	col;

	/**
	 * Creates a BoardPosition with the specified row and column.  Using "col, row" notation
	 * as an X, Y representation.
	 *
	 * @param col An integer representing the column for this piece position
	 * @param row An integer representing the row for this piece position.
	 */
	public BoardPosition(int col, int row)
	{
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Returns the column associated with this piece position.
	 *
	 * @return An integer representing the column of this piece position
	 */
	public int getCol()
	{
		return col;
	}
	
	/**
	 * 
	 * Returns the row associate with this piece position.
	 *
	 * @return An integer representing the row of this piece position.
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * Return the Euclidean distance to another BoardPosition
	 *
	 * @param newSpace The position to calculate distance from
	 * @return An integer indicating the number of spaces required to move to the indicated location
	 */
	public int distanceFrom(BoardPosition otherSpace)
	{
		//Abort if there's a bucket involved
		
		return Math.abs(row - otherSpace.row) + Math.abs(col - otherSpace.col);
	}
	
	public String toString()
	{
		return "" + col + ", " + row;
	}

	/**
	 * Return a list of all adjacent spaces to this position.
	 *
	 * @return
	 */
	public List<BoardPosition> getAdjacentSpaces()
	{
		List<BoardPosition> spaces = new Vector<BoardPosition>(4);
		BoardPosition temp = moveUp();
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
	 * Return the board position immediately to the left of this one.
	 * If there is no space to the left of this one, return null.
	 *
	 * @return
	 */
	public BoardPosition moveLeft()
	{
		if(col - 1 >= 0)
		{
			return new BoardPosition(col - 1, row);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 
	 * Return the board position immediately above this one.  
	 * If there is no space above this one, return null.
	 *
	 * @return
	 */
	public BoardPosition moveUp()
	{
		if(row - 1 >= 0)
		{
			return new BoardPosition(col, row - 1);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Return the board position immediately to the right of this one.
	 * If there is no space to the right of this one, return null.
	 *
	 * @return
	 */
	public BoardPosition moveRight()
	{
		if(col + 1 <= 7)
		{
			return new BoardPosition(col + 1, row);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 
	 * Return the board position immediately below this one.
	 * If there is no space below this one, return null.
	 *
	 * @return
	 */
	public BoardPosition moveDown()
	{
		if(row + 1 <= 7)
		{
			return new BoardPosition(col, row + 1);
		}
		else
		{
			return null;
		}
	}

	/**
	 * TODO: Describe overridden method
	 *
	 * @param otherPosition
	 * @return
	 * @see gamegalaxy.games.arimaa.data.PiecePosition#equals(gamegalaxy.games.arimaa.data.PiecePosition)
	 */
	public boolean equals(PiecePosition otherPosition)
	{
		//Verify that this is actually a PiecePosition object.
		if(otherPosition instanceof BoardPosition)
		{
			return ((BoardPosition)otherPosition).row == row && ((BoardPosition)otherPosition).col == col;
		}
		else
		{
			//This isn't a BoardPosition object.  The two objects cannot be the same.
			return false;
		}
	}
	
	/**
	 * 
	 * Compares this BoardPosition object with another object.  Returns true if the two
	 * objects are both PiecePosition objects and both have the same row and column.
	 *
	 * @param o The object to compare this object to.
	 * @return <code>true</code> if the two objects are equivalent
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		//Verify that this is actually a PiecePosition object.
		if(o instanceof BoardPosition)
		{
			return ((BoardPosition)o).row == row && ((BoardPosition)o).col == col;
		}
		else
		{
			//This isn't a BoardPosition object.  The two objects cannot be the same.
			return false;
		}
	}
}

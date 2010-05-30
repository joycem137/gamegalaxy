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

/**
 * Stores a representation of a bucket position.
 */
public final class BucketPosition implements PiecePosition
{
	private int	color;
	private final int	col;
	private final int	row;

	/**
	 * 
	 * Creates a bucket position of the appropriate color.
	 *
	 * @param color
	 */
	public BucketPosition(int color, int col, int row)
	{
		this.color = color;
		this.col = col;
		this.row = row;
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
	 * Returns the color of this bucket position.
	 *
	 * @return
	 */
	public int getColor()
	{
		return color;
	}
	
	/**
	 * returns a string representation of this bucket
	 *
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Bucket " + color;
	}
	
	/**
	 * Compares this BucketPosition object with another object.  Returns true if the two
	 * objects are both BucketPosition objects contained in the same bucket.
	 *
	 * @param o The object to compare this object to.
	 * @return <code>true</code> if the two objects are equal
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		//Verify that this is actually a PiecePosition object.
		if(o instanceof BucketPosition)
		{
			BucketPosition other = (BucketPosition)o;
			return other.color == color;
		}
		else
		{
			//This isn't a BucketPosition object.  The two objects cannot be the same.
			return false;
		}
	}
	
	/** 
	 * Hashes the BucketPosition.  For any two BucketPosition objects a and b,
	 * if a.equals(b), then a.hashCode() = b.hashCode() as well.
	 * 
	 * @return  <code>int</code> value representing the object's hashCode.
	 * 
	 * @see java.lang.Object#hashCode(java.lang.Object)
	 */
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + color;
		hash = hash * 31 + col;
		hash = hash * 31 + row;
		return hash;
	}

}

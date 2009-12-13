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

	/**
	 * 
	 * Creates a bucket position of the appropriate color.
	 *
	 * @param color
	 */
	public BucketPosition(int color)
	{
		this.color = color;
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
	 * returns a string representation of this bucket
	 *
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Bucket " + color;
	}

}

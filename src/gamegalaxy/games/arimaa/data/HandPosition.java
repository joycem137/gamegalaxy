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
 */
package gamegalaxy.games.arimaa.data;

/**
 * This class represents the position of a piece being in the player's "hand"
 * which means that it has a forced move to make.
 */
public final class HandPosition implements PiecePosition
{
	private BoardPosition	oldPosition;

	public HandPosition(BoardPosition oldPosition)
	{
		this.oldPosition = oldPosition;
	}

	/**
	 * Compares this HandPosition object with another object.  Returns true if the two
	 * objects are both HandPosition objects with the same oldPosition.
	 *
	 * @param o The object to compare this object to.
	 * @return <code>true</code> if the two objects are equal
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if(o instanceof HandPosition)
		{
			return oldPosition.equals(((HandPosition)o).oldPosition);
		}
		else
		{
			return false;
		}
	}
	
	/** 
	 * Hashes the HandPosition.  For any two HandPosition objects a and b,
	 * if a.equals(b), then a.hashCode() = b.hashCode() as well.
	 * 
	 * @return  <code>int</code> value representing the object's hashCode.
	 * 
	 * @see java.lang.Object#hashCode(java.lang.Object)
	 */
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + oldPosition.hashCode();
		return hash;
	}

	/**
	 * Returns the position on the board from which this piece
	 * came.
	 *
	 * @return
	 */
	public BoardPosition getOldPosition()
	{
		return oldPosition;
	}

}

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
	 * TODO: Describe overridden method
	 *
	 * @param otherPosition
	 * @return
	 * @see gamegalaxy.games.arimaa.data.PiecePosition#equals(gamegalaxy.games.arimaa.data.PiecePosition)
	 */
	public boolean equals(PiecePosition otherPosition)
	{
		if(otherPosition instanceof HandPosition)
		{
			return oldPosition.equals(((HandPosition)otherPosition).oldPosition);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 
	 * Returns equals if this is the parameter object is a HandPosition that
	 * has the same values as this one.
	 *
	 * @param o
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if(o instanceof PiecePosition)
		{
			return equals((PiecePosition)o);
		}
		else
		{
			return false;
		}
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

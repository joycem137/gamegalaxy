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
 * This class stores a representation of a move.
 */
public final class StepData
{

	private PieceData	piece;
	private PiecePosition	destination;
	private PiecePosition	sourcePosition;

	/**
	 * TODO: Describe constructor
	 *
	 * @param piece
	 * @param bucketPosition
	 */
	public StepData(PieceData piece, PiecePosition destination)
	{
		this.piece = piece;
		this.destination = destination;
		sourcePosition = piece.getPosition();
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public PieceData getPiece()
	{
		return piece;
	}
	
	/**
	 * Compares this StepData object with another object.  Returns true only if both
	 * objects are StepData objects with the same piece, sourcePosition, and destination.
	 * 
	 * @param o The object to compare this object to.
	 * @return <code>true</code> if the two objects are equal
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if(o instanceof StepData)
		{
			StepData otherMove = (StepData)o;
			if (piece == null || otherMove.piece == null) return false;
			if (sourcePosition == null || otherMove.sourcePosition == null) return false;
			if (destination == null || otherMove.destination == null) return false;
			return piece.equals(otherMove.piece) && sourcePosition.equals(otherMove.sourcePosition) && destination.equals(otherMove.destination);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Hashes the StepData.  For any two StepData objects a and b,
	 * if a.equals(b), then a.hashCode() = b.hashCode() as well.
	 * 
	 * @return  <code>int</code> value representing the object's hashCode.
	 * 
	 * @see java.lang.Object#hashCode(java.lang.Object)
	 */
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + sourcePosition.hashCode();
		hash = hash * 31 + destination.hashCode();
		hash = hash * 31 + piece.hashCode();
		return hash;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public PiecePosition getDestination()
	{
		return destination;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public PiecePosition getSource()
	{
		return sourcePosition;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public StepData copy()
	{
		return new StepData(piece.copy(), destination);
	}

}

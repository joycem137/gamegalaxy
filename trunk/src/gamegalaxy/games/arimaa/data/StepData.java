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
	 * 
	 * TODO: Describe overridden method
	 *
	 * @param o
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if(o instanceof StepData)
		{
			StepData otherMove = (StepData)o;
			return piece.equals(otherMove.piece) && destination.equals(otherMove.destination);
		}
		else
		{
			return false;
		}
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

}

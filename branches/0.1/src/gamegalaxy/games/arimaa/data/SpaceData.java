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
 * This class is used to store all the information about a particular space.
 * It currently stores whether a particular space is normal or a trap, and
 * what piece is at this location, if any.
 */
public class SpaceData
{
	public static final int NORMAL = 0;
	public static final int TRAP = 1;
	
	private int	spaceType;
	private PieceData piece;
	
	/**
	 * Creates a new space of the indicated type.
	 *
	 * @param spaceType
	 */
	public SpaceData(int spaceType)
	{
		this.spaceType = spaceType;
	}

	/**
	 * Return true if the space is currently occupied.
	 *
	 * @return
	 */
	public boolean isOccupied()
	{
		return piece != null;
	}

	/**
	 * Assign the piece to this space.
	 *
	 * @param data
	 */
	public void placePiece(PieceData piece)
	{
		this.piece = piece;
	}

	/**
	 * Remove the current piece from the board.
	 *
	 */
	public void removePiece()
	{
		piece = null;
	}

	/**
	 * Returns the PieceData object representing the piece at this location.
	 *
	 * @return
	 */
	public PieceData getPiece()
	{
		return piece;
	}

}

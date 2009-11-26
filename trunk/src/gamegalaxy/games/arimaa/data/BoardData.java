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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class BoardData
{
	private SpaceData[][] spaces;
	
	public BoardData()
	{	
		//Create our actual board.
		spaces = new SpaceData[8][8];
		
		//Create our trap spaces
		spaces[2][5] = new SpaceData(SpaceData.TRAP);
		spaces[5][2] = new SpaceData(SpaceData.TRAP);
		spaces[2][2] = new SpaceData(SpaceData.TRAP);
		spaces[5][5] = new SpaceData(SpaceData.TRAP);
		
		//Create the rest of the spaces.
		for(int r = 0; r < 8; r++)
		{
			for(int c = 0; c < 8; c++)
			{
				//Create the space
				if(spaces[c][r] == null)
				{
					spaces[c][r] = new SpaceData(SpaceData.NORMAL);
				}
			}
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param space
	 * @return
	 */
	public boolean isOccupied(PiecePosition space)
	{
		if(space.isOnBoard()) 
		{
			return spaces[space.getCol()][space.getRow()].isOccupied();
		}
		else 
		{
			return false;
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @param space
	 */
	public void placePiece(PieceData piece, PiecePosition space)
	{
		if(space.isOnBoard())
		{
			piece.setPosition(space);
			spaces[space.getCol()][space.getRow()].placePiece(piece);
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param data
	 * @param space
	 */
	public void removePiece(PiecePosition space)
	{
		if(space.isOnBoard())
		{
			//Get the piece data.
			SpaceData spaceData = spaces[space.getCol()][space.getRow()];
			spaceData.getPiece().setPosition(null);
			spaceData.removePiece();
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param space
	 * @return
	 */
	public PieceData getPieceAt(PiecePosition space)
	{
		if(space.isOnBoard())
		{
			return spaces[space.getCol()][space.getRow()].getPiece();
		}
		else
		{
			return null;
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public static List<PiecePosition> getTrapPosition()
	{
		List<PiecePosition> traps = new Vector<PiecePosition>(4);
		traps.add(new PiecePosition(2, 5));
		traps.add(new PiecePosition(2, 2));
		traps.add(new PiecePosition(5, 2));
		traps.add(new PiecePosition(5, 5));
		return traps;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @return
	 */
	public List<PieceData> getAdjacentPieces(PieceData piece)
	{
		List<PieceData> adjacentPieces = new Vector<PieceData>();
		
		List<PiecePosition> adjacentPositions = piece.getPosition().getAdjacentSpaces();
		Iterator<PiecePosition> iterator = adjacentPositions.iterator();
		while(iterator.hasNext())
		{
			PiecePosition positionToTest = iterator.next();
			if(isOccupied(positionToTest))
			{
				adjacentPieces.add(getPieceAt(positionToTest));
			}
		}
		return adjacentPieces;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @return
	 */
	public boolean isPieceFrozen(PieceData piece)
	{
		//Get all of the adjacent pieces
		List<PieceData> adjacentPieces = getAdjacentPieces(piece);
		
		//If there are no pieces next to us, we're not frozen.
		if(adjacentPieces.size() == 0) return false;
		
		//Check to see what sort of pieces we're next to
		boolean pieceIsFrozen = false;
		Iterator<PieceData> iterator = adjacentPieces.iterator();
		while(iterator.hasNext())
		{
			PieceData adjacentPiece = iterator.next();
			
			//If we're next to any pieces of our color, we are not frozen
			if(adjacentPiece.getColor() == piece.getColor()) 
			{
				return false;
			}
			else
			{
				//Check to see if we need to raise the frozen piece flag.
				if(adjacentPiece.getValue() > piece.getValue())
				{
					pieceIsFrozen = true;
				}
			}
		}
		return pieceIsFrozen;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @return
	 */
	public boolean pieceHasSpaceToMoveInto(PieceData piece)
	{
		List<PiecePosition> adjacentPositions = piece.getPosition().getAdjacentSpaces();
		Iterator<PiecePosition> iterator = adjacentPositions.iterator();
		while(iterator.hasNext())
		{
			//If we find an unoccupied space, we're good!
			if(!isOccupied(iterator.next())) return true;
		}
		return false;
	}

}

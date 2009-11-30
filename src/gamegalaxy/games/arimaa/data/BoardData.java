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
 *  
 */
package gamegalaxy.games.arimaa.data;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This class stores all of the information about the board in a game of Arimaa.
 */
public class BoardData
{
	private SpaceData[][] spaces;
	
	/**
	 * 
	 * Create a blank board.
	 *
	 */
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
	 * Returns true if the given space is occupied by a piece.
	 *
	 * @param space
	 * @return
	 */
	public boolean isOccupied(BoardPosition space)
	{
		return spaces[space.getCol()][space.getRow()].isOccupied();
	}

	/**
	 * Places the indicated piece at the indicated position.
	 *
	 * @param piece
	 * @param space
	 */
	public void placePiece(PieceData piece, BoardPosition space)
	{
		piece.setPosition(space);
		spaces[space.getCol()][space.getRow()].placePiece(piece);
	}

	/**
	 * Removes the piece on the board that is on the indicated space.
	 *
	 * @param data
	 * @param space
	 */
	public void removePiece(BoardPosition space)
	{
		//Get the piece data.
		SpaceData spaceData = spaces[space.getCol()][space.getRow()];
		spaceData.getPiece().setPosition(null);
		spaceData.removePiece();
	}

	/**
	 * Returns the piece that is loaded at the given position  
	 * Returns null if the position is not on the board.
	 *
	 * @param space
	 * @return
	 */
	public PieceData getPieceAt(BoardPosition space)
	{
		return spaces[space.getCol()][space.getRow()].getPiece();
	}

	/**
	 * Returns a list of all of the trap positions
	 *
	 * @return
	 */
	public static List<BoardPosition> getTrapPosition()
	{
		List<BoardPosition> traps = new Vector<BoardPosition>(4);
		traps.add(new BoardPosition(2, 5));
		traps.add(new BoardPosition(2, 2));
		traps.add(new BoardPosition(5, 2));
		traps.add(new BoardPosition(5, 5));
		return traps;
	}

	/**
	 * Returns a list of all of the pieces that are adjacent to the indicated piece.
	 *
	 * @param piece
	 * @return
	 */
	public List<PieceData> getAdjacentPieces(PieceData piece)
	{
		List<PieceData> adjacentPieces = new Vector<PieceData>();
		
		//Verify that the piece is on the board.
		assert piece.getPosition() instanceof BoardPosition;
		
		//Recast to a board position
		BoardPosition position = (BoardPosition)piece.getPosition();
		
		//Look at all of the adjacent spaces.
		List<BoardPosition> adjacentPositions = position.getAdjacentSpaces();
		Iterator<BoardPosition> iterator = adjacentPositions.iterator();
		while(iterator.hasNext())
		{
			//Check to see if the space is occupied.
			BoardPosition positionToTest = iterator.next();
			if(isOccupied(positionToTest))
			{
				adjacentPieces.add(getPieceAt(positionToTest));
			}
		}
		return adjacentPieces;
	}

	/**
	 * Returns true if the piece is frozen by a larger piece of the opposite player.
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
	 * Returns true if the piece has an empty space that it can move into.
	 *
	 * @param piece
	 * @return
	 */
	public boolean pieceHasSpaceToMoveInto(PieceData piece)
	{
		//Assert that the piece is on the board.
		assert piece.getPosition() instanceof BoardPosition;
		
		//Convert the board position.
		BoardPosition position = (BoardPosition)piece.getPosition();
		
		List<BoardPosition> adjacentPositions = position.getAdjacentSpaces();
		Iterator<BoardPosition> iterator = adjacentPositions.iterator();
		while(iterator.hasNext())
		{
			//If we find an unoccupied space, we're good!
			if(!isOccupied(iterator.next())) return true;
		}
		return false;
	}

	/**
	 * Returns true if it is possible for the indicated piece to be pushed.
	 *
	 * @param piece
	 * @return
	 */
	public boolean pieceCanBePushed(PieceData piece)
	{
		//Get a list of all the pieces next to this one.
		List<PieceData> adjacentPieces = getAdjacentPieces(piece);
		
		//You can't select a piece of the opposite color if it has no adjacent pieces.
		if(adjacentPieces.size() == 0) return false;
		
		//This piece must be next to a piece of the opposite color and of a higher value
		Iterator<PieceData> iterator = adjacentPieces.iterator();
		while(iterator.hasNext())
		{
			PieceData adjacentPiece = iterator.next();
			
			//Check to see if we have a piece of the appropriate properties next to us.
			if(adjacentPiece.getColor() != piece.getColor() &&
				adjacentPiece.getValue() > piece.getValue())
			{
				//Make sure the adjacent piece is not frozen.
				if(!isPieceFrozen(adjacentPiece))
				{
					return true;
				}
			}	
		}
		
		//If we made it this far, that means that none of the adjacent pieces are correct.
		return false;
	}

	/**
	 * Returns a "deep copy" of this board, using an entirely new board and new PieceData objects
	 *
	 * @return
	 */
	public BoardData copy()
	{
		BoardData newBoard = new BoardData();
		for(int col = 0; col < 8; col++)
		{
			for(int row = 0; row < 8; row++)
			{
				//Construct the piece position
				BoardPosition position = new BoardPosition(col, row);
				
				if(isOccupied(position))
				{
					PieceData piece = getPieceAt(position).copy();
					newBoard.placePiece(piece, position);
				}
			}
		}
		return newBoard;
	}

}

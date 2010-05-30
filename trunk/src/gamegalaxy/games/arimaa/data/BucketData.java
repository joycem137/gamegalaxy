/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gamegalaxy.
 *  
 *  gamegalaxy is Copyright 2009-2010 Joyce Murton, Andrea Kilpatrick, and Melissa Bollen
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

public class BucketData {
	private SpaceData[][] spaces;
	
	static int numCols = 2;
	static int numRows = 8;
	
	int color;
	
	/**
	 * Create a new empty bucket
	 */
	public BucketData(int color)
	{
		this.color = color;
		
		spaces = new SpaceData[2][8];
		
		for(int x = 0; x < numCols ; x++)
		{
			for(int y = 0; y < numRows; y++)
			{
				spaces[x][y] = new SpaceData(SpaceData.NORMAL);
			}
		}
	}
	
	/**
	 * Returns true if the given space is occupied by a piece.
	 *
	 * @param space
	 * @return
	 */
	public boolean isOccupied(BucketPosition space)
	{
		return spaces[space.getCol()][space.getRow()].isOccupied();
	}
	
	/**
	 * Places the indicated piece at the indicated position.
	 *
	 * @param piece
	 * @param space
	 */
	public void addPiece(PieceData piece)
	{
		BucketPosition space = findEmptySpace();
		
		if (space != null){
			piece.setPosition(space);
			spaces[space.getCol()][space.getRow()].placePiece(piece);
		}else{
			System.err.println("ERROR: Bucket is already full!");
			System.exit(-1);
		}
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
	public PieceData getPieceAt(BucketPosition space)
	{
		return spaces[space.getCol()][space.getRow()].getPiece();
	}


	private BucketPosition findEmptySpace(){
		for(int x = 0; x < numCols ; x++)
		{
			for(int y = 0; y < numRows; y++)
			{
				BucketPosition position = new BucketPosition(color, x, y);
				
				if (!isOccupied(position)){
					return position;
				}
			}
		}
		
		return null;
	}


	/**
	 * Returns a "deep copy" of this board, using an entirely new board and new PieceData objects
	 *
	 * @return
	 */
	public BucketData copy()
	{
		BucketData newBucket = new BucketData(color);
		for(int x = 0; x < numCols ; x++)
		{
			for(int y = 0; y < numRows; y++)
			{
				BucketPosition position = new BucketPosition(color, x, y);
				
				if (isOccupied(position)){
					PieceData piece = getPieceAt(position).copy();
					newBucket.addPiece(piece);
				}
			}
		}
		
		return newBucket;
	}
}
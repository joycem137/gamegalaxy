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

import gamegalaxy.tools.SimpleObservable;

import java.util.Observer;

/**
 * 
 */
public class GameBoardData
{
	private SpaceData[][] spaces;
	private SimpleObservable observable;
	
	public GameBoardData()
	{
		//Create our observable
		observable = new SimpleObservable();
		
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
				if(spaces[r][c] == null)
				{
					spaces[r][c] = new SpaceData(SpaceData.NORMAL);
				}
			}
		}
	}
	
	/**
	 * Return the space at r,c
	 *
	 * @param r
	 * @param c
	 * @return
	 */
	public SpaceData getDataAt(int r, int c)
	{
		return spaces[r][c];
	}

	/**
	 * Add an observer to the data.
	 *
	 * @param boardDisplay
	 */
	public void addObserver(Observer observer)
	{
		observable.addObserver(observer);
	}

}

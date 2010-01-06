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

package gamegalaxy.games.arimaa.gui;


/**
 * A generic interface for any object that can hold a playing piece.
 */
public interface PieceHolder
{

	/**
	 * Called in order to drop the current playing piece onto the implementing class.
	 *
	 * @param piecePanel The playing piece being dropped.
	 */
	void dropPiece(PiecePanel piecePanel);

	/**
	 * Called to remove the playing piece from the implementing class.
	 *
	 * @param piecePanel The playing piece to be removed.
	 */
	void removePiece(PiecePanel piecePanel);

}

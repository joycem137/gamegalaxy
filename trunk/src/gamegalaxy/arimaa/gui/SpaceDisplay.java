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

package gamegalaxy.arimaa.gui;

import gamegalaxy.arimaa.data.SpaceData;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 
 */
public class SpaceDisplay extends JPanel
{
	
	public SpaceDisplay()
	{
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(Color.black, 3));
	}

	/**
	 * TODO: Describe method
	 *
	 * @param spaceData
	 */
	public void updateData(SpaceData spaceData)
	{
		
		//Draw the space
		int spaceType = spaceData.getSpaceType();
		if(spaceType == SpaceData.TRAP)
		{
			setBackground(Color.black);
		}
		else
		{
			setBackground(Color.white);
		}
	}

}

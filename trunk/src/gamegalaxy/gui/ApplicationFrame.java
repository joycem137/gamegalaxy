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

package gamegalaxy.gui;

import gamegalaxy.games.arimaa.gui.ArimaaUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * 
 */
public class ApplicationFrame extends JFrame
{
	
	public ApplicationFrame(ArimaaUI gui)
	{
		super("Arimaa");
	
		//Create the main window.
		Dimension preferredSize = gui.getPreferredSize();
		setSize(preferredSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		add(gui);
		
		setVisible(true);
	}
}

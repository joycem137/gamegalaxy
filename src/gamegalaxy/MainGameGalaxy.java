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

package gamegalaxy;

import gamegalaxy.games.arimaa.engine.ArimaaEngine;
import gamegalaxy.games.arimaa.gui.ArimaaUI;
import gamegalaxy.gui.ApplicationFrame;
import gamegalaxy.tools.ResourceLoader;

import javax.swing.SwingUtilities;

/**
 * This is the main class for activating games in the "Galaxy of Games"
 * It should be used to launch menus for selecting what game to play, matching controls, etc.
 */
public class MainGameGalaxy
{

	/**
	 * Run the main program.
	 * 
	 * @param args Command line arguments, if used.
	 */
	public static void main(String[] args)
	{
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				//Load all graphical resources.
				ResourceLoader loader = new ResourceLoader();
				loader.loadResources("arimaa");
				
				//Load the game engine
				ArimaaEngine engine = new ArimaaEngine();
				
				//Load the GUI
				ArimaaUI gui = new ArimaaUI(engine, loader);
				
				//Add the GUI to the application frame.
				new ApplicationFrame(gui, engine);
			}
		});
	}

}

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

package gamegalaxy.gui;

import gamegalaxy.games.arimaa.engine.ArimaaEngine;
import gamegalaxy.games.arimaa.gui.ArimaaMenu;
import gamegalaxy.games.arimaa.gui.ArimaaUI;
import gamegalaxy.tools.GPLPopup;
import gamegalaxy.tools.GeneralKeyStrokeFactory;
import gamegalaxy.tools.KeyStrokeFactory;
import gamegalaxy.tools.MacKeyStrokeFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A generic frame for putting game applications into.  This frame may also be used for other
 * system level UI elements.  Ultimately, it should contain nothing that is game specific,
 * and only contain data affiliated with  generally running games.
 */
@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame
{
	private KeyStrokeFactory	keyStrokeFactory;
	
	private ArimaaEngine		engine;
	private ArimaaUI				gui;

	/**
	 * 
	 * Create our frame and drop the appropriate game UI into it.
	 *
	 * @param gui The Game UI associated with this frame.
	 */
	public ApplicationFrame(ArimaaUI gui, ArimaaEngine engine)
	{
		super("Arimaa");
		
		this.gui = gui;
		this.engine = engine;
		
		// Determine which keystroke factory to grab.
		if (System.getProperty("os.name").contains("Mac OS X"))
		{
			keyStrokeFactory = new MacKeyStrokeFactory();
		}
		else  // use this for Windows and other non-Mac systems.
		{
			keyStrokeFactory = new GeneralKeyStrokeFactory();
		}
	
		//Create the main window.
		Dimension preferredSize = gui.getPreferredSize();
		
		//Increase the height enough for the menu.
		preferredSize.height += 43; 

		//Finish setting up the frame.
		setSize(preferredSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		//Create the menubar
		gui.setMenu (new ArimaaMenu(gui,engine,keyStrokeFactory,this));
		setJMenuBar(gui.getMenu());
		
		//Drop the GUI in.
		add(gui);
		
		//Display the frame.
		setVisible(true);
		
	}
}

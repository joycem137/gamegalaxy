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

import gamegalaxy.games.arimaa.gui.ArimaaUI;
import gamegalaxy.tools.GPLPopup;
import gamegalaxy.tools.GeneralKeyStrokeFactory;
import gamegalaxy.tools.KeyStrokeFactory;
import gamegalaxy.tools.MacKeyStrokeFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * A generic frame for putting game applications into.  This frame may also be used for other
 * system level UI elements.  Ultimately, it should contain nothing that is game specific,
 * and only contain data affiliated with  generally running games.
 */
@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame
{
	private KeyStrokeFactory	keyStrokeFactory;
	private JDialog				gplPopup;

	/**
	 * 
	 * Create our frame and drop the appropriate game UI into it.
	 *
	 * @param gui The Game UI associated with this frame.
	 */
	public ApplicationFrame(ArimaaUI gui)
	{
		super("Arimaa");
		
		// Determine which keystroke factory to grab.
		if (System.getProperty("os.name").contains("Mac OS X"))
		{
			keyStrokeFactory = new MacKeyStrokeFactory();
		}
		else  // use this for Windows and other non-Mac systems.
		{
			keyStrokeFactory = new GeneralKeyStrokeFactory();
		}
		
		//Create some components for the window.
		gplPopup = new GPLPopup(this);
	
		//Create the main window.
		Dimension preferredSize = gui.getPreferredSize();
		
		//Increase the height enough for the menu.
		preferredSize.height += 43; 

		//Finish setting up the frame.
		setSize(preferredSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		//Create the menubar
		createMenu();
		
		//Drop the GUI in.
		add(gui);
		
		//Display the frame.
		setVisible(true);
	}
	
	//Create the standard UI menu bar.
	private void createMenu()
	{
		JMenuBar jMenuBar = new JMenuBar();

		// Build the file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		// Build the exit item.
		JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitItem.setAccelerator(keyStrokeFactory.getExitGameAcceleratorKeyStroke());
		fileMenu.add(exitItem);

		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				System.exit(0);
			}
		});

		// Add the file menu to the menu bar.
		jMenuBar.add(fileMenu);

		// Create the help menu for the GNU stuff.
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// Create the "GPL Text" item.
		JMenuItem gplItem = new JMenuItem("About GPL", KeyEvent.VK_G);
		helpMenu.add(gplItem);

		// Add the GPL text to the gplItem
		gplItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				displayGPLPopup();
			}
		});

		// Add the help menu to the menu bar.
		jMenuBar.add(helpMenu);
		
		setJMenuBar(jMenuBar);
	}

	private void displayGPLPopup()
	{
		gplPopup.setVisible(true);
	}
}

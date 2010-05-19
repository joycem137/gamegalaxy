package gamegalaxy.games.arimaa.gui;

import gamegalaxy.games.arimaa.engine.ArimaaEngine;
import gamegalaxy.tools.GPLPopup;
import gamegalaxy.tools.KeyStrokeFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

public class ArimaaMenu extends JMenuBar{
	private ArimaaUI 			gui;
	private ArimaaEngine 		engine;
	private KeyStrokeFactory 	keyStrokeFactory;
	private JFrame				parentFrame;
	
	private JDialog				gplPopup;
	
	private JMenuItem keyboardInputItem;
	
	public ArimaaMenu( ArimaaUI gui, ArimaaEngine engine, KeyStrokeFactory keyStrokeFactory,
			JFrame parentFrame){
		this.gui = gui;
		this.engine = engine;
		this.keyStrokeFactory = keyStrokeFactory;
		this.parentFrame = parentFrame;
		
		gplPopup = new GPLPopup(parentFrame);
		
		createMenu();
	}
	
	//Create the standard UI menu bar.
	private void createMenu()
	{

		
		//JMenuBar jMenuBar = new JMenuBar();

		// Build the file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
				
		// Build the New Game item.
		JMenuItem newGameItem = new JMenuItem("New Game", KeyEvent.VK_N);
		newGameItem.setAccelerator(keyStrokeFactory.getNewGameAcceleratorKeyStroke());
		fileMenu.add(newGameItem);
		
		newGameItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				engine.newGame();
			}
		});
		
		//FIXME: Test item
		/*
		JMenuItem testItem = new JMenuItem("Undo Step", KeyEvent.VK_S);
		fileMenu.add(testItem);
		
		testItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				engine.undoStep();
			}
		});
		*/
		
		// Build the Save Game item.
		
		// Build the Load Game item.
				
		// Build the Exit item.
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
		this.add(fileMenu);

		// Create the keyboard menu
		JMenu keyboardMenu = new JMenu("Keyboard");
		keyboardMenu.setMnemonic(KeyEvent.VK_K);
		
		// Create the keyboard controls item
		JMenuItem keyboardItem = new JMenuItem("Keyboard Controls", KeyEvent.VK_G);
		keyboardMenu.add(keyboardItem);
		keyboardItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				displayKeyboardPopup();
			}
		});
		
		//Separate the radio buttons from the previous option(s)
		keyboardMenu.addSeparator();
		ButtonGroup groupInputMode = new ButtonGroup();
		
		//Create the mouse input option
		final JMenuItem mouseInputItem = new JRadioButtonMenuItem("Mouse Input");
		mouseInputItem.setSelected(true);
		mouseInputItem.setMnemonic(KeyEvent.VK_M);
		groupInputMode.add(mouseInputItem);
		mouseInputItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				gui.setUseMouse(true);
				

			}
		});	
		keyboardMenu.add(mouseInputItem);
		
		//Create the keyboard input option
		//FIXME: This menu item should be grayed out if we cannot select it
		keyboardInputItem = new JRadioButtonMenuItem("Keyboard Input");
		keyboardInputItem.setMnemonic(KeyEvent.VK_K);
		groupInputMode.add(keyboardInputItem);
		keyboardInputItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				gui.setUseMouse(false);
				
				//If this wasn't successful, reset to marking mouse as the selected mode
				if (gui.isUsingMouse()){
					mouseInputItem.doClick();
				}
			}
		});	

		keyboardMenu.add(keyboardInputItem);
		
		keyboardInputItem.setEnabled(false);
		
		//When the menu is clicked, check if we should grey-out the KeyboardInput option
		keyboardMenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				keyboardInputItem.setEnabled(false);
			}
		});	
		
		// Add the keyboard menu to the menu bar.
		this.add(keyboardMenu);
		
		
		
		
		// Create the history menu
		JMenu historyMenu = new JMenu("History");
		historyMenu.setMnemonic(KeyEvent.VK_I);

		// Create the undo item
		JMenuItem undoCurrentItem = new JMenuItem("Undo Turn", KeyEvent.VK_U);
		undoCurrentItem.setAccelerator(keyStrokeFactory.getUndoTurnAcceleratorKeyStroke());
		historyMenu.add(undoCurrentItem);

		undoCurrentItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				engine.undoMove();
			}
		});	
		
		// Add the keyboard menu to the menu bar.
		this.add(historyMenu);
		
		// Create the help menu for the GNU stuff.
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// Create the "GPL Text" item.
		JMenuItem gplItem = new JMenuItem("GPL License", KeyEvent.VK_G);
		helpMenu.add(gplItem);

		gplItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				displayGPLPopup();
			}
		});
		



		// Add the help menu to the menu bar.
		this.add(helpMenu);
		
		//FIXME:
		//setJMenuBar(jMenuBar);
	}
	
	/**
	 * Use this to set whether the keyboardInput option can be selected
	 * 
	 * @param input - true for enabled, false for disabled (grayed-out)
	 */
	public void setKeyboardEnabled(boolean input){
		keyboardInputItem.setEnabled(input);
	}

	private void displayGPLPopup()
	{
		gplPopup.setVisible(true);
	}
	
	private void displayKeyboardPopup()
	{
		//Creates a new line and indents it
		String indent = "\n     ";
		
		//Create our pop-up text
		String text = "";
		text += "Keyboard commands for Arimaa:";
		text += "\n\n";
		text += "General Keys:";
		text += indent + "ESCAPE: Return pieceInHand to it's original spot on the board";
		text += indent + "DELETE: Return pieceInHand to the bucket during the setup phase";
		text += indent + "ENTER: End Turn";
		text += indent + "'Z': Undo Turn (can also be used during setup)";
		text += indent + "'/': Toggles between mouse and keyboard controls";
		text += "\n\n" ;
		text += "These commands only work when using keyboard controls:";
		text += indent + "ARROWS: Move selection";
		text += indent + "SPACE: Grab/Drop piece at the selected location";


		//And create a basic pop-up with that text
		JOptionPane.showMessageDialog(parentFrame,text,"Keyboard controls",JOptionPane.INFORMATION_MESSAGE);
	}
}

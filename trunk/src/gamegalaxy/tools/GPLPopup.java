/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gamegalaxy (originally this one was part of gjset).
 *  
 *  gamegalaxy is Copyright 2009 Joyce Murton and Andrea Kilpatrick
 *  gjset is Copyright 2008-2009 Joyce Murton and Andrea Kilpatrick
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
package gamegalaxy.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class handles everything needed to create a little pop up window that displays the GPL license thingie.
 * <P>
 * The actual license is stored externally to this class, in the /resources/COPYING folder.
 */
@SuppressWarnings("serial")
public class GPLPopup extends JDialog
{
	private String	gplTextString;

	/**
	 * 
	 * Create a dialog window that's tied to the passed in {@link JFrame} object.
	 * The dialog window contains the GPL, along with the ability to scroll through the GPL to read it.
	 *\
	 * @param parentFrame The parent JFrame object.
	 */
	public GPLPopup(JFrame parentFrame)
	{	
		// Create the basic dialog
		super(parentFrame, "GNU Public License", true);
		setSize(new Dimension(600, 600));
		setLayout(new BorderLayout());

		//Load the GPL text
		loadText();
		
		// Create the text area for the GPL
		JTextArea textPane = new JTextArea();
		textPane.setText(gplTextString);
		textPane.setEditable(false);

		// Create a scroll pane to allow the text to be scrolled.
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Ensure that we're scrolled to the top of the pane.
		textPane.setCaretPosition(0);

		// Add the text area to the dialog
		add(scrollPane, BorderLayout.CENTER);

		// Create a button for the dialog
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		add(button, BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 * Construct the class to read in the GPL license.
	 */
	public void loadText()
	{
		// Load the GPL text from memory.
		File gplFile = new File("resources/COPYING");
		
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(gplFile);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		InputStreamReader reader = new InputStreamReader(inputStream);

		char gplTextChars[] = new char[36000];
		int charsRead = 0;
		try
		{
			charsRead = reader.read(gplTextChars);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Read " + charsRead + " bytes from GPL.");

		gplTextString = new String(gplTextChars);
	}
}


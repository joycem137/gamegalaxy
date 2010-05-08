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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
		String gplTextString = loadText();
		
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
	 * This loads the GPL license from a file in to the global 
	 */
	public String loadText()
	{
		String gplTextString;
		
		//Get the path of the file and confirm it is valid
		String path = "/resources/COPYING";
		URL gplURL = getClass().getResource(path);
		
		if(gplURL == null)
		{
			System.err.println("Could not find GPL file " + path);
			System.exit(-1);
		}
		
		//Open the file for reading
		InputStream inputStream = null;
		try
		{
			inputStream = gplURL.openStream();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		InputStreamReader reader = new InputStreamReader(inputStream);



		
		//Adjust the array size if using a larger license! (Included license is 32471 characters)
		char gplTextChars[] = new char[36000];
		
		//Tracks the total number of characters read
		int charsRead = 0;
		
		//Read each character of the license file
		try
		{
			charsRead = reader.read(gplTextChars);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//(Optional) This can safely be commented out 
		//Output the # of characters to the terminal
		System.out.println("Read " + charsRead + " bytes from GPL.");

		//Convert the characters to a string
		gplTextString = new String(gplTextChars);
		
		//This shortens the string to only include the characters found in the file
		//Otherwise we get null characters appended to the end
		gplTextString = gplTextString.substring(0,charsRead);

		return gplTextString;
	}
}


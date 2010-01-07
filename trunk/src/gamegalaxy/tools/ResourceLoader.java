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

package gamegalaxy.tools;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

/**
 * The resource loader will look into the .properties file for the appropriate application
 * and loads all of the graphics related to it.  Those graphics are then stored in
 * this file for later access.
 * <P>
 * This allows us to load all of the graphics at start up and have a single copy of each
 * game.
 */
public class ResourceLoader
{
	private static final String RESOURCE_DIRECTORY = "resources";
	private Map<String, Image> map;
	
	/**
	 * Construct the resource loader with a blank image map.
	 *
	 */
	public ResourceLoader()
	{
		map = new HashMap<String, Image>();
	}
	
	/**
	 * 
	 * Load all of the resources associated with the indicated application name.
	 *
	 * @param applicationName
	 */
	public void loadResources(String applicationName)
	{
		//Load the properties file for this application
		String propertiesPath = "/" + RESOURCE_DIRECTORY + "/" + applicationName + ".properties";
		URL propertiesURL = getClass().getResource(propertiesPath);
		
		//Verify that we found a file
		if(propertiesURL == null)
		{
			System.err.println("Could not load path " + propertiesPath);
			System.exit(-1);
		}
		
		//Now actually load the properties.
		Properties resources = new Properties();
		try
		{
			InputStream inStream = propertiesURL.openStream();
			resources.load(inStream);
		} catch (IOException e1)
		{
			e1.printStackTrace();
			System.err.println("Malformatted resource manifest");
			System.exit(-1);
		}
		
		//Now run through the various image files and load them.
		Iterator iterator = resources.keySet().iterator();
		
		while(iterator.hasNext())
		{	
			//Get the image name and filename for each item, then load it and store it.
			String name = (String)iterator.next();
			String filename = resources.getProperty(name);
			
			String path = "/" + RESOURCE_DIRECTORY + "/" + applicationName + "/" + filename;
			URL imageFile = getClass().getResource(path);

			//Verify that we found a file
			if(imageFile == null)
			{
				System.err.println("Could not load path " + path);
				System.exit(-1);
			}
			
			//Read the file in.
			try
			{
				Image image = ImageIO.read(imageFile);
				
				if(image== null)
				{
					System.err.println("Error Loading " + path);
					System.exit(-1);
				}
				
				//Store the image with its name
				map.put(name, image);
			} catch (IOException e)
			{
				e.printStackTrace();
				System.err.println("Failed to load " + path);
				System.exit(-1);
			}
		}
	}
	
	/**
	 * 
	 * Returns the image object associated with the indicated image name.
	 *
	 * @param name
	 * @return
	 */
	public Image getResource(String name)
	{
		if(map.containsKey(name))
		{
			return (Image)map.get(name);
		}
		else
		{
			System.err.println("Resource Not Found: " + name);
			System.exit(-1);
			return null;
		}
	}
}

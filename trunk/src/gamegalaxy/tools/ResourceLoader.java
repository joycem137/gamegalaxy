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

package gamegalaxy.tools;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

/**
 * 
 */
public class ResourceLoader
{
	private static final String RESOURCE_DIRECTORY = "resources";
	private Map<String, Image> map;
	
	public ResourceLoader()
	{
		map = new HashMap<String, Image>();
	}
	
	public void loadResources(String applicationName)
	{
		//Load the properties file for this application
		String propertiesPath = "resources" + File.separator + applicationName + ".properties";
		FileInputStream inStream = null;
		try
		{
			inStream = new FileInputStream (propertiesPath);
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
			System.err.println("Could not open resource manifest at " + propertiesPath);
			System.exit(-1);
		}
		
		//Now actually load the properties.
		Properties resources = new Properties();
		try
		{
			resources.load(inStream);
		} catch (IOException e1)
		{
			e1.printStackTrace();
			System.err.println("Malformatted resource manifest");
			System.exit(-1);
		}
		
		Iterator iterator = resources.keySet().iterator();
		
		while(iterator.hasNext())
		{
			String name = (String)iterator.next();
			String filename = resources.getProperty(name);
			
			String path = RESOURCE_DIRECTORY + File.separator + applicationName + File.separator + filename;
			Image image = null;
			try
			{
				image = ImageIO.read(new File(path));
			} catch (IOException e)
			{
				e.printStackTrace();
				System.err.println("Failed to load " + path);
				System.exit(-1);
			}
			
			//Store the image with its name
			map.put(name, image);
		}
	}
	
	public Image getResource(String name)
	{
		return (Image)map.get(name);
	}
}

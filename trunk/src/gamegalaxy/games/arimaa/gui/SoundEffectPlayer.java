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
 */
package gamegalaxy.games.arimaa.gui;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 */
public class SoundEffectPlayer
{

	private Clip	pickupSound;
	private Clip	dropSound;
	private Clip	trapSound;
	private Clip	bzztSound;

	public SoundEffectPlayer()
	{
		pickupSound = loadSoundFile("resources/arimaa/pickup.wav");
		dropSound = loadSoundFile("resources/arimaa/dropsound.wav");
		trapSound = loadSoundFile("resources/arimaa/trap.wav");
		bzztSound = loadSoundFile("resources/arimaa/bzzt.wav");
	}

	/**
	 * TODO: Describe method
	 *
	 * @param string
	 * @return
	 */
	private Clip loadSoundFile(String filename)
	{
		try
		{
			File soundFile = new File(filename);
			AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);

			DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(sound);
			sound.close();
			return clip;

		} catch (UnsupportedAudioFileException e)
		{
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		} catch (LineUnavailableException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void playSound(Clip sound)
	{
		sound.setMicrosecondPosition(0);
		sound.start();
	}

	public void playPickupSound()
	{
		playSound(pickupSound);
	}

	/**
	 * TODO: Describe method
	 *
	 */
	public void playDropSound()
	{
		playSound(dropSound);
	}
	
	public void playTrapSound()
	{
		playSound(trapSound);
	}
	
	public void playBzztSound()
	{
		playSound(bzztSound);
	}
}

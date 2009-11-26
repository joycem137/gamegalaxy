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
 *  
 */
package gamegalaxy.games.arimaa.data;


/**
 * 
 */
public class PieceData
{
	
	public static final int RABBIT 	= 1;
	public static final int CAT 	= 2;
	public static final int DOG		= 3;
	public static final int HORSE	= 4;
	public static final int CAMEL	= 5;
	public static final int ELEPHANT= 6;
	
	private int	color;
	private int	value;
	private PiecePosition	position;
	
	public PieceData(int color, int value)
	{
		this.color = color;
		this.value = value;
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public String getNameString()
	{
		switch(value)
		{
			case RABBIT: return "Rabbit";
			case CAT: return "Cat";
			case DOG: return "Dog";
			case HORSE: return "Horse";
			case CAMEL: return "Camel";
			case ELEPHANT: return "Elephant";
			default: return "";
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public String getColorString()
	{
		if(color == GameConstants.GOLD) return "Gold";
		else return "Silver";
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getColor()
	{
		return color;
	}
	
	public String toString()
	{
		return getColorString() + " " + getNameString();
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param space
	 */
	public void setPosition(PiecePosition position)
	{
		this.position = position;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public PiecePosition getPosition()
	{
		return position;
	}
}

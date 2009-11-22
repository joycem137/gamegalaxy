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
	private int	type;

	
	public PieceData(int color, int type)
	{
		this.color = color;
		this.type = type;
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public String getNameString()
	{
		switch(type)
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

}

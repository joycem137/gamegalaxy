package gamegalaxy.games.arimaa.gui;


/**
 * A generic interface for any object that can hold a playing piece.
 */
public interface PieceHolder
{

	/**
	 * Called in order to drop the current playing piece onto the implementing class.
	 *
	 * @param piecePanel The playing piece being dropped.
	 */
	void dropPiece(PiecePanel piecePanel);

	/**
	 * Called to remove the playing piece from the implementing class.
	 *
	 * @param piecePanel The playing piece to be removed.
	 */
	void removePiece(PiecePanel piecePanel);

}

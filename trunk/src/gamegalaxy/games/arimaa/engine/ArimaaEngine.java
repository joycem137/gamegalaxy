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
package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.BucketPosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.GameState;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.PiecePosition;
import gamegalaxy.games.arimaa.gui.ArimaaUI;

import java.util.Iterator;
import java.util.List;

/**
 * This class manages all of the data associated with an Arimaa game. It accepts and validates incoming moves as well as storing all of the data for
 * representing the board.
 */
public class ArimaaEngine
{
	// The current game state.
	private GameState	currentGameState;

	// Store a link to the UI
	private ArimaaUI	gui;

	/**
	 * Create the game engine.
	 * 
	 */
	public ArimaaEngine()
	{
		currentGameState = new GameState();
	}

	/**
	 * Connect the UI to the game engine and display the latest game state.
	 * 
	 * @param gui
	 */
	public void linkGUI(ArimaaUI gui)
	{
		this.gui = gui;

		gui.displayGameState(getCurrentGameState());
	}

	/**
	 * Returns true if the indicated piece is able to move.
	 * 
	 * @param data
	 * @return
	 */
	public boolean canPieceBeMoved(PieceData piece)
	{
		// If we've exceeded our moves, we can't do this.
		if (currentGameState.getNumMoves() >= 4) return false;

		if (currentGameState.isSetupPhase())
		{
			// If we've got the wrong color, we can't move this piece.
			return piece.getColor() == currentGameState.getCurrentPlayer();
		}
		else if (currentGameState.isGameOn())
		{
			// Get the position of the piece we're looking at.
			PiecePosition piecePosition = piece.getPosition();
			BoardData board = currentGameState.getBoardData();

			// Handle the case where our piece does not have a position.
			if (piecePosition == null) return false;

			// Make sure that this piece has somewhere to move
			if (!board.pieceHasSpaceToMoveInto(piece)) return false;

			// Check the color of this piece
			if (piece.getColor() != currentGameState.getCurrentPlayer()) // The
			// piece
			// is
			// the
			// opposite
			// player
			// 's
			{
				// If we have a forced move, you can't do this.
				if (currentGameState.getPushPosition() != null) return false;

				// We can move an opponent's piece into our last space if it was
				// a bigger piece.
				BoardPosition position = (BoardPosition) piece.getPosition();
				if (currentGameState.getPullPosition() != null && position.distanceFrom(currentGameState.getPullPosition()) == 1
						&& piece.getValue() < currentGameState.getLastPieceMoved().getValue()) return true;

				// The piece can only be pushed if there are at least two moves
				// left.
				if (currentGameState.getNumMoves() > 2) return false;

				return board.pieceCanBePushed(piece);
			}
			else
			{
				// Verify that the piece is not frozen
				if (board.isPieceFrozen(piece)) return false;

				// if we have a pushPosition, verify that we can move into that spot.
				if (currentGameState.getPushPosition() != null)
				{
					// Pieces can only push pieces of lower value.
					if (piece.getValue() <= currentGameState.getLastPieceMoved().getValue()) return false;

					// Verify that this piece can actually push this particular piece.
					BoardPosition position = (BoardPosition) piece.getPosition();
					return position.distanceFrom(currentGameState.getPushPosition()) == 1;
				}
				else
				{
					return true;
				}
			}
		}
		else
		{
			// Cannot move piece during other phases of the game.
			return false;
		}
	}

	/**
	 * Checks to see if the selected piece can be legally moved from their original space to the new space selected.
	 * 
	 * @param piece
	 *            PieceData of the piece being checked.
	 * @param originalSpace
	 *            PiecePosition where the selected piece started from.
	 * @param newSpace
	 *            target PiecePosition we want to move to.
	 * @return true if a valid move can be made, otherwise false.
	 */
	public boolean isValidMove(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
		BoardData board = currentGameState.getBoardData();
		
		// Abort stupid cases.
		if (newSpace == null) return false;

		// And cases where the piece isn't allowed to move
		if (!canPieceBeMoved(piece)) return false;

		// Let's actually see if the move is valid.
		if (currentGameState.isSetupPhase())
		{
			// Handle the bucket case:
			if (newSpace instanceof BucketPosition)
			{
				// Verify that the piece's color matches the color of the
				// bucket.
				if (originalSpace instanceof BoardPosition)
				{
					BucketPosition position = (BucketPosition) newSpace;
					return piece.getColor() == position.getColor();
				}
				else
				{
					// We should be returning the piece to the original bucket.
					return originalSpace.equals(newSpace);
				}
			}

			// We know that this is a board position
			BoardPosition newPosition = (BoardPosition) newSpace;

			// Return true if this is the same space we are starting from.
			if (originalSpace.equals(newSpace))
			{
				return true;
			}

			// If the space is occupied, a move is invalid. (swaps may be okay
			// though.)
			if (board.isOccupied(newPosition))
			{
				return false;
			}

			// Otherwise, verify that the pair of rows is okay.
			if (piece.getColor() == GameConstants.GOLD)
			{
				return newPosition.getRow() >= 6;
			}
			else
			{
				return newPosition.getRow() <= 1;
			}
		}
		else if (currentGameState.isGameOn())
		{
			// You can't move things to buckets here.
			if (newSpace instanceof BucketPosition) return false;

			// We know this is a BoardPosition now
			BoardPosition newPosition = (BoardPosition) newSpace;

			// We also know the original position is a board position because
			// the piece can be moved.
			BoardPosition originalPosition = (BoardPosition) originalSpace;

			// Return true if this is the same space we are starting from.
			if (originalPosition.equals(newPosition)) return true;

			// Otherwise, if the space is occupied, return false.
			/*
			 * Note that this is temporary. We have to fix this when we can push/pull pieces.
			 */
			if (board.isOccupied(newPosition)) return false;

			// Verify that we have moves remaining
			if (currentGameState.getRemainingMoves() == 0) return false;

			// Now check what color we're working with.
			if (piece.getColor() == currentGameState.getCurrentPlayer())
			{
				// If we have to move into the pushPosition, we require the
				// space to match
				if (currentGameState.getPushPosition() != null)
				{
					return newPosition.equals(currentGameState.getPushPosition());
				}

				// This is a piece of the current player's color
				if (piece.getValue() == PieceData.RABBIT)
				{
					// Movement is only okay forward, left, and right.
					if (newPosition.equals(originalPosition.moveLeft())) return true;
					if (newPosition.equals(originalPosition.moveRight())) return true;
					if (piece.getColor() == GameConstants.GOLD)
					{
						if (newPosition.equals(originalPosition.moveUp())) return true;
					}
					else
					{
						if (newPosition.equals(originalPosition.moveDown())) return true;
					}
					return false;
				}
				else
				{
					// Okay! Since we are good with that, we can move one space.
					return originalPosition.distanceFrom(newPosition) == 1;
				}
			}
			else
			{
				// We're dealing with an opponent's piece.

				// If we're moving the piece into the pull space, and we are a
				// valid piece for doing so, we're good.
				if (currentGameState.getPullPosition() != null && piece.getValue() < currentGameState.getLastPieceMoved().getValue()
						&& newPosition.equals(currentGameState.getPullPosition())) return true;

				// If we're pushing more than one space, this is invalid.
				if (originalPosition.distanceFrom(newPosition) > 1) return false;

				// The only thing left to check is whether or not this is really
				// a push.
				if (board.pieceCanBePushed(piece)) return true;
			}
		}

		// We have no moves to do if we're not playing the game or setting
		// things up.
		return false;
	}

	/**
	 * Checks to see if the pieces in the original and target squares can be swapped. Swaps are only legal during the Setup phase.
	 * 
	 * @param piece
	 *            PieceData of the piece in the original space.
	 * @param originalSpace
	 *            PiecePosition where the selected piece started from.
	 * @param newSpace
	 *            target PiecePosition we want to swap with.
	 * @return true if a valid swap can be made, otherwise false.
	 */
	public boolean isValidSwap(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
		if (newSpace == null) return false;

		// We can only swap during the Setup phase, with pieces of the same
		// color.
		if (currentGameState.isSetupPhase())
		{
			// Return false if trying to put the piece back in the bucket.
			if (newSpace instanceof BucketPosition)
			{
				return false;
			}

			// We now know this is a board position
			BoardPosition newPosition = (BoardPosition) newSpace;

			// Return false if this is the same space we are starting from.
			if (originalSpace.equals(newSpace))
			{
				return false;
			}

			// Check if the target piece is the same color.
			if (currentGameState.getBoardData().isOccupied(newPosition))
			{
				PieceData targetPiece = currentGameState.getBoardData().getPieceAt(newPosition);
				return piece.getColor() == targetPiece.getColor();
			}
		}

		// Return false under all other circumstances.
		return false;
	}

	/**
	 * Submit the indicated move to the game engine for implementation. Upon completion of the move, the engine will ask the GUI to update to the latest game
	 * state.
	 * 
	 * @param piece
	 * @param originalSpace
	 * @param newSpace
	 */
	public void movePiece(PieceData piece, PiecePosition originalSpace, PiecePosition newSpace)
	{
		BoardData board = currentGameState.getBoardData();
		
		// Validate the move and don't move onto the same space.
		if (isValidMove(piece, originalSpace, newSpace) && !originalSpace.equals(newSpace))
		{
			if (originalSpace instanceof BucketPosition && newSpace instanceof BoardPosition)
			{
				// Moving from bucket to board.
				// First remove the piece from the bucket.
				removePieceFromBucket((BucketPosition) originalSpace, piece);

				// Now place the piece on the board in the correct space.
				board.placePiece(piece, (BoardPosition) newSpace);
			}
			else if (newSpace instanceof BoardPosition)
			{
				// Moving from board to board.
				BoardPosition originalPosition = (BoardPosition) originalSpace;
				BoardPosition newPosition = (BoardPosition) newSpace;

				board.removePiece(originalPosition);

				board.placePiece(piece, newPosition);

				if (currentGameState.isGameOn())
				{
					// Increment the number of moves
					currentGameState.incrementNumMoves();

					if (piece.getColor() != currentGameState.getCurrentPlayer())
					{
						if (currentGameState.getPullPosition() != null && currentGameState.getPullPosition().equals(newPosition))
						{
							// This cannot be a push or a pull.
							currentGameState.setPullPosition(null);
							currentGameState.setPushPosition(null);
						}
						else
						{
							// This is a push.
							currentGameState.setPushPosition(originalPosition);
						}
					}
					else
					{
						// This is a move of this player's color.
						if (currentGameState.getPushPosition() == null)
						{
							// The last move was *not* a push position. You can
							// record the piece movement.
							currentGameState.setPullPosition(originalPosition);
						}
						else
						{
							// The last move was a push. Reset it.
							currentGameState.setPushPosition(null);
						}
					}
				}
				currentGameState.setLastPieceMoved(piece);
			}
			else
			{
				// Moving from board to bucket.
				BoardPosition originalPosition = (BoardPosition) originalSpace;
				BucketPosition newPosition = (BucketPosition) newSpace;

				// Remove the piece from the board.
				board.removePiece(originalPosition);

				// Add the piece to the bucket.
				currentGameState.addToBucket(piece, newPosition);
			}

			if (currentGameState.isGameOn())
			{
				// Check the traps to see if there are pieces in them and if
				// they are dead.
				checkTheTraps();

				// Check to see if anyone has won the game.
				checkForWinner();
			}
		}

		// Update the UI with the results.
		gui.displayGameState(getCurrentGameState());
	}

	/**
	 * Remove the indicated piece from the bucket.
	 * 
	 * @param bucketPosition
	 *            The {@link BucketPosition} object that represents the bucket to remove the piece from.
	 * @param piece
	 *            The {@link PieceData} object that represents the piece to remove.
	 */
	private void removePieceFromBucket(BucketPosition bucketPosition, PieceData piece)
	{
		currentGameState.removePieceFromBucket(piece, bucketPosition);
	}

	/**
	 * Swaps the location of two pieces. This is used only during the Setup phase of the game.
	 * 
	 * @param piece1
	 *            PieceData of the first piece.
	 * @param piece2
	 *            PieceData of the second piece.
	 * @param space1
	 *            PiecePosition of the first piece (either a bucket or board location)
	 * @param space2
	 *            PiecePosition of the second piece (board location only)
	 */
	public void swapPieces(PieceData piece1, PieceData piece2, PiecePosition space1, PiecePosition space2)
	{
		if (!isValidSwap(piece1, space1, space2)) return;

		// Assert that the second space is a board position
		assert space2 instanceof BoardPosition;
		BoardPosition space2Position = (BoardPosition) space2;

		// Remove the second piece from the board.
		currentGameState.getBoardData().removePiece(space2Position);

		// Check if the first piece came from a bucket.
		if (space1 instanceof BucketPosition)
		{
			BucketPosition space1Position = (BucketPosition) space1;

			currentGameState.removePieceFromBucket(piece1, space1Position);
			currentGameState.addToBucket(piece2, space1Position);
		}
		// Otherwise both pieces came from the board.
		else
		{
			BoardPosition space1Position = (BoardPosition) space1;
			// remove the first piece and put the second piece in its place.
			currentGameState.getBoardData().removePiece(space1Position);
			currentGameState.getBoardData().placePiece(piece2, space1Position);
		}

		// finally, put the first piece where the second one was.
		currentGameState.getBoardData().placePiece(piece1, space2Position);

		// Update the UI with the latest game state.
		gui.displayGameState(getCurrentGameState());
	}

	/**
	 * End the current player's turn.
	 * 
	 */
	public void endTurn()
	{
		currentGameState.endTurn();

		gui.displayGameState(getCurrentGameState());
	}

	/**
	 * Evaluate the current game situation to determine if there has been a winner.
	 * 
	 */
	private void checkForWinner()
	{
		boolean foundGoldRabbit = false;
		boolean foundSilverRabbit = false;
		Iterator<PieceData> iterator = currentGameState.getPieces().iterator();
		while (iterator.hasNext())
		{
			PieceData piece = iterator.next();

			// If we find a rabbit piece, check to see if it is in the back row.
			if (piece.getValue() == PieceData.RABBIT)
			{
				// Check if the rabbit is in the back row.
				if (piece.getPosition() != null)
				{
					if (piece.getPosition() instanceof BoardPosition)
					{
						BoardPosition position = (BoardPosition) piece.getPosition();
						if (piece.getColor() == GameConstants.GOLD)
						{
							foundGoldRabbit = true;
							if (position.getRow() == 0)
							{
								// Gold won!!!
								currentGameState.setGameWinner(GameConstants.GOLD);
							}
						}
						else
						{
							foundSilverRabbit = true;
							if (position.getRow() == 7)
							{
								// Silver won!
								currentGameState.setGameWinner(GameConstants.SILVER);
							}
						}
					}
				}
			}
		}

		// If we made it here, make sure we found rabbits of each color.
		if (!foundGoldRabbit)
		{
			currentGameState.setGameWinner(GameConstants.SILVER);
		}
		else if (!foundSilverRabbit)
		{
			currentGameState.setGameWinner(GameConstants.GOLD);
		}
	}

	/**
	 * Check all of the traps to see if there are any pieces that need to be captured.
	 * 
	 */
	private void checkTheTraps()
	{
		BoardData board = currentGameState.getBoardData();
		
		Iterator<BoardPosition> iterator = BoardData.getTrapPosition().iterator();
		while (iterator.hasNext())
		{
			BoardPosition trapPosition = iterator.next();
			if (board.isOccupied(trapPosition))
			{
				PieceData trappedPiece = board.getPieceAt(trapPosition);
				boolean pieceIsDead = true;

				// Check for allies in each direction:
				List<BoardPosition> adjacentSpaces = trapPosition.getAdjacentSpaces();
				Iterator<BoardPosition> adjacentIterator = adjacentSpaces.iterator();
				while (adjacentIterator.hasNext())
				{
					BoardPosition space = adjacentIterator.next();
					if (board.isOccupied(space))
					{
						PieceData adjacentPiece = board.getPieceAt(space);
						if (adjacentPiece.getColor() == trappedPiece.getColor())
						{
							// We found an ally! We're safe!
							pieceIsDead = false;
						}
					}
				}

				// Now kill the piece.
				if (pieceIsDead)
				{
					// Remove the piece from the board.
					board.removePiece(trapPosition);

					// Move the piece to the appropriate bucket.
					currentGameState.addToBucket(trappedPiece, new BucketPosition(trappedPiece.getColor()));
				}
			}
		}
	}

	/**
	 * Randomly move the pieces from the bucket to the board for the current player.
	 * 
	 */
	public void doRandomSetup()
	{
		currentGameState.doRandomSetup();
		// Update the UI
		gui.displayGameState(getCurrentGameState());
	}

	/**
	 * Get the current {@link GameState} object. This method is currently in place because I want a simple method to return said state, but in the future, we
	 * may be using a game state to store all data and not have it duplicated in the engine.
	 * 
	 * @return
	 */
	private GameState getCurrentGameState()
	{
		return currentGameState;
	}

}

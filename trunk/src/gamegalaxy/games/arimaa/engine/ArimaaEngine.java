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
import gamegalaxy.games.arimaa.data.StepData;
import gamegalaxy.tools.SimpleObservable;

import java.util.Observer;

/**
 * This class manages all of the data associated with an Arimaa game. It accepts and validates incoming moves as well as storing all of the data for
 * representing the board.
 */
public class ArimaaEngine
{
	// The current game state.
	private GameState	currentGameState;
	
	// The game state at the beginning of the current turn, for undo purposes.
	private GameState	lastGameState;

	// Store a link to the UI
	private SimpleObservable observable;

	/**
	 * Create the game engine.
	 * 
	 */
	public ArimaaEngine()
	{
		currentGameState = new GameState();
		currentGameState.initializeGameState();
		lastGameState = currentGameState.copy();
		observable = new SimpleObservable();
	}

	/**
	 * Connect the UI to the game engine and display the latest game state.
	 * 
	 * @param gui
	 */
	public void addObserver(Observer observer)
	{
		observable.addObserver(observer);
		observable.notifyObservers();
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
		if (currentGameState.getNumSteps() >= 4) return false;

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

			if (piece.getColor() != currentGameState.getCurrentPlayer()) // The piece is the opposite player 's
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
				if (currentGameState.getNumSteps() > 2) return false;

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
	public boolean isValidStep(StepData step)
	{
		BoardData board = currentGameState.getBoardData();
		PiecePosition destination = step.getDestination();
		PieceData piece = step.getPiece();
		
		// Abort stupid cases.
		if (destination == null) return false;

		// And cases where the piece isn't allowed to move
		if (!canPieceBeMoved(piece)) return false;

		// Let's actually see if the move is valid.
		if (currentGameState.isSetupPhase())
		{	
			// Handle the bucket case:
			if (destination instanceof BucketPosition)
			{
				// Verify that the piece's color matches the color of the
				// bucket.
				if (piece.getPosition() instanceof BoardPosition)
				{
					BucketPosition position = (BucketPosition) destination;
					return piece.getColor() == position.getColor();
				}
				else
				{
					// We should be returning the piece to the original bucket.
					return piece.getPosition().equals(destination);
				}
			}

			// We know that this is a board position
			BoardPosition newPosition = (BoardPosition) destination;

			// Return true if this is the same space we are starting from.
			if (piece.getPosition().equals(destination))
			{
				return true;
			}

			// If the space is occupied, swaps are okay.
			if (board.isOccupied(newPosition))
			{
				// Check if the target piece is the same color.
				if (currentGameState.getBoardData().isOccupied(newPosition))
				{
					PieceData targetPiece = currentGameState.getBoardData().getPieceAt(newPosition);
					return piece.getColor() == targetPiece.getColor();
				}
			}

			// Otherwise, verify that the pair of rows is okay.
			if (step.getPiece().getColor() == GameConstants.GOLD)
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
			if (destination instanceof BucketPosition) return false;

			// We know this is a BoardPosition now
			BoardPosition newPosition = (BoardPosition) destination;

			// We also know the original position is a board position because
			// the piece can be moved.
			BoardPosition originalPosition = (BoardPosition) piece.getPosition();

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
	 * Submit the indicated move to the game engine for implementation. Upon completion of the move, the engine will ask the GUI to update to the latest game
	 * state.
	 * 
	 * @param piece
	 * @param originalSpace
	 * @param newSpace
	 */
	public void takeStep(StepData step)
	{
		// Validate the move and don't move onto the same space.
		if (isValidStep(step) && !step.getPiece().getPosition().equals(step.getDestination()))
		{
			currentGameState.takeStep(step);
			
			// Update the UI with the results.
			observable.notifyObservers();
		}

	}

	/**
	 * End the current player's turn.
	 * 
	 */
	public void endMove()
	{
		currentGameState.endMove();
		lastGameState = currentGameState.copy();

		observable.notifyObservers();
	}
	
	public void undoMove()
	{
		currentGameState = lastGameState.copy();

		observable.notifyObservers();		
	}

	/**
	 * Randomly move the pieces from the bucket to the board for the current player.
	 * 
	 */
	public void doRandomSetup()
	{
		currentGameState.doRandomSetup();
		
		// Update the UI
		observable.notifyObservers();
	}

	/**
	 * Get the current {@link GameState} object. This method is currently in place because I want a simple method to return said state, but in the future, we
	 * may be using a game state to store all data and not have it duplicated in the engine.
	 * 
	 * @return
	 */
	public GameState getCurrentGameState()
	{
		return currentGameState;
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public boolean lastStepWasCapture()
	{
		return currentGameState.lastStepWasCapture();
	}

}

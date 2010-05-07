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
package gamegalaxy.games.arimaa.engine;

import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.BucketPosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.GameState;
import gamegalaxy.games.arimaa.data.HandPosition;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.StepData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 
 */
public class MoveGenerator
{
	private GameState	gameState;
	private Map<PieceData, List<StepData>> moveDatabase;

	/**
	 * Construct a move generator for the indicated game state.
	 *
	 * @param gameState
	 */
	public MoveGenerator(GameState gameState)
	{
		this.gameState = gameState;
		moveDatabase = new HashMap<PieceData, List<StepData>>();
	}

	/**
	 * Return all possible steps that this piece can take.
	 *
	 * @param piece
	 * @return
	 */
	public List<StepData> generateSteps(PieceData piece)
	{
		if(moveDatabase.containsKey(piece))
		{
			return moveDatabase.get(piece);
		}
		else
		{
			List<StepData> steps;
			
			//First, check the phase.
			if(gameState.isSetupPhase())
			{
				steps = generateSetupSteps(piece);
			}
			else if(gameState.isGameOn())
			{
				steps = generateGameSteps(piece);
			}
			else
			{
				steps = new Vector<StepData>();
			}
			
			moveDatabase.put(piece, steps);
			return steps;
		}
	}

	/**
	 * Reset the internal database of moves.
	 *
	 */
	public void reset()
	{
		moveDatabase.clear();
	}

	/**
	 * Return all moves that can be made during the active game phase.
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> generateGameSteps(PieceData piece)
	{
		//This will pick up the set of available moves
		List<StepData> steps = new Vector<StepData>();
		
		//Ensure that there are moves remaining
		if(gameState.getRemainingMoves() > 0)
		{
			//If there are forced moves, those get added
			if(gameState.getPieceInHand() != null)
			{
				steps.addAll(getForcedSteps(piece));
			}
			//??? 
			else if(piece.getPosition() instanceof BoardPosition)
			{ 
				//If this piece is NOT yours, add moves you can do to an enemy piece
				//IE initiating a push/pull
				if(piece.getColor() != gameState.getCurrentPlayer())
				{			
					steps.addAll(getOpponentPieceSteps(piece));
				}
				//Otherwise, check to ensure the piece isn't frozen
				else if(!gameState.getBoardData().isPieceFrozen(piece))
				{
					//??? forced pushes or something i dunno
					if(gameState.getPushPosition() != null)
					{
						steps.addAll(getForcedPush(piece));
					}
					//Add the normal moves for the piece
					else
					{
						steps.addAll(getNormalSteps(piece));
					}
				}
			}
		}
		
		return steps;
	}

	/**
	 * Return a list of normal moves that this piece can make.
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> getNormalSteps(PieceData piece)
	{
		List<StepData> steps = new Vector<StepData>();
		BoardData board = gameState.getBoardData();
		
		//Find all the open spaces
		BoardPosition position = (BoardPosition) piece.getPosition();
		List<BoardPosition> adjacentSpaces = position.getAdjacentSpaces();
		Iterator<BoardPosition> iterator = adjacentSpaces.iterator();
		while(iterator.hasNext())
		{
			BoardPosition adjacentPosition = iterator.next();
			
			if(!board.isOccupied(adjacentPosition))
			{
				//Handle normal moves to unoccupied locations
				if(piece.getValue() == PieceData.RABBIT)
				{
					//Rabbits can't move backwards
					if(gameState.getCurrentPlayer() == GameConstants.GOLD)
					{
						if(!adjacentPosition.equals(position.moveDown()))
						{
							steps.add(new StepData(piece, adjacentPosition));
						}
					}
					else
					{
						if(!adjacentPosition.equals(position.moveUp()))
						{
							steps.add(new StepData(piece, adjacentPosition));
						}
					}
				}
				else
				{
					//Normal pieces can move to any unoccupied location.
					steps.add(new StepData(piece, adjacentPosition));
				}
			}
			else
			{
				//Handle pushes
				PieceData adjacentPiece = board.getPieceAt(adjacentPosition);
				if(adjacentPiece.getColor() != gameState.getCurrentPlayer() &&
					adjacentPiece.getValue() < piece.getValue())
				{
					//Verify that the other piece has a location to move into.
					List<BoardPosition> spacesToPushInto = adjacentPosition.getAdjacentSpaces();
					Iterator<BoardPosition> pushIterator = spacesToPushInto.iterator();
					boolean canPush = false;
					while(pushIterator.hasNext())
					{
						BoardPosition pushPosition = pushIterator.next();
						if(!board.isOccupied(pushPosition))
						{
							canPush = true;
						}
					}
					
					//Add the move if the piece can push AND there are sufficient moves to do a push
					if(canPush && gameState.getRemainingMoves() >= 2)
					{
						steps.add(new StepData(piece, adjacentPosition));
					}
				}
			}
		}

		return steps;
	}

	/**
	 * Return all of the moves that this opponent's piece can make.
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> getOpponentPieceSteps(PieceData piece)
	{
		List<StepData> steps = new Vector<StepData>();

		BoardPosition position = (BoardPosition)piece.getPosition();
		
		//We can either finish a pull, start a pull, or start a push.
		
		//Check to see if we can finish a pull.
		if(gameState.getLastStep() != null &&
			piece.getValue() < gameState.getLastStep().getPiece().getValue())
		{
			if(gameState.getPullPosition() != null && position.distanceFrom(gameState.getPullPosition()) == 1)
			{
				steps.add(new StepData(piece, gameState.getPullPosition()));
			}
		}
		
		//Check to see if we can start a pull or push...
		if(gameState.getRemainingMoves() >= 2)
		{
			List<BoardPosition> adjacentSpaces = position.getAdjacentSpaces();
			BoardData board = gameState.getBoardData();
			
			List<PieceData> possiblePullers = new Vector<PieceData>();
			List<BoardPosition> openSpaces = new Vector<BoardPosition>();
			boolean foundPusher = false;
			Iterator<BoardPosition> iterator = adjacentSpaces.iterator();
			while(iterator.hasNext())
			{
				BoardPosition adjacentPosition = iterator.next();
				
				if(board.isOccupied(adjacentPosition))
				{
					PieceData adjacentPiece = board.getPieceAt(adjacentPosition);
					if(adjacentPiece.getColor() == gameState.getCurrentPlayer() &&
							!board.isPieceFrozen(adjacentPiece))
					{
						if(adjacentPiece.getValue() > piece.getValue())
						{
							foundPusher = true;
							possiblePullers.add(adjacentPiece);
						}
					}
				}
				else
				{
					openSpaces.add(adjacentPosition);
				}
			}
			
			//Now add any possible pushes.
			if(foundPusher)
			{
				//Add all of the open spaces as possible moves.
				Iterator<BoardPosition> openIterator = openSpaces.iterator();
				while(openIterator.hasNext())
				{
					steps.add(new StepData(piece, openIterator.next()));
				}
			}
			
			//Add the pullers, too.
			Iterator<PieceData> pullerIterator = possiblePullers.iterator();
			while(pullerIterator.hasNext())
			{
				steps.add(new StepData(piece, pullerIterator.next().getPosition()));
			}
		}
		
		return steps;
	}

	/**
	 * Returns all of the moves this piece can make to complete the forced
	 * push.
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> getForcedPush(PieceData piece)
	{
		List<StepData> steps = new Vector<StepData>();
		
		//Verify a few facts about this piece and its relationship to the push piece
		if(piece.getValue() > gameState.getLastStep().getPiece().getValue() &&
			piece.getColor() == gameState.getCurrentPlayer())
		{
			BoardPosition position = (BoardPosition)piece.getPosition();
			if(position.distanceFrom(gameState.getPushPosition()) == 1)
			{
				steps.add(new StepData(piece, gameState.getPushPosition()));
			}
		}
		
		return steps;
	}

	/**
	 * Returns all of the pushes or pulls that the piece in hand can complete
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> getForcedSteps(PieceData piece)
	{
		List<StepData> steps = new Vector<StepData>();
		
		//We can *only* move the piece in question.
		if(gameState.getPieceInHand().equals(piece))
		{
			/*
			 * Now look at each adjacent space.  Since this is a push or pull
			 * we can move anywhere we like, so long as the space is unoccupied,
			 * and we're not moving back to our original location
			 */
			StepData lastStep = gameState.getLastStep();
			BoardData board = gameState.getBoardData();
			HandPosition position = (HandPosition)piece.getPosition();
			BoardPosition oldPosition = (BoardPosition)position.getOldPosition();
			List<BoardPosition> adjacentSpaces = oldPosition.getAdjacentSpaces();
			Iterator<BoardPosition> iterator = adjacentSpaces.iterator();
			while(iterator.hasNext())
			{
				BoardPosition destination = iterator.next();
				if(!board.isOccupied(destination) && 
					!destination.equals(lastStep.getSource()))
				{
					steps.add(new StepData(piece, destination));
				}
			}
		}
		
		return steps;
	}

	/**
	 * Return a list of steps for a piece during the setup phase.
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> generateSetupSteps(PieceData piece)
	{
		List<StepData> steps = new Vector<StepData>();
		
		if(piece.getColor() == gameState.getCurrentPlayer())
		{
			//Return all of the spaces on the board
			int row1;
			int row2;
			if(gameState.getCurrentPlayer() == GameConstants.GOLD)
			{
				row1 = 6;
				row2 = 7;
			}
			else
			{
				row1 = 0;
				row2 = 1;
			}
			
			//Return all of the spaces on the board
			for(int col = 0; col < 8; col++)
			{
				steps.add(new StepData(piece, new BoardPosition(row1, col)));
				steps.add(new StepData(piece, new BoardPosition(row2, col)));
			}
			
			//Add the appropriate bucket.
			steps.add(new StepData(piece, new BucketPosition(gameState.getCurrentPlayer())));
		}
		
		return steps;
	}

}

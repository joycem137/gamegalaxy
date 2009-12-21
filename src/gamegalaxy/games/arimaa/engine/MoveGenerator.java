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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 */
public class MoveGenerator
{
	private GameState	gameState;

	/**
	 * TODO: Describe constructor
	 *
	 * @param gameState
	 */
	public MoveGenerator(GameState gameState)
	{
		this.gameState = gameState;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @return
	 */
	public List<StepData> generateSteps(PieceData piece)
	{
		//First, check the phase.
		if(gameState.isSetupPhase())
		{
			return generateSetupSteps(piece);
		}
		else if(gameState.isGameOn())
		{
			return generateGameSteps(piece);
		}
		else
		{
			return new Vector<StepData>();
		}
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @return
	 */
	private List<StepData> generateGameSteps(PieceData piece)
	{
		List<StepData> steps = null;
		
		if(gameState.getRemainingMoves() > 0)
		{
			//Check to see about forced moves, first.
			if(gameState.getPieceInHand() != null)
			{
				steps = getForcedSteps(piece);
			}
			else if(piece.getPosition() instanceof BoardPosition)
			{ 
				if(piece.getColor() != gameState.getCurrentPlayer())
				{
					steps = getOpponentPieceSteps(piece);
				}
				else if(!gameState.getBoardData().isPieceFrozen(piece))
				{
					if(gameState.getPushPosition() != null)
					{
						steps = getForcedPush(piece);
					}
					else
					{
						steps = getNormalSteps(piece);
					}
				}
			}
			else
			{
				//Handle the empty case.
				steps = new Vector<StepData>();
			}
		}
		
		return steps;
	}

	/**
	 * TODO: Describe method
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
					
					//Now add the move, if appropriate.
					if(canPush)
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
		if(gameState.getLastPieceMoved() != null &&
			piece.getValue() < gameState.getLastPieceMoved().getValue())
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
						}
						else if(adjacentPiece.getValue() < piece.getValue())
						{
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
		if(piece.getValue() > gameState.getLastPieceMoved().getValue() &&
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
			BoardData board = gameState.getBoardData();
			HandPosition position = (HandPosition)piece.getPosition();
			BoardPosition oldPosition = (BoardPosition)position.getOldPosition();
			List<BoardPosition> adjacentSpaces = oldPosition.getAdjacentSpaces();
			Iterator<BoardPosition> iterator = adjacentSpaces.iterator();
			while(iterator.hasNext())
			{
				BoardPosition destination = iterator.next();
				if(!board.isOccupied(destination))
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
				steps.add(new StepData(piece, new BoardPosition(col, row1)));
				steps.add(new StepData(piece, new BoardPosition(col, row2)));
			}
			
			//Add the appropriate bucket.
			steps.add(new StepData(piece, new BucketPosition(gameState.getCurrentPlayer())));
		}
		
		return steps;
	}

}

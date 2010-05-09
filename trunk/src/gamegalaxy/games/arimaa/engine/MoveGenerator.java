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
		//If this move is already in our database, just return the cached value
		if(moveDatabase.containsKey(piece))
		{
			return moveDatabase.get(piece);
		}
		else
		{
			List<StepData> steps;
			
			//Create our steps based on the phase
			if(gameState.isSetupPhase())
			{
				//Moves for setting up the board
				steps = generateSetupSteps(piece);
			}
			else if(gameState.isGameOn())
			{
				//Moves during the actual game
				steps = generateGameSteps(piece);
			}
			else
			{
				//If the game has ended, return an empty set of moves
				steps = new Vector<StepData>();
			}
			
			//Cache this info in the database for future reference
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
			//If the previous move displaced an enemy piece, we have to complete the push
			if(gameState.getPieceInHand() != null)
			{
				steps.addAll(getForcedSteps(piece));
			}
			//Ensure this is a real board position (i.e. not null)
			else if(piece.getPosition() instanceof BoardPosition)
			{ 
				//If this piece is NOT yours, add moves you can do to an enemy piece
				//i.e. initiating a push/pull OR completing a pull
				if(piece.getColor() != gameState.getCurrentPlayer())
				{	
					steps.addAll(getOpponentPieceSteps(piece));
				}
				//Otherwise, check to ensure the piece isn't frozen
				else if(!gameState.getBoardData().isPieceFrozen(piece))
				{
					//If the last move moved an opponent piece, we must complete the push
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
	 * This set of three functions validates whether one piece can push/pull another
	 * 
	 * validPush() and validPull() exist solely as entry points
	 * validPushPull() runs the actual validation, as the code is largely the same for both moves
	 * 
	 * INPUTS: The piece that is pushing, piece that is being pushed
	 *		These two pieces MUST be adjacent (this is NOT checked within this code)
	 */
	
	private boolean validPush(PieceData myPiece, PieceData enemyPiece){
		return validPushPull(myPiece,enemyPiece,true);
	}
	
	private boolean validPull(PieceData myPiece, PieceData enemyPiece){
		return validPushPull(myPiece,enemyPiece,false);
	}
	
	private boolean validPushPull(PieceData myPiece, PieceData enemyPiece, boolean isPush){
		BoardData board = gameState.getBoardData();
		
		BoardPosition myPosition = (BoardPosition) myPiece.getPosition();
		BoardPosition enemyPosition = (BoardPosition) enemyPiece.getPosition();
		
		//Ensure we have sufficient moves to push (requires 2)
		if (gameState.getRemainingMoves() < 2){
			return false;
		}
		
		//Ensure that myPiece really belongs to the active player
		//And that the enemyPiece belongs to a different player
		if (myPiece.getColor() != gameState.getCurrentPlayer() ||
				myPiece.getColor() == enemyPiece.getColor())
		{
			return false;
		}
		
		//Ensure that active player's piece isn't frozen
		if (board.isPieceFrozen(myPiece)){
			return false;
		}
		
		//Ensure that the active player's piece has the higher value
		if(myPiece.getValue() < enemyPiece.getValue()){
			return false;
		}

		//Determine which piece we need to check for valid moves
		//If pushing, ensure that the pushed(enemy) piece has a place to move
		//If pulling, ensure that the displaced(my) piece has a place to move
		BoardPosition activePosition;
		
		if (isPush){
			activePosition = enemyPosition;
		}else{
			activePosition = myPosition;			
		}
		
		//Ensure that the active piece has an open space		
		boolean foundOpening = false;
		List<BoardPosition> adjacentSpaces = activePosition.getAdjacentSpaces();
		Iterator<BoardPosition> iterator = adjacentSpaces.iterator();

		//Check each adjacent square
		while(iterator.hasNext())
		{
			BoardPosition adjacentPosition = iterator.next();

			//If the square is occupied
			if(!board.isOccupied(adjacentPosition))
			{
				foundOpening = true;
			}
		}

		return foundOpening;
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
		
		//For each adjacent space
		while(iterator.hasNext())
		{
			BoardPosition adjacentPosition = iterator.next();
			
			//If this square isn't occupied
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
			//If this square IS occupied
			else
			{
				//Handle pushes
				PieceData adjacentPiece = board.getPieceAt(adjacentPosition);
				
					//Verify that this piece could be pushed
					if(validPush(piece,adjacentPiece))
					{
						steps.add(new StepData(piece, adjacentPosition));
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
		//(i.e. if this is not the first move, and the last piece moved had a greater value
		if(gameState.getLastStep() != null &&
			piece.getValue() < gameState.getLastStep().getPiece().getValue())
		{
			//Ensure that there is a PullPosition adjacent to this piece
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
			
			//Check each adjacent square
			while(iterator.hasNext())
			{
				BoardPosition adjacentPosition = iterator.next();
				
				//If the square is occupied
				if(board.isOccupied(adjacentPosition))
				{
					//Check if the adjacent piece can push this one
					PieceData pushingPiece = board.getPieceAt(adjacentPosition);
					if(validPush(pushingPiece,piece)){
						foundPusher = true;
					}
					
					//And check if that piece can pull this one
					if(validPull(pushingPiece,piece)){
						possiblePullers.add(pushingPiece);
					}
				}
				else
				{
					openSpaces.add(adjacentPosition);
				}
			}
			
			//Now add any possible pushes.
			//(i.e. If I can be pushed, what open spaces do I have that I can be pushed IN to)
			if(foundPusher)
			{
				//Add all of the open spaces as possible moves.
				Iterator<BoardPosition> openIterator = openSpaces.iterator();
				while(openIterator.hasNext())
				{
					steps.add(new StepData(piece, openIterator.next()));
				}
			}
			
			//Add moves that place the piece on top of a valid Puller
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
		
		//Verify the piece has a higher value than the pushee, and is owned by the active player
		if(piece.getValue() > gameState.getLastStep().getPiece().getValue() &&
			piece.getColor() == gameState.getCurrentPlayer())
		{
			BoardPosition position = (BoardPosition)piece.getPosition();
			
			//Ensure that the distance between the two is exactly 1 square
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
		
		//Ensure we're really placing one of our own pieces
		//FIXME: Is this validation really necessary?
		if(piece.getColor() == gameState.getCurrentPlayer())
		{
			//Calculate which rows pieces can be placed on
			int row1;
			int row2;
			
			//The gold player uses the last two rows
			if(gameState.getCurrentPlayer() == GameConstants.GOLD)
			{
				row1 = 6;
				row2 = 7;
			}
			
			//The silver player use the first two rows
			else
			{
				row1 = 0;
				row2 = 1;
			}
			
			//Add all of the squares in those two rows
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
	
	/**
	 * This returns all of the rows that the opposing player can place pieces on
	 * 
	 * FIXME: This is just generateSetupSteps run for the enemy's rows instead
	 * 	Should be possible to make this more modular!
	 *
	 * @param piece
	 * @return
	 */
	public List<StepData> isEnemyRow(PieceData piece)
	{
		List<StepData> steps = new Vector<StepData>();
		
		//Ensure we're really placing one of our own pieces
		//FIXME: Is this validation really necessary?
		if(piece.getColor() == gameState.getCurrentPlayer())
		{
			//Calculate which rows pieces can be placed on
			int row1;
			int row2;
			
			//The gold player uses the last two rows
			if(gameState.getCurrentPlayer() != GameConstants.GOLD)
			{
				row1 = 6;
				row2 = 7;
			}
			
			//The silver player use the first two rows
			else
			{
				row1 = 0;
				row2 = 1;
			}
			
			//Add all of the squares in those two rows
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


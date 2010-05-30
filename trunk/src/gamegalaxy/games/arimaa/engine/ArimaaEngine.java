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

import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.GameState;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.StepData;
import gamegalaxy.tools.SimpleObservable;

import java.util.Observer;
import java.util.Vector;

/**
 * This class manages all of the data associated with an Arimaa game. It accepts and validates incoming moves as well as storing all of the data for
 * representing the board.
 */
public class ArimaaEngine
{
	// The current game state.
	private GameState	currentGameState;

	// Archive the game
	private Vector<GameState> archiveGameState;
	
	// Archive the move history
	private Vector<StepData> stepHistory;
	
	// Store a link to the UI
	private SimpleObservable observable;

	/**
	 * Create the game engine.
	 * 
	 */
	public ArimaaEngine()
	{
		observable = new SimpleObservable();
		newGame();
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
		return currentGameState.canPieceBeMoved(piece);
	}

	/**
	 * Checks to see if the selected piece can be legally moved from their original space to the new space selected.
	 * 
	 * FIXME: Is this documentation correct?
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
		return currentGameState.isValidStep(step);
	}
	
	/**
	 * Check to see if the player is trying to setup a piece on the wrong side of the board
	 * 
	 * @param step
	 * 		The location we are trying to set up a piece at 
	 * 
	 */
	public boolean isEnemyRow(StepData step)
	{
		return currentGameState.isEnemyRow(step);
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
		if (!step.getPiece().getPosition().equals(step.getDestination()))
		{

			
			//Archive, unless we are in the setup phase
			//if (!currentGameState.isSetupPhase()){
				stepHistory.add(step.copy());
			//}
			
			//Make the move
			currentGameState.takeStep(step);
			
			// Update the UI with the results.
			observable.notifyObservers();
		}

	}
	
	/**
	 * Reverts the GameState to it's status at the start of the current move
	 * @return true if we successfully reverted, false otherwise
	 */
	private Boolean revertToPreviousGameState(){
		return revertGameState(archiveGameState.size()-1);
	}
	
	/**
	 * Revert to the saved GameState specified by the index value
	 * 
	 * @param index of saved GameState in archiveGameState.get()
	 * @return true if we successfully reverted, false if the index was invalid
	 */
	private Boolean revertGameState(int index){
		//Ensure the index doesn't exceed the last element in the vector
		if (index >= archiveGameState.size()){
			return false;
		}else{
			//Change the active gameState to the archived gameState
			currentGameState = archiveGameState.get(index).copy();
			
			//Remove all future gameStates from the vector
			while (archiveGameState.size() > index+1){
				archiveGameState.remove(index+1);
			}
			
			return true;
		}
	}
	/**
	 * Undoes the most recent step made.
	 * Works by undoing the entire move and then re-doing all but the most recent step
	 * Can be invoked multiple times, until no further steps exist that turn
	 */
	public void undoStep(){					
		//Check that at least one step has been made
		if (stepHistory.size() <= 0){
			return;
		}
		
		//Revert the gamestate and check that it was successful
		if (revertToPreviousGameState()){

			//Archive the old step history
			Vector<StepData> oldHistory = new Vector<StepData>();
			for (int i = 0; i < stepHistory.size() ; i++){
				oldHistory.add(stepHistory.get(i));
			}
			
			//Clear the current step history (since the takeSteps will recreate it)
			stepHistory = new Vector<StepData>();
			
			//Redo each (archived) step
			for (int i = 0; i < oldHistory.size()-1 ; i++){
				takeStep(oldHistory.get(i));
			}
			
			//If we have a piece in hand, go back one more step
			// (This is because otherwise the UX is a bit confusing)
			if(getCurrentGameState().getPieceInHand()  != null)
			{
				undoStep();
			}
			
			//Update the display
			observable.notifyObservers();
		}
	}
	
	/**
	 * Reverts to the gameState as it was at the start of the move
	 */
	public void undoMove()
	{
		//Revert the gamestate
		revertToPreviousGameState();
		
		//Clear the step history
		stepHistory.removeAllElements();
		
		//Update the UI
		observable.notifyObservers();		
	}

	/**
	 * End the current player's turn.
	 * 
	 */
	public void endMove()
	{
		if (currentGameState.canPlayerEndTurn())
		{
			currentGameState.endMove();
			
			archiveGameState.add(currentGameState.copy());
			stepHistory.removeAllElements();
			
			observable.notifyObservers();
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

	/**
	 * Starts a new game.
	 */
	public void newGame()
	{
		currentGameState = new GameState();
		currentGameState.initializeGameState();
		
		archiveGameState = new Vector<GameState>();
		archiveGameState.add(currentGameState.copy());
		
		stepHistory = new Vector<StepData>();
		
		observable.notifyObservers();
	}

}

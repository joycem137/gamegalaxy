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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.GameState;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.StepData;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class TestEngine
{
	private ArimaaEngine	engine;

	/**
	 * TODO: Describe method
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{	
		//Load the game engine
		engine = new ArimaaEngine();
	}

	/**
	 * TODO: Describe method
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Tests whether randomPlacementworks
	 */
	@Test
	public void testRandomPlacementWorks()
	{	
		randomlySetupBoard();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		for(int col = 0; col < 8; col++)
		{
			assertTrue(board.isOccupied(new BoardPosition(col, 0)));
			assertTrue(board.isOccupied(new BoardPosition(col, 1)));
			assertTrue(board.isOccupied(new BoardPosition(col, 6)));
			assertTrue(board.isOccupied(new BoardPosition(col, 7)));
		}
	}
	
	/**
	 * Test that pieces do not swap unless they are placed over another piece
	 */
	@Test
	public void testBasicSetupPhase()
	{
		List<PieceData> bucket = engine.getCurrentGameState().getGoldBucket();
		int numPieces = 16;
		for(int col = 0; col < 7; col++)
		{
			//Drop a piece in the top row.
			StepData step = new StepData(bucket.get(0), new BoardPosition(col, 6));
			assertTrue(engine.isValidStep(step));
			engine.takeStep(step);
			bucket = engine.getCurrentGameState().getGoldBucket();
			numPieces--;
			assertEquals(numPieces, bucket.size());
			
			//Drop a piece in the bottom row.
			step = new StepData(bucket.get(0), new BoardPosition(col, 7));
			assertTrue(engine.isValidStep(step));
			engine.takeStep(step);
			bucket = engine.getCurrentGameState().getGoldBucket();
			numPieces--;
			assertEquals(numPieces, bucket.size());
		}
	}

	/**
	 * Test that pieces do not swap unless they are placed over another piece
	 */
	@Test
	public void testForAccidentalSwaps()
	{
		int col = 0;
		int numPieces = 16;
		
		PieceData horse1 = getPieceFromBucket(GameConstants.GOLD, PieceData.HORSE);
		confirmPlacement1(horse1, col, numPieces);
		numPieces--;
		col++;
		
		PieceData horse2 = getPieceFromBucket(GameConstants.GOLD, PieceData.HORSE);
		confirmPlacement2(horse2, col, numPieces, horse1);
		numPieces--;
		col++;
		
		PieceData dog1 = getPieceFromBucket(GameConstants.GOLD, PieceData.DOG);
		confirmPlacement1(dog1, col, numPieces);
		numPieces--;
		col++;
		
		PieceData dog2 = getPieceFromBucket(GameConstants.GOLD, PieceData.DOG);
		confirmPlacement2(dog2, col, numPieces, dog1);
		numPieces--;
		col++;
		
		PieceData cat1 = getPieceFromBucket(GameConstants.GOLD, PieceData.CAT);
		confirmPlacement1(cat1, col, numPieces);
		numPieces--;
		col++;
		
		PieceData cat2 = getPieceFromBucket(GameConstants.GOLD, PieceData.CAT);
		confirmPlacement2(cat2, col, numPieces, cat1);
		numPieces--;
		col++;
	}

	/**
	 * Test that pieces in the front row can move, and that the pieces in the back
	 * row cannot.
	 */
	@Test
	public void testInitialMovementCapable()
	{
		//Randomly setup the board
		randomlySetupBoard();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Test that the gold player can move.
		for(int col = 0; col < 8; col++)
		{
			//Make sure the front row piece can move.
			PieceData piece = board.getPieceAt(new BoardPosition(col, 6));
			assertTrue(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
			
			//And the back row piece cannot.
			piece = board.getPieceAt(new BoardPosition(col, 7));
			assertFalse(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
		}
		
		//Switch to the silver player.
		PieceData piece = board.getPieceAt(new BoardPosition(3,6));
		BoardPosition newPosition = ((BoardPosition) piece.getPosition()).moveUp();
		engine.takeStep(new StepData(piece, newPosition.moveUp()));
		engine.endMove();
		
		//Now test the movement capabilities of silver.
		for(int col = 0; col < 8; col++)
		{
			//Front row can move
			piece = board.getPieceAt(new BoardPosition(col, 1));
			assertTrue(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
			
			//Back row cannot.
			piece = board.getPieceAt(new BoardPosition(col, 0));
			assertFalse(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
		}	
	}
	
	/**
	 * Test that a normal move resolves correctly.
	 */
	@Test
	public void testNormalMove()
	{
		randomlySetupBoard();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		BoardPosition source = new BoardPosition(3, 6);
		BoardPosition destination = source.moveUp();
		StepData step = new StepData(board.getPieceAt(source), destination);
		
		assertTrue(engine.isValidStep(step));
		
		engine.takeStep(step);
		
		board = engine.getCurrentGameState().getBoardData();
		assertEquals(step.getPiece(), board.getPieceAt(destination));
		assertFalse(board.isOccupied(source));
		assertEquals(1, engine.getCurrentGameState().getNumSteps());
	}
	
	/**
	 * Test that rabbits cannot move backwards.
	 */
	@Test
	public void testRabbitMovement()
	{
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 6);
		
		engine.doRandomSetup();
		engine.endMove();
		
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 1);
		
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up one space.
		PieceData rabbit1 = board.getPieceAt(new BoardPosition(3, 6));
		movePieceUp(rabbit1);
		
		//Confirm that the rabbit can't move backwards.
		assertFalse(engine.isValidStep(new StepData(rabbit1, new BoardPosition(3, 6))));
		
		engine.endMove();
		
		//Now move the silver rabbit down one space.
		PieceData rabbit2 = board.getPieceAt(new BoardPosition(3, 1));
		movePieceDown(rabbit2);
		
		//Confirm that it can't move back up
		assertFalse(engine.isValidStep(new StepData(rabbit2, new BoardPosition(3, 1))));
	}
	
	/**
	 * Test incrementing of numSteps
	 */
	@Test
	public void testNumSteps()
	{
		//Test that num moves increments when making a normal move
		randomlySetupBoard();
		
		BoardPosition startPosition = new BoardPosition(3, 6);
		PieceData piece = engine.getCurrentGameState().getBoardData().getPieceAt(startPosition);
		StepData normalStep = new StepData(piece, startPosition.moveUp());
		
		//Check the numSteps before the move
		assertEquals(0, engine.getCurrentGameState().getNumSteps());
		
		//Check after
		engine.takeStep(normalStep);
		assertEquals(1, engine.getCurrentGameState().getNumSteps());
		
		//And then a stationary move.
		engine.takeStep(new StepData(piece, startPosition.moveUp()));
		assertEquals(1, engine.getCurrentGameState().getNumSteps());
	}

	/**
	 * Test that the traps actually capture pieces when moved into them.
	 */
	@Test
	public void testWalkingIntoTrap()
	{
		randomlySetupBoard();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		BoardPosition source = new BoardPosition(2, 6);
		BoardPosition destination = source.moveUp();
		StepData step = new StepData(board.getPieceAt(source), destination);
		
		assertTrue(engine.isValidStep(step));
		
		engine.takeStep(step);
		
		board = engine.getCurrentGameState().getBoardData();
		assertFalse(board.isOccupied(source));
		assertFalse(board.isOccupied(destination));
		assertEquals(engine.getCurrentGameState().getNumSteps(), 1);
		
		//Verify that the piece is in the bucket.
		List<PieceData> bucket = engine.getCurrentGameState().getSilverBucket();
		assertNotNull(bucket);
		assertEquals(1, bucket.size());
		PieceData piece = bucket.get(0);
		assertEquals(piece, step.getPiece());
	}
	
	/**
	 * Test that the trap works if it isn't the piece in question being moved.
	 */
	@Test
	public void testTrapAbandon()
	{
		randomlySetupBoard();
		
		//Move the piece diagonal from the trap up one
		BoardData board = engine.getCurrentGameState().getBoardData();
		PieceData piece = board.getPieceAt(new BoardPosition(1, 6));
		movePieceUp(piece);
		assertTrue(board.isOccupied(new BoardPosition(1, 5)));
		
		//Move the piece into the trap
		PieceData trapPiece = board.getPieceAt(new BoardPosition(2, 6));
		movePieceUp(trapPiece);
		
		//Assert that the piece has not been captured.
		board = engine.getCurrentGameState().getBoardData();
		assertTrue(board.isOccupied(new BoardPosition(2, 5)));
		
		//Now move the piece next to it away
		piece = board.getPieceAt(new BoardPosition(1, 5));
		movePieceUp(piece);
		
		//Assert that the piece has been captured.
		assertFalse(board.isOccupied(new BoardPosition(2, 5)));
		
		//Verify that the piece is in the bucket.
		List<PieceData> bucket = engine.getCurrentGameState().getSilverBucket();
		assertNotNull(bucket);
		assertEquals(1, bucket.size());
		piece = bucket.get(0);
		assertEquals(trapPiece, piece);
	}
	
	/**
	 * Verify that basic pushing and pulling works correctly.  
	 * This just pushes a single piece back and forth.
	 *
	 */
	@Test
	public void testBasicPushingAndPulling()
	{
		//Place a rabbit in the front row.
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 6);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 3, 1);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		PieceData rabbit = board.getPieceAt(new BoardPosition(3, 6));
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		
		//End the move
		engine.endMove();
		
		//Move the rabbit down, to do the first half of the move.
		movePieceDown(rabbit);
		
		//Assert that we cannot end the move here, since we have a forced push.
		assertFalse(engine.getCurrentGameState().canPlayerEndTurn());
		
		//Move the elephant down.
		PieceData elephant = board.getPieceAt(new BoardPosition(3, 1));
		movePieceDown(elephant);
		
		//Check our state
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(3, 2)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(3, 3)));
		
		//Now move the elephant back up to test pulling.
		movePieceUp(elephant);
		
		//And make the rabbit follow
		movePieceUp(rabbit);
		
		//And check our state.
		gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(3, 1)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(3, 2)));
	}
	
	/**
	 * Verify that basic pushing and pulling works correctly.  
	 * This just pushes a single piece back and forth.
	 *
	 */
	@Test
	public void testPushingAndPullingOverAnother()
	{
		//Place a rabbit in the front row.
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 6);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 3, 1);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		PieceData rabbit = board.getPieceAt(new BoardPosition(3, 6));
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		
		//End the move
		engine.endMove();
		
		//Move the elephant down, to do the first half of the move.
		PieceData elephant = board.getPieceAt(new BoardPosition(3, 1));
		movePieceDown(elephant);
		
		//Assert that we cannot end the move here, since we have a forced push.
		assertFalse(engine.getCurrentGameState().canPlayerEndTurn());
		
		//Assert that the rabbit is in hand.
		assertEquals(rabbit, engine.getCurrentGameState().getPieceInHand());
		
		//Assert that no other pieces can move.
		assertFalse(engine.canPieceBeMoved(board.getPieceAt(new BoardPosition(6, 1))));
		
		//Move the rabbit down.
		StepData pushStep = new StepData(rabbit, new BoardPosition(3, 3));
		assertTrue(engine.canPieceBeMoved(rabbit));
		assertTrue(engine.isValidStep(pushStep));
		engine.takeStep(pushStep);
		
		//Check our state
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(3, 2)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(3, 3)));
		
		//Now move the rabbit back up to test pulling.
		movePieceUp(rabbit);
		
		//And make the elephant follow
		StepData pullStep = new StepData(elephant, new BoardPosition(3, 1));
		assertTrue(engine.canPieceBeMoved(elephant));
		assertTrue(engine.isValidStep(pullStep));
		engine.takeStep(pullStep);
		
		//And check our state.
		gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(3, 1)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(3, 2)));
	}
	
	/**
	 * Test to make sure that if two pieces are available to be pulled by a piece, 
	 * only a piece of lower value can be pulled.
	 *
	 */
	@Test
	public void testComplexPushingAndPulling()
	{
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 6);
		
		//Put an elephant next to it.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 4, 6);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the cat and move it to the front row.
		placePieceTypeOnBoard(PieceData.CAT, 3, 1);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();

		//Move the gold pieces up.
		BoardData board = engine.getCurrentGameState().getBoardData();
		PieceData rabbit = board.getPieceAt(new BoardPosition(3, 6));
		PieceData elephant = board.getPieceAt(new BoardPosition(4, 6));
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(elephant);
		movePieceUp(elephant);
		
		engine.endMove();
		
		//Now move the silver pieces forward.
		PieceData cat = board.getPieceAt(new BoardPosition(3, 1));
		PieceData unfreezingCompanion = board.getPieceAt(new BoardPosition(2, 1));
		movePieceDown(cat);
		movePieceDown(unfreezingCompanion);
		movePieceDown(unfreezingCompanion);
		movePieceDown(cat);
		
		engine.endMove();
		
		//Move the elephant forward one space.
		movePieceUp(elephant);
		
		engine.endMove();
		
		//And now the actual test!
		movePieceUp(cat);
		
		//Check that we cannot pull the elephant.
		assertFalse(engine.canPieceBeMoved(elephant));
		assertTrue(engine.canPieceBeMoved(rabbit));
		
		//Now move the cat back into position to prep for the push test.
		movePieceDown(cat);
		
		engine.endMove();
		
		//Check that we can move the cat and move it.
		assertTrue(engine.canPieceBeMoved(cat));
		movePieceUp(cat);
		
		//Check that we can't end the turn.
		assertFalse(engine.getCurrentGameState().canPlayerEndTurn());
		
		//And now make sure the rabbit can't finish the move.
		assertFalse(engine.canPieceBeMoved(rabbit));
		assertTrue(engine.canPieceBeMoved(elephant));
	}

	/**
	 * Test that pushing works.
	 */
	@Test
	public void testBasicFreezing()
	{
		//Grab a rabbit
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 6);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 3, 1);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 3 spaces.
		BoardPosition source = new BoardPosition(3, 6);
		PieceData rabbit = board.getPieceAt(source);
		
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		
		//End the move
		engine.endMove();
		
		//Move the piece down.
		PieceData elephant = board.getPieceAt(new BoardPosition(3, 1));
		movePieceDown(elephant);
		
		engine.endMove();
		
		//Now verify that the rabbit can't move.
		assertTrue(board.isPieceFrozen(rabbit));
		assertFalse(engine.canPieceBeMoved(rabbit));
	}
	
	/**
	 * Test game winning conditions
	 */
	@Test
	public void testNormalWin()
	{
		//Grab a rabbit
		placePieceTypeOnBoard(PieceData.RABBIT, 3, 6);
		
		randomlySetupBoard();
		
		//Move the rabbit as far as it can go.
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		BoardPosition source = new BoardPosition(3, 6);
		PieceData rabbit = board.getPieceAt(source);
		
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		
		engine.endMove();
		
		//Move some silver pieces out of the way.
		BoardPosition trapPosition = new BoardPosition(2, 2);
		PieceData piece = board.getPieceAt(trapPosition.moveUp());
		engine.takeStep(new StepData(piece, trapPosition));
		
		piece = board.getPieceAt(trapPosition.moveUp().moveRight());
		engine.takeStep(new StepData(piece, trapPosition.moveUp()));
		engine.takeStep(new StepData(piece, trapPosition));
		
		piece = board.getPieceAt(new BoardPosition(3, 0));
		engine.takeStep(new StepData(piece, new BoardPosition(3, 1)));
		
		engine.endMove();
		
		//Move a random gold piece and then move back to getting silver pieces out of the way.
		piece = board.getPieceAt(new BoardPosition(2, 6));
		engine.takeStep(new StepData(piece, new BoardPosition(2, 5)));
		engine.endMove();
		
		//Move a few more silver pieces around.
		
		piece = board.getPieceAt(new BoardPosition(3, 1));
		engine.takeStep(new StepData(piece, new BoardPosition(2, 1)));
		engine.takeStep(new StepData(piece, trapPosition));
		
		piece = board.getPieceAt(new BoardPosition(4, 1));
		engine.takeStep(new StepData(piece, new BoardPosition(4, 2)));
		engine.takeStep(new StepData(piece, new BoardPosition(5, 2)));
		engine.endMove();
		
		//Now we should be clear.  Send in the rabbit!
		engine.takeStep(new StepData(rabbit, new BoardPosition(3, 1)));
		engine.takeStep(new StepData(rabbit, new BoardPosition(3, 0)));
		
		//Assert that the game has been won.
		assertTrue(engine.getCurrentGameState().isGameOver());
		assertEquals(GameConstants.GOLD, engine.getCurrentGameState().getGameWinner());
	}
	
	/**
	 * Test that suicide killing all of the rabbits gives the game to the other player.
	 */
	@Test
	public void testSuicideVictory()
	{
		killAlmostAllOfTheRabbits();
		PieceData rabbit = engine.getCurrentGameState().getBoardData().getPieceAt(new BoardPosition(6, 5));
		movePieceLeft(rabbit); //And that should win the game for silver.
		
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.isGameOver());
		assertEquals(GameConstants.SILVER, gameState.getGameWinner());
	}
	
	/**
	 * Test that killing all of the rabbits gives the game to the current player.
	 */
	@Test
	public void testKillingVictory()
	{	
		killAlmostAllOfTheRabbits();
		BoardData board = engine.getCurrentGameState().getBoardData();
		PieceData rabbit = board.getPieceAt(new BoardPosition(6, 5));
		PieceData elephant = board.getPieceAt(new BoardPosition(6, 3));
		
		//Make it silver's turn again
		engine.endMove();
		movePieceDown(elephant);
		movePieceLeft(rabbit);
		
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.isGameOver());
		assertEquals(GameConstants.SILVER, gameState.getGameWinner());
	}
	
	/**
	 * TODO: Describe method
	 *
	 */
	private void killAlmostAllOfTheRabbits()
	{	
		//Line the front row with rabbits.
		for(int col = 0; col < 8; col++)
		{
			placePieceTypeOnBoard(PieceData.RABBIT, col, 6);
		}
		
		//Do the rest randomly.
		engine.doRandomSetup();
		engine.endMove();
		
		//Place an elephant on the board across from where the last rabbit will be
		placePieceTypeOnBoard(PieceData.ELEPHANT, 6, 1);
		
		//Do the rest randomly.
		engine.doRandomSetup();
		engine.endMove();
		
		//Go kill some rabbits.
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Kill the rabbit next to the trap
		PieceData rabbit = board.getPieceAt(new BoardPosition(2, 6));
		movePieceUp(rabbit);
		
		//Kill the one down on the right.
		rabbit = board.getPieceAt(new BoardPosition(0, 6));
		movePieceUp(rabbit);
		movePieceRight(rabbit);
		movePieceRight(rabbit); //Scratch one rabbit 
		
		//Do a dummy move.
		engine.endMove();
		PieceData elephant = board.getPieceAt(new BoardPosition(6, 1));
		movePieceDown(elephant);
		engine.endMove();
		
		//And back to rabbit killin!
		rabbit = board.getPieceAt(new BoardPosition(1, 6));
		movePieceUp(rabbit);
		movePieceRight(rabbit); //Another kill
		
		rabbit = board.getPieceAt(new BoardPosition(3, 6));
		movePieceUp(rabbit);
		movePieceLeft(rabbit);
		
		//Cycle back to gold again
		engine.endMove();
		movePieceDown(elephant);
		engine.endMove();
		
		//And kill rabbits!
		
		rabbit = board.getPieceAt(new BoardPosition(5, 6));
		movePieceUp(rabbit); //Right into the trap!
		
		rabbit = board.getPieceAt(new BoardPosition(4, 6));
		movePieceUp(rabbit);
		movePieceRight(rabbit); //Dead!
		
		rabbit = board.getPieceAt(new BoardPosition(6, 6));
		movePieceUp(rabbit);
		
		//Cycle
		engine.endMove();
		movePieceRight(elephant);
		engine.endMove();
		
		//Rabbits!
		movePieceLeft(rabbit);
		
		//And the last one
		rabbit = board.getPieceAt(new BoardPosition(7, 6));
		movePieceUp(rabbit);
		movePieceLeft(rabbit);
		
		engine.endMove();
		movePieceLeft(elephant);
		engine.endMove();
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 */
	private void movePieceDown(PieceData piece)
	{
		assertNotNull(piece);
		assertTrue(engine.canPieceBeMoved(piece));
		BoardPosition rabbitPosition = (BoardPosition)piece.getPosition();
		StepData step = new StepData(piece, rabbitPosition.moveDown());
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 */
	private void movePieceUp(PieceData piece)
	{
		assertNotNull(piece);
		assertTrue(engine.canPieceBeMoved(piece));
		BoardPosition rabbitPosition = (BoardPosition)piece.getPosition();
		StepData step = new StepData(piece, rabbitPosition.moveUp());
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 */
	private void movePieceLeft(PieceData piece)
	{
		assertNotNull(piece);
		assertTrue(engine.canPieceBeMoved(piece));
		BoardPosition rabbitPosition = (BoardPosition)piece.getPosition();
		StepData step = new StepData(piece, rabbitPosition.moveLeft());
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
	}
	
	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 */
	private void movePieceRight(PieceData piece)
	{
		assertNotNull(piece);
		assertTrue(engine.canPieceBeMoved(piece));
		BoardPosition rabbitPosition = (BoardPosition)piece.getPosition();
		StepData step = new StepData(piece, rabbitPosition.moveRight());
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
	}

	/**
	 * TODO: Describe method
	 *
	 * @param rabbit
	 * @param i
	 * @param j
	 */
	private void placePieceTypeOnBoard(int value, int i, int j)
	{
		//Test walking straight into one.
		PieceData piece = getPieceFromBucket(engine.getCurrentGameState().getCurrentPlayer(), value);
		
		//Place it in the front row
		StepData step = new StepData(piece, new BoardPosition(i, j));
		engine.takeStep(step);
	}

	/**
	 * TODO: Describe method
	 *
	 */
	private void randomlySetupBoard()
	{
		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
	}

	/**
	 * TODO: Describe method
	 * @param i 
	 *
	 * @param rabbit
	 * @return
	 */
	private PieceData getPieceFromBucket(int color, int value)
	{
		List<PieceData> bucket;
		if(color == GameConstants.GOLD)
		{
			bucket = engine.getCurrentGameState().getGoldBucket();
		}
		else
		{
			bucket = engine.getCurrentGameState().getSilverBucket();
		}
		
		Iterator<PieceData> iterator = bucket.iterator();
		while(iterator.hasNext())
		{
			PieceData piece = iterator.next();
			if(piece.getValue() == value) 
				return piece;
		}
		return null;
	}

	/**
	 * TODO: Describe method
	 *
	 * @param piece
	 * @param col
	 */
	private void confirmPlacement1(PieceData piece, int col, int numPieces)
	{	
		//Drop a piece in the top row.
		StepData step = new StepData(piece, new BoardPosition(col, 6));
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		
		List<PieceData> bucket = engine.getCurrentGameState().getGoldBucket();
		assertEquals(numPieces - 1, bucket.size());
		assertEquals(new BoardPosition(col, 6), piece.getPosition());
		BoardData board = engine.getCurrentGameState().getBoardData();
		assertEquals(piece, board.getPieceAt(new BoardPosition(col, 6)));
	}

	/**
	 * TODO: Describe method
	 *
	 * @param horse2
	 * @param col
	 * @param numPieces
	 * @param horse1
	 */
	private void confirmPlacement2(PieceData horse2, int col, int numPieces, PieceData horse1)
	{	
		StepData step = new StepData(horse2, new BoardPosition(col, 6));
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		List<PieceData> bucket = engine.getCurrentGameState().getGoldBucket();
		assertEquals(numPieces - 1, bucket.size());
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		assertEquals(horse2, board.getPieceAt(new BoardPosition(col, 6)));
		assertEquals(horse1, board.getPieceAt(new BoardPosition(col - 1, 6)));
		assertEquals(new BoardPosition(col, 6), horse2.getPosition());
		assertEquals(new BoardPosition(col - 1, 6), horse1.getPosition());
	}
}

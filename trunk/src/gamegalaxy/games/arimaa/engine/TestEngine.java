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
import gamegalaxy.games.arimaa.data.PiecePosition;
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
			assertTrue(board.isOccupied(new BoardPosition(0, col)));
			assertTrue(board.isOccupied(new BoardPosition(1, col)));
			assertTrue(board.isOccupied(new BoardPosition(6, col)));
			assertTrue(board.isOccupied(new BoardPosition(7, col)));
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
			StepData step = new StepData(bucket.get(0), new BoardPosition(6, col));
			assertTrue(engine.isValidStep(step));
			engine.takeStep(step);
			bucket = engine.getCurrentGameState().getGoldBucket();
			numPieces--;
			assertEquals(numPieces, bucket.size());
			
			//Drop a piece in the bottom row.
			step = new StepData(bucket.get(0), new BoardPosition(7, col));
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
			PieceData piece = board.getPieceAt(new BoardPosition(6, col));
			assertTrue(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
			
			//And the back row piece cannot.
			piece = board.getPieceAt(new BoardPosition(7, col));
			assertFalse(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
		}
		
		//Switch to the silver player.
		PieceData piece = board.getPieceAt(new BoardPosition(6, 3));
		movePieceUp(piece);
		engine.endMove();
		
		//Now test the movement capabilities of silver.
		for(int col = 0; col < 8; col++)
		{
			//Front row can move
			piece = board.getPieceAt(new BoardPosition(1, col));
			assertTrue(engine.canPieceBeMoved(piece));
			assertFalse(board.isPieceFrozen(piece));
			
			//Back row cannot.
			piece = board.getPieceAt(new BoardPosition(0, col));
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
		BoardPosition source = new BoardPosition(6, 3);
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
		placePieceTypeOnBoard(PieceData.RABBIT, 6, 3);
		
		engine.doRandomSetup();
		engine.endMove();
		
		placePieceTypeOnBoard(PieceData.RABBIT, 1, 3);
		
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up one space.
		PieceData rabbit1 = board.getPieceAt(new BoardPosition(6, 3));
		movePieceUp(rabbit1);
		
		//Confirm that the rabbit can't move backwards.
		assertFalse(engine.isValidStep(new StepData(rabbit1, new BoardPosition(6, 3))));
		
		engine.endMove();
		
		//Now move the silver rabbit down one space.
		PieceData rabbit2 = board.getPieceAt(new BoardPosition(1, 3));
		movePieceDown(rabbit2);
		
		//Confirm that it can't move back up
		assertFalse(engine.isValidStep(new StepData(rabbit2, new BoardPosition(1, 3))));
	}
	
	/**
	 * Test to ensure two adjacent pieces can't be captured on the same trap square
	 *  in a single push action.
	 */
	@Test
	public void testPushDoubleCapture()
	{
		//place an opposing Gold Elephant and Silver Rabbit on column 1.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 6, 1);		
		engine.doRandomSetup();
		engine.endMove();
		
		placePieceTypeOnBoard(PieceData.RABBIT, 1, 1);
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//move the Gold Elephant up three spaces and end the gold turn.
		PieceData gElephant = board.getPieceAt(new BoardPosition(6, 1));
		for (int x=1; x<=3; x++)
		{
			movePieceUp(gElephant);
		}
		
		engine.endMove();
		
		//move the Silver Rabbit down one square (next to the trap) and its
		//neighbor down one square (onto the trap) and end the silver turn.
		PieceData sRabbit = board.getPieceAt(new BoardPosition(1, 1));
		movePieceDown(sRabbit);
		PieceData sNeighbor = board.getPieceAt(new BoardPosition(1, 2));
		movePieceDown(sNeighbor);
		
		engine.endMove();
		
		//the piece on the trap should still be safe.
		List<PieceData> bucket = engine.getCurrentGameState().getGoldBucket();
		assertEquals(0, bucket.size());
		
		//move the Gold Elephant up, which is a push move that should result in
		//the Silver Rabbit being placed in hand.  The neighbor should not be
		//captured until the push action is complete.
		movePieceUp(gElephant);
		
		assertFalse(engine.getCurrentGameState().canPlayerEndTurn());
		assertEquals(sRabbit, engine.getCurrentGameState().getPieceInHand());
		assertEquals(0, bucket.size());
		
		//since the trap has not yet been emptied, confirm that the Silver Rabbit
		//cannot be pushed onto the trap square.
		StepData pushStep = new StepData(sRabbit, new BoardPosition(2, 2));
		assertFalse(engine.isValidStep(pushStep));
		
		//now complete the push action by pushing the Silver Rabbit up, which should
		//finally capture the piece.
		pushStep = new StepData(sRabbit, new BoardPosition(1, 1));
		assertTrue(engine.isValidStep(pushStep));
		engine.takeStep(pushStep);
		
		assertEquals(1, bucket.size());
		PieceData piece = bucket.get(0);
		assertEquals(sNeighbor, piece);
	}
	
	/**
	 * Test incrementing of numSteps
	 */
	@Test
	public void testNumSteps()
	{
		//Test that num moves increments when making a normal move
		randomlySetupBoard();
		
		BoardPosition startPosition = new BoardPosition(6, 3);
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
		BoardPosition source = new BoardPosition(6, 2);
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
		PieceData piece = board.getPieceAt(new BoardPosition(6, 1));
		movePieceUp(piece);
		assertTrue(board.isOccupied(new BoardPosition(5, 1)));
		
		//Move the piece into the trap
		PieceData trapPiece = board.getPieceAt(new BoardPosition(6, 2));
		movePieceUp(trapPiece);
		
		//Assert that the piece has not been captured.
		board = engine.getCurrentGameState().getBoardData();
		assertTrue(board.isOccupied(new BoardPosition(5, 2)));
		
		//Now move the piece next to it away
		piece = board.getPieceAt(new BoardPosition(5, 1));
		movePieceUp(piece);
		
		//Assert that the piece has been captured.
		assertFalse(board.isOccupied(new BoardPosition(5, 2)));
		
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
		placePieceTypeOnBoard(PieceData.RABBIT, 6, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 1, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		PieceData rabbit = board.getPieceAt(new BoardPosition(6, 3));
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
		PieceData elephant = board.getPieceAt(new BoardPosition(1, 3));
		movePieceDown(elephant);
		
		//Check our state
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(2, 3)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(3, 3)));
		
		//Now move the elephant back up to test pulling.
		movePieceUp(elephant);
		
		//And make the rabbit follow
		movePieceUp(rabbit);
		
		//And check our state.
		gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(1, 3)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(2, 3)));
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
		placePieceTypeOnBoard(PieceData.RABBIT, 6, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 1, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		PieceData rabbit = board.getPieceAt(new BoardPosition(6, 3));
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		
		//End the move
		engine.endMove();
		
		//Move the elephant down, to do the first half of the move.
		PieceData elephant = board.getPieceAt(new BoardPosition(1, 3));
		movePieceDown(elephant);
		
		//Assert that we cannot end the move here, since we have a forced push.
		assertFalse(engine.getCurrentGameState().canPlayerEndTurn());
		
		//Assert that the rabbit is in hand.
		assertEquals(rabbit, engine.getCurrentGameState().getPieceInHand());
		
		//Assert that no other pieces can move.
		assertFalse(engine.canPieceBeMoved(board.getPieceAt(new BoardPosition(1, 6))));
		
		//Assert that the rabbit cannot move up to avoid invalid pushes.
		assertFalse(engine.isValidStep(new StepData(rabbit, new BoardPosition(1, 3))));
		
		//Move the rabbit down.
		StepData pushStep = new StepData(rabbit, new BoardPosition(3, 3));
		assertTrue(engine.canPieceBeMoved(rabbit));
		assertTrue(engine.isValidStep(pushStep));
		engine.takeStep(pushStep);
		
		//Check our state
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(2, 3)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(3, 3)));
		
		//Now move the rabbit back up to test pulling.
		movePieceUp(rabbit);
		
		//Assert that the elephant cannot move back into the rabbit's location
		assertFalse(engine.isValidStep(new StepData(elephant, new BoardPosition(3, 3))));
		
		//And make the elephant follow
		StepData pullStep = new StepData(elephant, new BoardPosition(1, 3));
		assertTrue(engine.canPieceBeMoved(elephant));
		assertTrue(engine.isValidStep(pullStep));
		engine.takeStep(pullStep);
		
		//And check our state.
		gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(new BoardPosition(1, 3)));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(new BoardPosition(2, 3)));
	}
	
	/**
	 * Test to make sure that if two pieces are available to be pulled by a piece, 
	 * only a piece of lower value can be pulled.
	 *
	 */
	@Test
	public void testComplexPushingAndPulling()
	{
		placePieceTypeOnBoard(PieceData.RABBIT, 6, 3);
		
		//Put an elephant next to it.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 6, 4);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the cat and move it to the front row.
		placePieceTypeOnBoard(PieceData.CAT, 1, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();

		//Move the gold pieces up.
		BoardData board = engine.getCurrentGameState().getBoardData();
		PieceData rabbit = board.getPieceAt(new BoardPosition(6, 3));
		PieceData elephant = board.getPieceAt(new BoardPosition(6, 4));
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(elephant);
		movePieceUp(elephant);
		
		engine.endMove();
		
		//Now move the silver pieces forward.
		PieceData cat = board.getPieceAt(new BoardPosition(1, 3));
		PieceData unfreezingCompanion = board.getPieceAt(new BoardPosition(1, 2));
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
		placePieceTypeOnBoard(PieceData.RABBIT, 6, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		placePieceTypeOnBoard(PieceData.ELEPHANT, 1, 3);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 3 spaces.
		BoardPosition source = new BoardPosition(6, 3);
		PieceData rabbit = board.getPieceAt(source);
		
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		movePieceUp(rabbit);
		
		//End the move
		engine.endMove();
		
		//Move the piece down.
		PieceData elephant = board.getPieceAt(new BoardPosition(1, 3));
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
		placePieceTypeOnBoard(PieceData.RABBIT, 6, 3);
		
		randomlySetupBoard();
		
		//Move the rabbit as far as it can go.
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		BoardPosition source = new BoardPosition(6, 3);
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
		
		piece = board.getPieceAt(new BoardPosition(0, 3));
		engine.takeStep(new StepData(piece, new BoardPosition(1, 3)));
		
		engine.endMove();
		
		//Move a random gold piece and then move back to getting silver pieces out of the way.
		piece = board.getPieceAt(new BoardPosition(6, 2));
		engine.takeStep(new StepData(piece, new BoardPosition(5, 2)));
		engine.endMove();
		
		//Move a few more silver pieces around.
		
		piece = board.getPieceAt(new BoardPosition(1, 3));
		engine.takeStep(new StepData(piece, new BoardPosition(1, 2)));
		engine.takeStep(new StepData(piece, trapPosition));
		
		piece = board.getPieceAt(new BoardPosition(1, 4));
		engine.takeStep(new StepData(piece, new BoardPosition(2, 4)));
		engine.takeStep(new StepData(piece, new BoardPosition(2, 5)));
		engine.endMove();
		
		//Now we should be clear.  Send in the rabbit!
		engine.takeStep(new StepData(rabbit, new BoardPosition(1, 3)));
		engine.takeStep(new StepData(rabbit, new BoardPosition(0, 3)));
		
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
		PieceData rabbit = engine.getCurrentGameState().getBoardData().getPieceAt(new BoardPosition(5, 6));
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
		PieceData rabbit = board.getPieceAt(new BoardPosition(5, 6));
		PieceData elephant = board.getPieceAt(new BoardPosition(3, 6));
		
		//Make it silver's turn again
		PieceData dummy = board.getPieceAt(new BoardPosition(7, 0));
		movePieceUp(dummy);
		engine.endMove();
		
		//let the Silver Elephant make the final capture.
		movePieceDown(elephant);
		movePieceLeft(rabbit);
		movePieceDown(elephant);
		
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.isGameOver());
		assertEquals(GameConstants.SILVER, gameState.getGameWinner());
	}
	
	/**
	 * Test overridden equals methods, and confirm that hashing methods create
	 *  identical hashes for equal objects.
	 */
	@Test
	public void testEqualsAndHashing()
	{
		placePieceTypeOnBoard(PieceData.ELEPHANT, 6, 0);
		randomlySetupBoard();
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		BoardPosition boardPosition1 = new BoardPosition(6, 2);
		PieceData goldDie1 = board.getPieceAt(boardPosition1);
		PiecePosition goldDiePosition1 = goldDie1.getPosition();
		
		//boardPosition1 should be same as goldDiePosition1.
		assertTrue(goldDiePosition1.equals(boardPosition1));
		assertTrue(boardPosition1.equals(goldDiePosition1));
		assertEquals(goldDiePosition1.hashCode(), boardPosition1.hashCode());
		
		movePieceUp(goldDie1);
		goldDiePosition1 = goldDie1.getPosition();
		
		BoardPosition boardPosition2 = new BoardPosition(6, 5);	
		PieceData goldDie2 = board.getPieceAt(boardPosition2);
		PiecePosition goldDiePosition2 = goldDie2.getPosition();
		
		//boardPosition2 should be same as goldDiePosition2.
		assertTrue(goldDiePosition2.equals(boardPosition2));
		assertTrue(boardPosition2.equals(goldDiePosition2));
		assertEquals(goldDiePosition2.hashCode(), boardPosition2.hashCode());
		
		//goldDiePosition1 is the bucket, goldDiePosition2 is the board.
		assertFalse(goldDiePosition1.equals(goldDiePosition2));
		
		movePieceUp(goldDie2);
		goldDiePosition2 = goldDie2.getPosition();
		
		//goldDie1 and goldDie2 should now be in the bucket.
		assertTrue(goldDiePosition1.equals(goldDiePosition2));
		assertEquals(goldDiePosition1.hashCode(), goldDiePosition2.hashCode());
		
		engine.endMove();
		
		PieceData silverDie1 = board.getPieceAt(new BoardPosition(1, 2));
		movePieceDown(silverDie1);
		PiecePosition silverDiePosition1 = silverDie1.getPosition();
		
		//goldDie1 and silverDie1 are in different buckets.
		assertFalse(goldDiePosition1.equals(silverDiePosition1));
		assertFalse(goldDiePosition1.hashCode() == silverDiePosition1.hashCode());
		
		engine.endMove();
		
		PieceData goldLive1 = board.getPieceAt(new BoardPosition(6, 0));
		PieceData goldLive2 = board.getPieceAt(new BoardPosition(6, 1));
		
		//remember stepToCheck1.  we will test equality/hashing later.
		StepData stepToCheck1 = new StepData(goldLive1, ((BoardPosition)goldLive1.getPosition()).moveUp());
		engine.takeStep(stepToCheck1);
		
		//move another piece so that we can return goldLive1 to its original position.
		movePieceUp(goldLive2);
		movePieceDown(goldLive1);
		
		assertTrue(goldLive1.getPosition().equals(new BoardPosition(6, 0)));
		engine.endMove();
		
		//make a quick move to return to gold's turn.
		movePieceDown(board.getPieceAt(new BoardPosition(1, 0)));
		engine.endMove();

		StepData stepToCheck2 = new StepData(goldLive1, new BoardPosition(5, 0));
		engine.takeStep(stepToCheck2);
		
		//check that stepToCheck1 is equal to stepToCheck2.
		assertTrue(stepToCheck1.equals(stepToCheck2));
		assertEquals(stepToCheck1.hashCode(), stepToCheck2.hashCode());
	}
	
	/**
	 * Creates a "near-death" situation for Gold player by suiciding all but
	 * one rabbit.  At the end of this method it is Gold's turn, with the
	 * last gold rabbit at (5,6) and a Silver elephant at (3,6).
	 */
	private void killAlmostAllOfTheRabbits()
	{	
		//Line the front row with rabbits.
		for(int col = 0; col < 8; col++)
		{
			placePieceTypeOnBoard(PieceData.RABBIT, 6, col);
		}
		
		//Do the rest randomly.
		engine.doRandomSetup();
		engine.endMove();
		
		//Place an elephant on the board across from where the last rabbit will be
		placePieceTypeOnBoard(PieceData.ELEPHANT, 1, 6);
		
		//Do the rest randomly.
		engine.doRandomSetup();
		engine.endMove();
		
		//Go kill some rabbits.
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Kill the rabbit next to the trap
		PieceData rabbit = board.getPieceAt(new BoardPosition(6, 2));
		movePieceUp(rabbit);
		
		//Kill the one down on the right.
		rabbit = board.getPieceAt(new BoardPosition(6, 0));
		movePieceUp(rabbit);
		movePieceRight(rabbit);
		movePieceRight(rabbit); //Scratch one rabbit 
		
		//Do a dummy move.
		engine.endMove();
		PieceData elephant = board.getPieceAt(new BoardPosition(1, 6));
		movePieceDown(elephant);
		engine.endMove();
		
		//And back to rabbit killin!
		rabbit = board.getPieceAt(new BoardPosition(6, 1));
		movePieceUp(rabbit);
		movePieceRight(rabbit); //Another kill
		
		rabbit = board.getPieceAt(new BoardPosition(6, 3));
		movePieceUp(rabbit);
		movePieceLeft(rabbit);
		
		//Cycle back to gold again
		engine.endMove();
		movePieceDown(elephant);
		engine.endMove();
		
		//And kill rabbits!
		
		rabbit = board.getPieceAt(new BoardPosition(6, 5));
		movePieceUp(rabbit); //Right into the trap!
		
		rabbit = board.getPieceAt(new BoardPosition(6, 4));
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
		rabbit = board.getPieceAt(new BoardPosition(6, 7));
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
	private void placePieceTypeOnBoard(int value, int row, int col)
	{
		//Test walking straight into one.
		PieceData piece = getPieceFromBucket(engine.getCurrentGameState().getCurrentPlayer(), value);
		
		//Place it in the front row
		StepData step = new StepData(piece, new BoardPosition(row, col));
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
		StepData step = new StepData(piece, new BoardPosition(6, col));
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		
		List<PieceData> bucket = engine.getCurrentGameState().getGoldBucket();
		assertEquals(numPieces - 1, bucket.size());
		assertEquals(new BoardPosition(6, col), piece.getPosition());
		BoardData board = engine.getCurrentGameState().getBoardData();
		assertEquals(piece, board.getPieceAt(new BoardPosition(6, col)));
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
		StepData step = new StepData(horse2, new BoardPosition(6, col));
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		List<PieceData> bucket = engine.getCurrentGameState().getGoldBucket();
		assertEquals(numPieces - 1, bucket.size());
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		assertEquals(horse2, board.getPieceAt(new BoardPosition(6, col)));
		assertEquals(horse1, board.getPieceAt(new BoardPosition(6, col - 1)));
		assertEquals(new BoardPosition(6, col), horse2.getPosition());
		assertEquals(new BoardPosition(6, col - 1), horse1.getPosition());
	}
}

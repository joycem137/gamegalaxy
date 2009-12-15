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
		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
		
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
	 * Test that pieces in the front row can move.
	 */
	@Test
	public void testFrontRowMovementCapable()
	{
		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Test that the gold player can move.
		for(int col = 0; col < 8; col++)
		{
			PieceData piece = board.getPieceAt(new BoardPosition(col, 6));
			assertTrue(engine.canPieceBeMoved(piece));
		}
		
		//Test that the silver player can move.
		PieceData piece = board.getPieceAt(new BoardPosition(3,6));
		BoardPosition newPosition = ((BoardPosition) piece.getPosition()).moveUp();
		engine.takeStep(new StepData(piece, newPosition.moveUp()));
		engine.endMove();
		for(int col = 0; col < 8; col++)
		{
			piece = board.getPieceAt(new BoardPosition(col, 1));
			assertTrue(engine.canPieceBeMoved(piece));
		}	
	}
	
	/**
	 * Test that a normal move resolves correct.
	 */
	@Test
	public void testNormalMove()
	{
		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
		
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
	 * Test that the traps actually capture pieces when moved into them.
	 */
	@Test
	public void testWalkingIntoTrap()
	{
		//Test walking straight into one.

		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
		
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
	 * Test that pieces become captured when a piece moved away from them.
	 */
	@Test
	public void testTrapAbandon()
	{
		//Do a random setup
		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
		
		//Move the piece diagonal from the trap up one
		BoardData board = engine.getCurrentGameState().getBoardData();
		PieceData piece = board.getPieceAt(new BoardPosition(1, 6));
		StepData step = new StepData(piece, ((BoardPosition)piece.getPosition()).moveUp());
		engine.takeStep(step);
		assertTrue(board.isOccupied(new BoardPosition(1, 5)));
		
		//Move the piece into the trap
		board = engine.getCurrentGameState().getBoardData();
		piece = board.getPieceAt(new BoardPosition(2, 6));
		StepData trapStep = new StepData(piece, ((BoardPosition)piece.getPosition()).moveUp());
		engine.takeStep(trapStep);
		
		//Assert that the piece has not been captured.
		board = engine.getCurrentGameState().getBoardData();
		assertTrue(board.isOccupied(new BoardPosition(2, 5)));
		
		//Now move the piece next to it away
		board = engine.getCurrentGameState().getBoardData();
		piece = board.getPieceAt(new BoardPosition(1, 5));
		step = new StepData(piece, ((BoardPosition)piece.getPosition()).moveUp());
		engine.takeStep(step);
		
		//Assert that the piece has been captured.
		assertFalse(board.isOccupied(new BoardPosition(2, 5)));
		
		//Verify that the piece is in the bucket.
		List<PieceData> bucket = engine.getCurrentGameState().getSilverBucket();
		assertNotNull(bucket);
		assertEquals(1, bucket.size());
		piece = bucket.get(0);
		assertEquals(trapStep.getPiece(), piece);
	}
	
	/**
	 * Test pieces for freezing.
	 *
	 */
	@Test
	public void testBasicPushingAndPulling()
	{
		//Test walking straight into one.
		PieceData rabbit = getPieceFromBucket(GameConstants.GOLD, PieceData.RABBIT);
		
		//Place it in the front row
		StepData step = new StepData(rabbit, new BoardPosition(3, 6));
		engine.takeStep(step);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		PieceData elephant = getPieceFromBucket(GameConstants.SILVER, PieceData.ELEPHANT);
		
		//Place it in the front row
		step = new StepData(elephant, new BoardPosition(3, 1));
		engine.takeStep(step);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		BoardPosition source = new BoardPosition(3, 6);
		BoardPosition destination = source.moveUp();
		rabbit = board.getPieceAt(source);
		
		engine.takeStep(new StepData(rabbit, destination));
		engine.takeStep(new StepData(rabbit, destination.moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp().moveUp()));
		
		//End the move
		engine.endMove();
		
		//Move the rabbit down
		assertTrue(engine.canPieceBeMoved(rabbit));
		BoardPosition rabbitPosition = (BoardPosition)rabbit.getPosition();
		step = new StepData(rabbit, rabbitPosition.moveDown());
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		
		//Assert that we cannot end the move here, since we have a forced push.
		assertFalse(engine.getCurrentGameState().canPlayerEndTurn());
		
		//Move the elephant down.
		step = new StepData(elephant, rabbitPosition);
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		
		//Check our state
		GameState gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(rabbitPosition));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(rabbitPosition.moveDown()));
		
		//Now move the elephant back up
		step = new StepData(elephant, rabbitPosition.moveUp());
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		
		//And make the rabbit follow
		assertTrue(engine.canPieceBeMoved(rabbit));
		step = new StepData(rabbit, rabbitPosition);
		assertTrue(engine.isValidStep(step));
		engine.takeStep(step);
		
		//And check our state.
		gameState = engine.getCurrentGameState();
		assertTrue(gameState.canPlayerEndTurn());
		assertEquals(elephant, gameState.getBoardData().getPieceAt(rabbitPosition.moveUp()));
		assertEquals(rabbit, gameState.getBoardData().getPieceAt(rabbitPosition));
	}

	/**
	 * Test that pushing works.
	 */
	@Test
	public void testBasicFreezing()
	{
		//Grab a rabbit
		PieceData rabbit = getPieceFromBucket(GameConstants.GOLD, PieceData.RABBIT);
		
		//Place it in the front row
		StepData step = new StepData(rabbit, new BoardPosition(3, 6));
		engine.takeStep(step);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		PieceData elephant = getPieceFromBucket(GameConstants.SILVER, PieceData.ELEPHANT);
		
		//Place it in the front row
		step = new StepData(elephant, new BoardPosition(3, 1));
		engine.takeStep(step);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 3 spaces.
		BoardPosition source = new BoardPosition(3, 6);
		BoardPosition destination = source.moveUp();
		rabbit = board.getPieceAt(source);
		
		engine.takeStep(new StepData(rabbit, destination));
		engine.takeStep(new StepData(rabbit, destination.moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp()));
		
		//End the move
		engine.endMove();
		
		//Move the piece down.
		elephant = board.getPieceAt((BoardPosition)elephant.getPosition());
		destination = ((BoardPosition)elephant.getPosition()).moveDown();
		engine.takeStep(new StepData(elephant, destination));
		
		engine.endMove();
	}
	
	/**
	 * Test game winning conditions
	 */
	@Test
	public void testNormalWin()
	{
		//Grab a rabbit
		PieceData rabbit = getPieceFromBucket(GameConstants.GOLD, PieceData.RABBIT);
		
		//Place it in the front row
		StepData step = new StepData(rabbit, new BoardPosition(3, 6));
		engine.takeStep(step);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Move the rabbit as far as it can go.
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Move the rabbit up 4 spaces.
		BoardPosition source = new BoardPosition(3, 6);
		BoardPosition destination = source.moveUp();
		rabbit = board.getPieceAt(source);
		
		engine.takeStep(new StepData(rabbit, destination));
		engine.takeStep(new StepData(rabbit, destination.moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp().moveUp()));
		
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
	 * Test incrementing of numSteps
	 */
	@Test
	public void testNumSteps()
	{
		//Test that num moves increments when making a normal move
		//Do a random setup
		engine.doRandomSetup();
		engine.endMove();
		
		engine.doRandomSetup();
		engine.endMove();
		
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
}

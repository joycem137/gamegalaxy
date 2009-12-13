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
import static org.junit.Assert.fail;
import gamegalaxy.games.arimaa.data.BoardData;
import gamegalaxy.games.arimaa.data.BoardPosition;
import gamegalaxy.games.arimaa.data.GameConstants;
import gamegalaxy.games.arimaa.data.GameState;
import gamegalaxy.games.arimaa.data.PieceData;
import gamegalaxy.games.arimaa.data.StepData;
import gamegalaxy.games.arimaa.gui.ArimaaUI;
import gamegalaxy.gui.ApplicationFrame;
import gamegalaxy.tools.ResourceLoader;

import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class TestEngine
{

	private ArimaaEngine	engine;
	private ArimaaUI	gui;
	private JFrame frame;

	/**
	 * TODO: Describe method
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		//Load all graphical resources.
		ResourceLoader loader = new ResourceLoader();
		loader.loadResources("arimaa");
		
		//Load the game engine
		engine = new ArimaaEngine();
		
		//Load the GUI
		gui = new ArimaaUI(engine, loader);
		
		//Add the GUI to the application frame.
		frame = new ApplicationFrame(gui);
	}

	/**
	 * TODO: Describe method
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		frame.dispose();
	}

	/**
	 * Tests whether randomPlacementworks
	 */
	@Test
	public void testRandomPlacementWorks()
	{
		GameState gameState = new GameState();
		gameState.initializeGameState();
		
		gameState.doRandomSetup();
		gameState.endMove();
		
		gameState.doRandomSetup();
		gameState.endMove();
		
		BoardData board = gameState.getBoardData();
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
		assertEquals(1, engine.getCurrentGameState().getNumMoves());
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
		assertEquals(engine.getCurrentGameState().getNumMoves(), 1);
		
		//Verify that the piece is in the bucket.
		List<PieceData> bucket = engine.getCurrentGameState().getSilverBucket();
		assertNotNull(bucket);
		assertEquals(1, bucket.size());
		PieceData piece = bucket.get(0);
		assertEquals(piece, step.getPiece());
	}
	
	/**
	 * Test that the traps actually capture pieces when moved into them.
	 */
	@Test
	public void testTrapAbandon()
	{
		//Test walking straight into one.
		PieceData rabbit = getPieceFromBucket(GameConstants.GOLD, PieceData.RABBIT);
		
		//Place it in the front row
		engine.getCurrentGameState().getBoardData().placePiece(rabbit, new BoardPosition(3, 6));
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		//Grab the elephant and move it to the front row.
		PieceData elephant = getPieceFromBucket(GameConstants.GOLD, PieceData.ELEPHANT);
		
		//Do the rest of the setup randomly
		engine.doRandomSetup();
		engine.endMove();
		
		BoardData board = engine.getCurrentGameState().getBoardData();
		
		//Gold the rabbit up 4 spaces.
		BoardPosition source = new BoardPosition(3, 6);
		BoardPosition destination = source.moveUp();
		rabbit = board.getPieceAt(source);
		
		engine.takeStep(new StepData(rabbit, destination));
		engine.takeStep(new StepData(rabbit, destination.moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp()));
		engine.takeStep(new StepData(rabbit, destination.moveUp().moveUp().moveUp()));
		
		board = engine.getCurrentGameState().getBoardData();
		assertFalse(board.isOccupied(source));
		assertFalse(board.isOccupied(destination));
		assertEquals(engine.getCurrentGameState().getNumMoves(), 1);
		
		//Verify that the piece is in the bucket.
		List<PieceData> bucket = engine.getCurrentGameState().getSilverBucket();
		assertNotNull(bucket);
		assertEquals(1, bucket.size());
		PieceData piece = bucket.get(0);
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
	 * Test that pushing works.
	 */
	@Test
	public void testPushing()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test pulling
	 */
	@Test
	public void testPulling()
	{
		fail("Not yet implemented");
	}
	
	/**
	 * Test that freezing works in a variety fo situations.
	 */
	@Test
	public void testFreezing()
	{
		fail("Not yet implemented");
	}
	
	/**
	 * Test game winning conditions
	 */
	@Test
	public void testWinning()
	{
		fail("Not yet implemented");
	}
	
	/**
	 * Test undo move
	 */
	@Test
	public void testUndoMove()
	{
		//Test undoing a basic move
		
		//Test undoing a capture
		
		//Test undoing a push
		
		//Test undoing a pull.
		fail("Not yet implemented");
	}
	
	/**
	 * Test incrementing of numSteps
	 */
	@Test
	public void testNumSteps()
	{
		//Test that num moves increments when making a normal move
		
		//Test that num moves does not increment when staying stationary
		fail("Not yet implemented");
	}
}

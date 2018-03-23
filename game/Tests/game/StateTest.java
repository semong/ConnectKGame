package game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StateTest {
	
	private State classUnderTest;
	private State classUnderTest2;
	private GameMove gameMove;
	private connectkParameters parameters;

	@Before
	public void setUp() throws Exception {
		int rows = 6;
		int columns = 7;
		int length = 4;
		boolean gravity = true;
		long time = 1000;
		parameters = new connectkParameters(rows,columns,length,gravity,time);
		classUnderTest = new State(parameters);
		classUnderTest.board[3][5] = 1 ;
		
		classUnderTest2 = new State(parameters);
        classUnderTest2.board[3][5] = 2 ;
        
		classUnderTest.winner = 1;
		
		gameMove = GameMove.getInstance(3,5);
	}

	@Test
	public void testGetconnectkParameters() {
		assertEquals(6, classUnderTest.getconnectkParameters().getRows());
	}

	@Test
	public void testGetCurrentPlayer() {
		assertEquals(1, classUnderTest.getCurrentPlayer());
	}

	@Test
	//To test- set State.board to public
	public void testGetPlayerAt() {
		assertEquals(1, classUnderTest.getPlayerAt(gameMove));
	}

	@Test
	public void testIsOnBoard() {
		assertEquals(true, classUnderTest.isOnBoard(gameMove));
	}

	@Test
	public void testIsAvailable() {
		assertEquals(false, classUnderTest.isAvailable(gameMove));
	}

	@Test
	public void testGetWinner() {
		assertEquals(1, classUnderTest.getWinner());
	}

	@Test
	public void testIsDone() {
		assertEquals(true, classUnderTest.isDone());
	}

	@Test
	public void testEqualsObject() {
	  assertEquals(false, classUnderTest.equals(classUnderTest2));
	}

}

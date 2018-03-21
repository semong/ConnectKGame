package game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class connectkParametersTest {

	private connectkParameters classUnderTest;
	
	@Before
	public void setUp() throws Exception {
		int rows = 4;
		int columns = 5;
		int winLength = 4;
		boolean gravity = true;
		long timeLimit = 2000;
		classUnderTest = new connectkParameters(rows,columns,winLength,gravity,timeLimit);
	}

	@Test
	public void testGetRows() {
		assertEquals(4, classUnderTest.getRows());
	}

	@Test
	public void testGetColumns() {
		assertEquals(5, classUnderTest.getColumns());
	}

	@Test
	public void testGetWinLength() {
		assertEquals(4, classUnderTest.getWinLength());
	}
	
	@Test
	public void testGetTimeLimit() {
		assertEquals(2000, classUnderTest.getTimeLimit());
	}

	@Test
	public void testIsGravity() {
		assertEquals(true, classUnderTest.isGravity());
	}

	@Test
	public void testToString() {
		assertEquals("connectkParameters{rows=4, columns=5, winLength=4, gravity=true}", classUnderTest.toString());
	}

}

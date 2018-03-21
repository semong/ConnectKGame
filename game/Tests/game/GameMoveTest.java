package game;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GameMoveTest {

  private GameMove classUnderTest;

  @Before
  public void setUp() throws Exception {
    int rows = 4;
    int columns = 5;
    classUnderTest = GameMove.getInstance(rows,columns);
  }

	@Test
	public void testHashCode() {
		int result = 4;
		result = 31 * result + 5;
		assertEquals(129, classUnderTest.hashCode());
	}

	@Test
	public void testGetRow() {
		assertEquals(4, classUnderTest.getRow());
	}

	@Test
	public void testGetColumn() {
		assertEquals(5, classUnderTest.getColumn());
	}

	@Test
	public void testToString() {
		assertEquals("GameMove{row=4, column=5}", classUnderTest.toString());
		
	}

	@Test
	public void testEqualsObject() {
		GameMove testObject = GameMove.getInstance(6,9);
		assertEquals(false, classUnderTest.equals(testObject));
	}

}

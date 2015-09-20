import MatrixEncryption.orderChange;

import org.junit.Assert;
import org.junit.Test;

public class orderChangeTest {

	@Test
	public void testPrintOrderChange() {
		int[] origionalArray = {0, 1, 2, 3, 5, 8};
		double[] encodedArray = {7, 2, 1, 4, 3, 9};
		double expected = 0.4;
		double actual = orderChange.printOrderChange(origionalArray, encodedArray);
		
		Assert.assertTrue(expected == actual);
	}

}

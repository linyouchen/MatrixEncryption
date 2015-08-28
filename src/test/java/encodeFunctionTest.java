import MatrixEncryption.encodeFunction;

import org.junit.Assert;
import org.junit.Test;

public class encodeFunctionTest {
	String testString = "Welcome to MMDBlab";
	int[] f = {140, 116, 86, 245};
	
	@Test
	public void testStringToASCII() {
		int[] ascii = encodeFunction.stringToASCII(testString);
		int[] expected = {87, 101, 108, 99, 111, 109, 101, 32 ,116 ,111, 32, 77, 77, 68, 66, 108, 97, 98, 0, 0 };
		Assert.assertArrayEquals(expected, ascii);
	}

	@Test
	public void testEncode() {
		double[] encrypted = encodeFunction.encode(encodeFunction.stringToASCII(testString), f);
		double[] expected = {
				0.6214285714285714, 0.7928571428571428, 0.8285714285714285, 0.5499999999999999, 0.6928571428571428, 
				1.6206896551724137, 1.896551724137931, 	1.956896551724138, 	1.25, 				1.6810344827586206, 
				3.44186046511628, 	3.732558139534884, 	3.0116279069767447, 2.4534883720930236, 2.2674418604651168, 
				1.612244897959184, 	1.4408163265306124, 1.3714285714285714, 1.3020408163265307, 0.7959183673469388 };
		Assert.assertArrayEquals(expected, encrypted, 0);
	}

	@Test
	public void testSetF() {
		int[] matrixF = encodeFunction.setF(f);
		int[] expected = {
				19600, 16240, 12040, 34300, 
				35840, 29696, 22016, 62720, 
				47880, 39672, 29412, 83790, 
				82180, 68092, 50482, 143815 };
		Assert.assertArrayEquals(expected, matrixF);
	}

}


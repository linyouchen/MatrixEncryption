import MatrixEncryption.sourcePackage;

import org.junit.Test;
import org.junit.Assert;

public class sourcePackageTest {

	@Test
	public void packageTest() {
		double[] sourceD = {0.01, 0.03, 0.05, 0.07, 0.09};
        int[] sourceF = {140,116,86,245};

        sourcePackage test = new sourcePackage(sourceD, sourceF);
        
        
        Assert.assertArrayEquals(sourceD, test.getD(), 0.0001);
        Assert.assertArrayEquals(sourceF, test.getF());
        
        double[] newD = {0.02, 0.04, 0.06, 0.08, 0.12};
        int[] newF = {140,116,86,247};
        test.setD(newD);
        test.setF(newF);
        Assert.assertArrayEquals(newD, test.getD(), 0.0001);
        Assert.assertArrayEquals(newF, test.getF());
        
	}

}

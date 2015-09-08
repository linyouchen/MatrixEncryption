import MatrixEncryption.StringSimilarity;



import org.junit.Assert;
import org.junit.Test;

public class StringSimilarityTest {
	StringSimilarity test = new StringSimilarity();
	
	@Test
	public void testRandomStringGenerator() {
		int length = 5;
		int as = 2;
		String rs = test.randomStringGenerator(length, as);
		
		int sameChar = 0;
		String measure = ""+rs.charAt(0);
		for(int i=1; i<rs.length(); i++){
			sameChar = 0;
			for(int j=0; j<measure.length(); j++){
				if(rs.charAt(i) == measure.charAt(j) ){
					sameChar++;
				}
			}
			if(sameChar == 0){
				measure += rs.charAt(i);
			}
		}
		
		Assert.assertTrue(length == rs.length());
		Assert.assertTrue(as == measure.length());
	}
	
	@Test
	public void testMeasureSimilarity(){
		String password = "aaabb";
		int as = 2;
		test.measureSimilarity(password, as);
	}
	
	@Test
	public void testRecordString(){
		String password = "aaabb";
		int as = 2;
		test.recordString(password, password.length(), as);
	}

}

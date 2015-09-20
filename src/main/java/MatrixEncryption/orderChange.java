package MatrixEncryption;

import java.io.FileWriter;
//import java.io.IOException;
//import java.lang.Object;
//import jebl.math.Binomial;

public class orderChange {
	
	
	public static double printOrderChange(int[] origionalArray, double[] encodedArray){
		FileWriter fr;
		int length = origionalArray.length;
		
		//output sets that orders are changed
		try {
			fr = new FileWriter("orderChange.txt");
			
			double count=0, total=0;
			double outcome =0;
			//total =  Binomial.choose2(length);
			
			for(int i=0; i<length-1;i++){
				for(int j=i+1; j<length;j++){
					total++;
					
					if(		(origionalArray[i] > origionalArray[j] && encodedArray[i] < encodedArray[j]) ||
							(origionalArray[i] < origionalArray[j] && encodedArray[i] > encodedArray[j]) ){
						count++;
						try {
							String set = "("+origionalArray[i]+","+origionalArray[j]+"),"+"("+encodedArray[i]+","+encodedArray[j]+") ";
							if(count%5==0){
								if(count==0)set+="\r";
								else set+="\n";
							}
							else set+="\r";
							fr.write(set);
			
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("ERROR"+e.toString());
						}
					}
					//扣除elements值相同的部份
					else if(origionalArray[i] == origionalArray[j]){
						total--;
					}
					
				}
					
			}
			outcome = count/total;
			fr.write(outcome+"\n");
						
			fr.flush();
			fr.close();
			fr = null;
			return outcome;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR"+e.toString());
			System.exit(0);
		}
		return 0;
	}
}
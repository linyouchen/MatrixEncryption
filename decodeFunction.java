package encoding_v2;

import org.ejml.simple.SimpleMatrix;
import java.io.*;

public class decodeFunction {
	public static int[] decode(int[] F, double[] D){
		int[] password = new int[D.length]; //return value
		
		double[] arrayF = new double[25];
		double[] arrayD = new double[D.length];
		int edge = D.length/4;
		
		Writer writer = null;
		try {
			writer = new FileWriter("decodingSteps.txt");
			
			writer.write("P(received from client):\n");
			for(int i=0; i<4;i++){
				for(int j=0; j<edge;j++){
					arrayD[i*edge+j] = D[i*edge+j];
					writer.write(D[i*edge+j] + " " );
				}
				writer.write("\n" );
			}
			
			writer.write("\nF(received from client):\n");
			for(int i=0; i<4;i++){
				for(int j=0; j<4;j++){
					arrayF[i*4+j] = (double) F[i*4+j];
					writer.write(F[i*4+j] + " " );
				}
				writer.write("\n" );
			}
			
			SimpleMatrix matrixF = new SimpleMatrix(4,4,true, arrayF);
			SimpleMatrix matrixD = new SimpleMatrix(4,edge,true, arrayD);
			
			
			/* Set matrix A*/
			double[] arrayA = new double[16];
			arrayA = setA();
			SimpleMatrix A = new SimpleMatrix(4,4,true, arrayA);
			matrixF = A.mult(matrixF);
			
			double[] firstrow = new double[4];
			double firstelement = matrixF.get(0, 0);
			
			for(int i=0; i< 4; i++){
				firstrow[i] = matrixF.get(i,0);
				firstrow[i] = firstrow[i] / Math.sqrt(firstelement);
			}

			/* Set matrix Sigma*/
			double[] arraySigma = new double[16];
			for(int i=0; i<16; i++){
				arraySigma[i] = 0;
			}
			for(int i=0; i<16; i=i+5){
				arraySigma[i] = firstrow[i/5];
			}
			SimpleMatrix Sigma = new SimpleMatrix(4,4,true, arraySigma);
			
			
			SimpleMatrix S = new SimpleMatrix(4,edge);
			S = Sigma.mult(matrixD);
			
			SimpleMatrix R = new SimpleMatrix(4,edge);
			R = A.mult(S);
			
			writer.write("\nThe decoding matrix S:\n");
			for(int i=0; i< 4; i++){
				for(int j=0; j< edge; j++){
					writer.write( (int) Math.round(S.get(i,j)) + " ");
				}
				writer.write("\n" );
			}
			
			writer.write("\nThe decode matrix R:\n");
			for(int i=0; i< 4; i++){
				for(int j=0; j< edge; j++){
					password[i*edge+j] = (int) Math.round(R.get(i,j));
					writer.write( (int) Math.round(R.get(i,j)) +" ");
				}
				writer.write("\n" );
			}
			
			for(int i=0; i< edge; i++){
				for(int j=0; j< 4; j++){
					password[i*4+j] = (int) Math.round(R.get(j,i));
				}
				writer.write("\n" );
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
					writer = null;
				} catch (IOException e) {

					System.err.println("Error closing the file : ");
					e.printStackTrace();
				}
			}
		}

		
		
		return password;
	}
	
	// set encoding matrix A(目前版本是寫死的A)
	public static double[] setA(){
		double[] arrayA = new double[16];
		for(int i=0; i<16; i++){
			arrayA[i] = 0;
		}
		for(int i=4; i<16; i=i+5){
			arrayA[i] = -1;
		}
		for(int i=0; i<16; i=i+5){
			arrayA[i] = 1;
		}
		
		return arrayA;
	}
	
	public static String ASCIItoString(int[] R){
		String query="";
		
		//用pwEnd紀錄多餘的空字元(D[i]==0)個數，避免轉回password時後面多出空字元
		int pwEnd = 0;

		for(int i=0; i<R.length;i++){
			if(R[i] == 0){
				pwEnd--;
			}
		}
		
		System.out.print("\nConvert ascii to char array: ");
		char[] ShowAnswer = new char[R.length+pwEnd];
		for(int i=0; i<R.length+pwEnd;i++){
			ShowAnswer[i] = (char) R[i];
		}
		
		query = String.valueOf(ShowAnswer);
		
		return query;
	}
}

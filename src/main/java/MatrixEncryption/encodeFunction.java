package MatrixEncryption;

import java.io.*;
import org.ejml.simple.SimpleMatrix;

public class encodeFunction {
	
	// convert string to integer array(every character is transformed into correspond ASCII number)
	public static int[] stringToASCII(String data){
		int[] password = null;
		int length = 16;
		
		if(data.length()>16){
			for(int i = 16; i <data.length(); i=i+4){
				length = i+4;
			}
		}
		
		password =  new int[length];
		for (int i=0; i < data.length(); i++){
			password[i] = data.charAt(i);
		}
		for (int i=data.length(); i < length; i++){		//if data.length()!=16, the remaining password[i] is 0
			password[i] = 0;
		}
		

		return password;
	}
	
	
	// encode the password
	public static double[] encode(int[] data, int[] f)
	{
		// return value(matrix P�Harray��{)
		double[] send = new double[data.length];
		
		// ����data array���ǡA���নdouble type�C
		/* �]����array��Jmatrix�ɡAmatrix�O�@��raw�뺡�A��U�@��raw(raw by raw)�A
		 * ���ڷQ�n�몺�覡�Ocolumn by column�A�ҥH�n���վ�array���ǡC
		 * 
		 * �p�Garray a�����׬O4n�A������matrix(4,n)�p�U�G
		 * �z(0,0),(0,1),(0,2),...,(0,n-1)�{
		 * �x(1,0),(1,1),(1,2),...,(1,n-1)�x
		 * �x(2,0),(2,1),(2,2),...,(2,n-1)�x
		 * �|(3,0),(3,1),(3,2),...,(3,n-1)�}
		 * �o��array a��JEJML��SimpleMatrix�覡�O�G
		 * �za(   1),a(   2),a(   3),...,a( n)�{
		 * �xa( n+1),a( n+2),a( n+3),...,a(2n)�x
		 * �xa(2n+1),a(2n+2),a(2n+3),...,a(3n)�x
		 * �|a(3n+1),a(3n+2),a(3n+3),...,a(4n)�}
		 * �i�O�ڷQ�諸�s��覡�G
		 * �zb(1),b(1+4),b(1+8),...,b(1+4(n-1))�{
		 * �xb(2),b(2+4),b(2+8),...,b(2+4(n-1))�x
		 * �xb(3),b(3+4),b(3+8),...,b(3+4(n-1))�x
		 * �|b(4),b(3+4),b(4+8),...,b(4+4(n-1))�}
		 * �ҥH�u�n��a�����ǽվ㦨matrix�̭�����b�A�A��Jmatrix�N�i�H�F��column by column��input
		 */
		double[] array = new double[data.length];
		int edge = data.length/4;
		
		for(int i=0; i<4; i++){
			for(int j=0; j<edge; j++){
				array[i*edge+j] = (double) data[i+j*4];
			}
		}
		SimpleMatrix B = new SimpleMatrix(4,edge,true, array);
		for(int i=0; i<4; i++){
			for(int j=0; j<edge; j++){
				data[i*edge+j] = (int) array[i*edge+j];
			}
		}
		
		// �]�wencoding matrix A, Sigma
		double[] arrayA = new double[16];
		arrayA = setA();
		SimpleMatrix A = new SimpleMatrix(4,4,true, arrayA);
		double[] arraySigma = new double[16];
		arraySigma = setSigma(f);
		SimpleMatrix sigma = new SimpleMatrix(4,4,true, arraySigma);
		
		// Mfsb = inverse(Sigma) X inverse(A)
		SimpleMatrix Mfsb = new SimpleMatrix(4,4);
		Mfsb = sigma.invert().mult(A.invert());
		
		// encode matrix B to matrix P
		SimpleMatrix P = new SimpleMatrix(4,edge);
		P = Mfsb.mult(B);
		
		// �Nmatrix P�Harray send�^��
		for(int i=0; i<4; i++){
			for(int j=0; j<edge; j++){
				send[i*edge+j] = P.get(i,j);
			}
		}
		
		//�N�L�{�O���U��
		recordSteps(data.length, data, A.invert(), Mfsb, send);
		
		return send;
	}
	
	// set encoding matrix A(�ثe�����O�g����A)
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
	
	// set encoding matrix Sigma(�ھ�f�ӳ]�wSigma)
	public static double[] setSigma(int[] f){
		double[] arraySigma = new double[16];
	
		for(int i=0; i<16; i++){
			if(i%5==0){
				arraySigma[i] = f[i/5];
			}
			else{
				arraySigma[i] = 0;
			}
		}
		
		return arraySigma;
	}
	
	// �]�wmatrix F
	public static int[] setF(int[] f){
		double[] arrayF = new double[f.length];
		
		for(int i=0; i<f.length; i++){
			arrayF[i] = (double) f[i]; 
		}

		SimpleMatrix matrixf = new SimpleMatrix(4, 1, true, arrayF);
		SimpleMatrix matrixF = new SimpleMatrix(4,4);
		SimpleMatrix matrixA = new SimpleMatrix(4,4, true, setA());
		
		matrixF = matrixA.invert().mult(matrixf.mult(matrixf.transpose()));

		int[] key = new int[16];
		for(int i=0; i< 4; i++){
			for(int j=0; j< 4; j++){
				key[i*4+j] = (int) matrixF.get(i,j);
			}
		}
		
		return key;
	}
	
	// �Nencode steps�g�Jsteps.txt
	public static void recordSteps(int length, int[] data, SimpleMatrix A,  SimpleMatrix Mfsb, double[] D){

		int edge = length/4;
		
		//Write to "steps.txt"
		Writer writer = null;
		try {
			writer = new FileWriter("encodingSteps.txt");

			writer.write("The matrix B\n");
			for(int i=0; i<4; i++){
				for(int j=0; j<edge; j++){
					writer.write(data[i*edge+j]+" ");
				}
				writer.write("\n");
			}
			
			writer.write("\nThe encoding matrix inverseA is:\n");
			for(int i=0; i<4; i++){
				for(int j=0; j<4; j++){
					writer.write( (int) A.get(i,j) +" ");
				}
				writer.write("\n");
			}
			
			writer.write("\nThe encoding matrix Mfsb is:\n");
			for(int i=0; i<4; i++){
				for(int j=0; j<4; j++){
					writer.write( Mfsb.get(i,j) +" ");
				}
				writer.write("\n");
			}
			
			writer.write("\nThe encode password matrix P is:\n");
			for(int i=0; i<4; i++){
				for(int j=0; j<edge; j++){
					writer.write(  D[i*edge+j] +" ");
				}
				writer.write("\n");
			}
			
		} catch (IOException e) {
			System.err.println("Error writing the file : ");
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
		
		
	}
}
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
		// return value(matrix P以array表現)
		double[] send = new double[data.length];
		
		// 改變data array順序，並轉成double type。
		/* 因為把array丟入matrix時，matrix是一個raw塞滿再塞下一個raw(raw by raw)，
		 * 但我想要塞的方式是column by column，所以要先調整array順序。
		 * 
		 * 如果array a的長度是4n，對應的matrix(4,n)如下：
		 * ┌(0,0),(0,1),(0,2),...,(0,n-1)┐
		 * │(1,0),(1,1),(1,2),...,(1,n-1)│
		 * │(2,0),(2,1),(2,2),...,(2,n-1)│
		 * └(3,0),(3,1),(3,2),...,(3,n-1)┘
		 * 這時array a放入EJML的SimpleMatrix方式是：
		 * ┌a(   1),a(   2),a(   3),...,a( n)┐
		 * │a( n+1),a( n+2),a( n+3),...,a(2n)│
		 * │a(2n+1),a(2n+2),a(2n+3),...,a(3n)│
		 * └a(3n+1),a(3n+2),a(3n+3),...,a(4n)┘
		 * 可是我想改的存放方式：
		 * ┌b(1),b(1+4),b(1+8),...,b(1+4(n-1))┐
		 * │b(2),b(2+4),b(2+8),...,b(2+4(n-1))│
		 * │b(3),b(3+4),b(3+8),...,b(3+4(n-1))│
		 * └b(4),b(3+4),b(4+8),...,b(4+4(n-1))┘
		 * 所以只要把a的順序調整成matrix裡面對應b，再放入matrix就可以達成column by column的input
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
		
		// 設定encoding matrix A, Sigma
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
		
		// 將matrix P以array send回傳
		for(int i=0; i<4; i++){
			for(int j=0; j<edge; j++){
				send[i*edge+j] = P.get(i,j);
			}
		}
		
		//將過程記錄下來
		recordSteps(data.length, data, A.invert(), Mfsb, send);
		
		return send;
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
	
	// set encoding matrix Sigma(根據f來設定Sigma)
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
	
	// 設定matrix F
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
	
	// 將encode steps寫入steps.txt
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
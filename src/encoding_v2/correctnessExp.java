/* --------- 探討傳送浮點數時要取至小數點後第幾個digit --------*
 * [分析]
 *  1.字元值愈大，愈容易產生誤差(ex. 同樣改變1%，100以上的值才會受影響)。
 *    目前考慮的ASCII table字元範圍是32~126，故以126作討論。
 *    (如果要extend到中文或其他字元，那就要再修改實驗或說明實驗限制)
 *  2.f值愈大時傳送的數值些微改變造成的影響就愈大
 * [實驗規劃]
 *  -X axis: 小數點取到第幾位
 *  -Y axis: correctness
 *          (字元原本的ascii integer是否和經過encode, decode後的integer一樣)
 *  分別畫出不同f值的線條，探討f值大小與浮點數取值範圍的關係
 * ---------------------------------------------------*/

package encoding_v2;

import encoding_v2.encodeFunction;
import encoding_v2.decodeFunction;
import java.io.*;

public class correctnessExp {
	public static Writer writer = null;
	
	public double[] doubleFormatTransformer(double[] array, int afterPoint){
		double[] adjust = new double[array.length];
		
		double place = Math.pow(10, afterPoint);
		
		for (int i=0; i<array.length; i++){
			adjust[i] = Math.round(array[i] * place) / place;
		}
		
		
		return adjust;
	}
	
	public void findSmallestDigit(int f1){
		// 討論string為16個'~'字元，其ascii int = 126 
		// 測試不同的f值
		String str = "~~~~~~~~~~~~~~~~";
		int[] f = {1, 1, 1, 1};
		f[0] = f1;
		int[] F = new int[16];
		F = encodeFunction.setF(f);
		
		/* --------------- 實驗步驟 ----------------- *
		 * step 1: string轉成integer array
		 * step 2-a: encode ─ 以各種f值對array加密
		 * step 2-b: decode ─ 用f將encoded array還原
		 * step 3: 比較還原的array與原本的array是不是一樣
		 * ---------------------------------------- */
		
		int correct = 1;
		int[] array = encodeFunction.stringToASCII(str);
		double[] encodedArray = encodeFunction.encode(array, f);
		double[] adjust = new double[array.length];
		int[] decodedArray = new int[array.length];
		
		
		for(int place=1; place<16; place++){
			adjust = doubleFormatTransformer(encodedArray, place);
			decodedArray = decodeFunction.decode(F, adjust);

			correct = 1;
			for(int l=0; l<array.length; l++){
				if (array[l] != decodedArray[l]){
					correct = 0;
					//System.out.println("f[0]="+f[0]+" ,value= "+decodedArray[l]);
					break;
				}
			}
			if (correct == 1){
				//To do: 把結果貼到.txt檔
				System.out.println("f[0]="+f[0]+" ,place= "+place);
				break;
			}
		}
				
	}
	public void fsd(int f){
		int correct = 1;
		int[] array = {126, 126, 126, 126};
		double[] encodedArray = new double[4];
		for(int i=0; i<4; i++){
			encodedArray[i] = ((double) array[i]) / (double) f;
		}
		double[] adjust = new double[array.length];
		int[] decodedArray = new int[array.length];
		
		for(int place=1; place<16; place++){
			adjust = doubleFormatTransformer(encodedArray, place);
			for (int i=0; i<4; i++){
				decodedArray[i] = (int) Math.round(adjust[i] * f);
			}

			correct = 1;
			for(int l=0; l<array.length; l++){
				if (array[l] != decodedArray[l]){
					correct = 0;
					//System.out.println("f="+f+" ,value= "+decodedArray[l]);
					break;
				}
			}
			if (correct == 1){
				//To do: 把結果貼到.txt檔
				//System.out.println("f="+f+" ,place= "+place);
				try {
					writer.write(f+"\t"+place+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	public static void main(String[] args){
		correctnessExp experiment = new correctnessExp();
		/*
		int[] testF = {99, 199, 499, 999, 1499, 1999, 3999, 32767};
		
		for(int i=0; i<testF.length; i++){
			experiment.findSmallestDigit(testF[i]);
		}
		*/
		try {
			writer = new FileWriter("smallestDigit.txt");
			for(int f=1; f<=40000; f++){
				experiment.fsd(f);
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
		
	}
}

/* --------- ���Q�ǰe�B�I�Ʈɭn���ܤp���I��ĴX��digit --------*
 * [���R]
 *  1.�r���ȷU�j�A�U�e�����ͻ~�t(ex. �P�˧���1%�A100�H�W���Ȥ~�|���v�T)�C
 *    �ثe�Ҽ{��ASCII table�r���d��O32~126�A�G�H126�@�Q�סC
 *    (�p�G�nextend�줤��Ψ�L�r���A���N�n�A�ק����λ������筭��)
 *  2.f�ȷU�j�ɶǰe���ƭȨǷL���ܳy�����v�T�N�U�j
 * [����W��]
 *  -X axis: �p���I����ĴX��
 *  -Y axis: correctness
 *          (�r���쥻��ascii integer�O�_�M�g�Lencode, decode�᪺integer�@��)
 *  ���O�e�X���Pf�Ȫ��u���A���Qf�Ȥj�p�P�B�I�ƨ��Ƚd�����Y
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
		// �Q��string��16��'~'�r���A��ascii int = 126 
		// ���դ��P��f��
		String str = "~~~~~~~~~~~~~~~~";
		int[] f = {1, 1, 1, 1};
		f[0] = f1;
		int[] F = new int[16];
		F = encodeFunction.setF(f);
		
		/* --------------- ����B�J ----------------- *
		 * step 1: string�নinteger array
		 * step 2-a: encode �w �H�U��f�ȹ�array�[�K
		 * step 2-b: decode �w ��f�Nencoded array�٭�
		 * step 3: ����٭쪺array�P�쥻��array�O���O�@��
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
				//To do: �⵲�G�K��.txt��
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
				//To do: �⵲�G�K��.txt��
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

package MatrixEncryption;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.Random;
import java.io.*;
import MatrixEncryption.encodeFunction;

public class StringSimilarity {
	public Connection con = null;
	
	public StringSimilarity(){
		// �s��MySQL database
		try{
	    	  Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	  con = DriverManager.getConnection(
		    		  "jdbc:mysql://localhost:3306/experiment1?useUnicode=true&characterEncoding=Big5", 
		    		  "root","mmdb95511");
		} catch(ClassNotFoundException cnfe){ //���i��|����sqlexception 
			System.out.println("DriverClassNotFound :"+cnfe.toString()); 
		} catch(SQLException x) { 
			System.out.println("SQLException :"+x.toString()); 
		} catch(Exception e){
			System.out.println("Other Exception :"+e.toString());
		}
	      
	}
	
	public String randomStringGenerator(int length, int as){
		
		StringBuilder rs = new StringBuilder(); //build random string
		Random random = new Random(); 
		
		// get alphabets(�e��as�ӬO�H�����͡F�Ѿl���h�O���ƫe�����C)
		int[] aSet = new int[length];
		//�H����as�Ӧr���@��string��alphabet set (aSet[0~9])
		for(int i=0; i<as; i++){
			/*------ �]�wstring�ѭ��Ǧr��(��ASCII integer�Ӫ��)�զ� ------*
			 * �@��`�����Ʀr�B�r���B�Ÿ��r���@�� 95 �� 
			 * 		->random�d��=95
			 * ASCII table: 32(space), 33(!), 34("),..., 126(~)
			 * 		-> �q32�}�l
			 *------------------------------------------------------*/
			aSet[i] = 32 + random.nextInt(95);
			
			// �ˬd���S�����쭫�ƪ���
			for(int j=0; j<i;j++){
				// �p�G���쭫�ƭȡA���s����
				if(aSet[i] == aSet[j]){
					i--;
					break;
				}
			}
		}
		// ���Ƥw�w�X�Ӫ�vectors
		for (int i = as; i < length; i++) {
			aSet[i] = aSet[random.nextInt(as)];
		}
		
		// generate string
		int c = length;
		for (int i = 0; i < length; i++) {
			//�qvector set�̭��H���D�X�@�ӧ@��string����i�Ӧr��
		    c = random.nextInt(length-i);
		    rs.append( (char)aSet[c] );
		    
		    aSet[c] = aSet[length-1-i];
		}
		
		return rs.toString();
	}
	
	public void measureSimilarity(String password, int as){
		
		int stringLength = password.length();
		
		// ��r���নinteger array
		int[] array = new int[encodeFunction.stringToASCII(password).length];
		array = encodeFunction.stringToASCII(password);
		
		// encode
		int[] f = {140, 116, 86, 245};
		double[] encodedArray = new double[password.length()];
		encodedArray = encodeFunction.encode(array, f);
		// �ھ�encodeFunction.encode����array��matrix�覡�A�����Τ@���ܼƧ���array�ȹ����� encode matrix array����m
		int edge = encodedArray.length/4;
		
		//int as=0;
		double total=stringLength*(stringLength-1)/2, originCount=0, encodedCount=0;
		double originalSimilarity=0, encodedSimilarity=0;
		
		for(int i=0; i<stringLength-1; i++){
			for(int j=i+1; j<stringLength; j++){
				if(	password.charAt(i) == password.charAt(j) ){
					originCount++;
				}
				if(	encodedArray[(i%4)*edge+i/4] == encodedArray[(j%4)*edge+j/4]){
					encodedCount++;
				}
			}
		}
		originalSimilarity = originCount/total;
		encodedSimilarity = encodedCount/total;
		
		storeData(stringLength, as, originalSimilarity, encodedSimilarity, password);
	}

	public void storeData(int length, int as, double original, double encoded, String password){

		
		try {
			//Connection con = getConnection();
			Statement state = null;
			state = con.createStatement();
			
			
			String sql = "INSERT INTO similarity VALUES("+as+","+length+","+original+","+encoded+",'') ";
						
			state.executeUpdate(sql);
			
			try { 
				if(state!=null){
					state.close(); 
					state = null; 
				} 
			} catch(SQLException e) { 
				System.out.println("Close Exception :" + e.toString()); 
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void recordString(String str, int length, int as){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Strings.txt", true)));
		    out.println(as+" "+length+" "+str);
		    out.close();
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	public static void main(String[] args)
	{  
		// connect to database
		StringSimilarity experiment = new StringSimilarity();
		
		// set of string length
		int[] strLength = {5, 10, 15, 20, 25, 50, 100};
		// set of alphabets
		int[] as = {1, 2, 3, 5, 10};
		
		//* ---------------------��--��---�}--�l---------------------- *
		//* Step 1(generate string): �̷�as���H������Strings
		//* Step 2(measure similarity): 
		//* 		a. �NString�নASCII integer array
		//* 		b. encode the string(array)
		//* 		(���O�Na, b��output�@similarity�p��A�M��N���絲�G���MySQL)
		//* ----------------------�}-��-�a-��-��----------------------- 
		
		for(int l=0; l<strLength.length; l++){
			for(int v=0; v<as.length;v++){
				// �C�ر��p������times��
				if(strLength[l] >= as[v]){
					for(int times=0; times<15; times++){
						// step 1
						String rs = experiment.randomStringGenerator(strLength[l], as[v]);
						// step 2
						experiment.measureSimilarity(rs, as[v]);
						// string���i��t��SQL�y�k�r���A�s�bMySQL�|�X�������txt��
						experiment.recordString(rs, rs.length(), as[v]);
					}
				}
			}
		}
		
	}
}

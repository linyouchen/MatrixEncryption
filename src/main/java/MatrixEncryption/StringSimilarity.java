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
		// 連接MySQL database
		try{
	    	  Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	  con = DriverManager.getConnection(
		    		  "jdbc:mysql://localhost:3306/experiment1?useUnicode=true&characterEncoding=Big5", 
		    		  "root","mmdb95511");
		} catch(ClassNotFoundException cnfe){ //有可能會產生sqlexception 
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
		
		// get alphabets(前面as個是隨機產生；剩餘的則是重複前面的。)
		int[] aSet = new int[length];
		//隨機找as個字元作為string的alphabet set (aSet[0~9])
		for(int i=0; i<as; i++){
			/*------ 設定string由哪些字元(用ASCII integer來表示)組成 ------*
			 * 一般常見的數字、字母、符號字元共有 95 個 
			 * 		->random範圍=95
			 * ASCII table: 32(space), 33(!), 34("),..., 126(~)
			 * 		-> 從32開始
			 *------------------------------------------------------*/
			aSet[i] = 32 + random.nextInt(95);
			
			// 檢查有沒有取到重複的值
			for(int j=0; j<i;j++){
				// 如果取到重複值，重新取值
				if(aSet[i] == aSet[j]){
					i--;
					break;
				}
			}
		}
		// 重複已定出來的vectors
		for (int i = as; i < length; i++) {
			aSet[i] = aSet[random.nextInt(as)];
		}
		
		// generate string
		int c = length;
		for (int i = 0; i < length; i++) {
			//從vector set裡面隨機挑出一個作為string的第i個字元
		    c = random.nextInt(length-i);
		    rs.append( (char)aSet[c] );
		    
		    aSet[c] = aSet[length-1-i];
		}
		
		return rs.toString();
	}
	
	public void measureSimilarity(String password, int as){
		
		int stringLength = password.length();
		
		// 把字串轉成integer array
		int[] array = new int[encodeFunction.stringToASCII(password).length];
		array = encodeFunction.stringToASCII(password);
		
		// encode
		int[] f = {140, 116, 86, 245};
		double[] encodedArray = new double[password.length()];
		encodedArray = encodeFunction.encode(array, f);
		// 根據encodeFunction.encode內部array轉matrix方式，必須用一個變數找到原array值對應到 encode matrix array的位置
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
		
		//* ---------------------實--驗---開--始---------------------- *
		//* Step 1(generate string): 依照as數隨機產生Strings
		//* Step 2(measure similarity): 
		//* 		a. 將String轉成ASCII integer array
		//* 		b. encode the string(array)
		//* 		(分別將a, b的output作similarity計算，然後將實驗結果丟到MySQL)
		//* ----------------------開-心-地-結-束----------------------- 
		
		for(int l=0; l<strLength.length; l++){
			for(int v=0; v<as.length;v++){
				// 每種情況都驗證times次
				if(strLength[l] >= as[v]){
					for(int times=0; times<15; times++){
						// step 1
						String rs = experiment.randomStringGenerator(strLength[l], as[v]);
						// step 2
						experiment.measureSimilarity(rs, as[v]);
						// string內可能含有SQL語法字元，存在MySQL會出錯→放到txt檔
						experiment.recordString(rs, rs.length(), as[v]);
					}
				}
			}
		}
		
	}
}

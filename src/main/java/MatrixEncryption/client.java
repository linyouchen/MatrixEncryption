package MatrixEncryption;

import java.net.InetSocketAddress;
import java.net.Socket;

import MatrixEncryption.sourcePackage;
import MatrixEncryption.encodeFunction;

import java.io.ObjectOutputStream; 
import java.io.*;
import java.util.Random;


public class client {
    private String address = "127.0.0.1";// 連線的ip
    private int port = 8765;// 連線的port

    public client() {
 
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
        
        int[] factor = {140,116,86,245};
        
        //從外部檔案query.txt讀取
        String queryLine = "";
        String factorLine = "";
        try {
        	FileReader fr = new FileReader("query.txt");
        	BufferedReader br = new BufferedReader(fr);
        	
        	queryLine = br.readLine();
        	factorLine = br.readLine();
        	
        	br.close();
        	br = null;
        	fr.close();
        	fr = null;
        }
        catch (IOException e) {
        	System.out.println(e);
        }
        
        //將讀取到的password加密並傳送封包
        try {
            client.connect(isa, 10000);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); 
            
            // 把query.txt line 1讀取到的String轉換成對應的ASCII number array
            int[] password =  encodeFunction.stringToASCII(queryLine);
            
            // 將query.txt line 2轉成Integer array並設定要傳輸的array sourceF(代表matrix F)
            int[] sourceF={0};
            String[] factorArray = factorLine.split(" |\\.|\\,");
            Random random = new Random(); 
            for(int i=0; i<4; i++){
            	factor[i] = Integer.parseInt(factorArray[i]);
            	if(factor[i] == 0){
            		factor[i] = random.nextInt(99)+1;
            	}
            }
            sourceF = encodeFunction.setF(factor) ;
            
            // 將password encode，然後設為傳輸用array sourceD(代表 matrix P)
            double[] sourceD = encodeFunction.encode(password,  factor);
            
            // 把sourceD與sourceF放入package，然後送出
            sourcePackage encrypted = new sourcePackage(sourceD, sourceF);
            out.writeObject(encrypted); // 送出object

            out.flush();
            out.close();
            out = null;
            client.close();
            client = null;
 
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 (client)!");
            System.out.println("IOException :" + e.toString());
        }
    }
    
 
    public static void main(String args[]) {
        new client();
    }
}

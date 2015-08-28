package MatrixEncryption;

import java.net.InetSocketAddress;
import java.net.Socket;

import MatrixEncryption.sourcePackage;
import MatrixEncryption.encodeFunction;

import java.io.ObjectOutputStream; 
import java.io.*;
import java.util.Random;


public class client {
    private String address = "127.0.0.1";// �s�u��ip
    private int port = 8765;// �s�u��port

    public client() {
 
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
        
        int[] factor = {140,116,86,245};
        
        //�q�~���ɮ�query.txtŪ��
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
        
        //�NŪ���쪺password�[�K�öǰe�ʥ]
        try {
            client.connect(isa, 10000);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream()); 
            
            // ��query.txt line 1Ū���쪺String�ഫ��������ASCII number array
            int[] password =  encodeFunction.stringToASCII(queryLine);
            
            // �Nquery.txt line 2�নInteger array�ó]�w�n�ǿ骺array sourceF(�N��matrix F)
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
            
            // �Npassword encode�A�M��]���ǿ��array sourceD(�N�� matrix P)
            double[] sourceD = encodeFunction.encode(password,  factor);
            
            // ��sourceD�PsourceF��Jpackage�A�M��e�X
            sourcePackage encrypted = new sourcePackage(sourceD, sourceF);
            out.writeObject(encrypted); // �e�Xobject

            out.flush();
            out.close();
            out = null;
            client.close();
            client = null;
 
        } catch (java.io.IOException e) {
            System.out.println("Socket�s�u�����D (client)!");
            System.out.println("IOException :" + e.toString());
        }
    }
    
 
    public static void main(String args[]) {
        new client();
    }
}

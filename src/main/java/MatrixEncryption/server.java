package MatrixEncryption;

import java.net.ServerSocket;
import java.net.Socket;

import MatrixEncryption.sourcePackage;
import MatrixEncryption.decodeFunction;

public class server extends java.lang.Thread {
 
    private boolean OutServer = false;
    private ServerSocket server;
    private final int ServerPort = 8765;// 要監控的port
 
    public server() {
        try {
            server = new ServerSocket(ServerPort);
 
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
 
    public void run() {
        Socket socket;
        java.io.ObjectInputStream in;
 
        System.out.println("伺服器已啟動 !");
        while (!OutServer) {
            socket = null;
            try
            {
                synchronized (server)
                {
                    socket = server.accept();
                }
                System.out.println("取得連線 : InetAddress = " + socket.getInetAddress());
                // TimeOut時間
                socket.setSoTimeout(15000);
                
                // 取得client端發送的object
                in = new java.io.ObjectInputStream(socket.getInputStream());
                
                // 將object依照package格式 放到data
                sourcePackage data = (sourcePackage) in.readObject();

                // 從package拿出double array D(代表matrix p)以及double array F(matrix F)
				double[] retrieveD = data.getD();
				int[] retrieveF = data.getF();
				
				// 利用matrix F解碼matrix P，得到一個還原的matrix R(存放成integer array R)
				int[] R = new int[retrieveD.length];
				R = decodeFunction.decode(retrieveF, retrieveD);
				
				// 將array R依照ASCII table轉回對應的String
				String password = decodeFunction.ASCIItoString(R);
				System.out.println(password);

                in.close();
                in = null;
                socket.close();
 
            } catch (java.io.IOException e) {
                System.out.println("Socket連線有問題 !");
                System.out.println("IOException :" + e.toString());
            } catch(java.lang.ClassNotFoundException e) { 
            	System.out.println("ClassNotFoundException :" + e.toString()); 
            } 
 
        }
    }

    public static void main(String args[]) {
        (new server()).start();
    }
 
}

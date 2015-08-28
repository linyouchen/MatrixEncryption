package MatrixEncryption;

import java.net.ServerSocket;
import java.net.Socket;

import MatrixEncryption.sourcePackage;
import MatrixEncryption.decodeFunction;

public class server extends java.lang.Thread {
 
    private boolean OutServer = false;
    private ServerSocket server;
    private final int ServerPort = 8765;// �n�ʱ���port
 
    public server() {
        try {
            server = new ServerSocket(ServerPort);
 
        } catch (java.io.IOException e) {
            System.out.println("Socket�Ұʦ����D !");
            System.out.println("IOException :" + e.toString());
        }
    }
 
    public void run() {
        Socket socket;
        java.io.ObjectInputStream in;
 
        System.out.println("���A���w�Ұ� !");
        while (!OutServer) {
            socket = null;
            try
            {
                synchronized (server)
                {
                    socket = server.accept();
                }
                System.out.println("���o�s�u : InetAddress = " + socket.getInetAddress());
                // TimeOut�ɶ�
                socket.setSoTimeout(15000);
                
                // ���oclient�ݵo�e��object
                in = new java.io.ObjectInputStream(socket.getInputStream());
                
                // �Nobject�̷�package�榡 ���data
                sourcePackage data = (sourcePackage) in.readObject();

                // �qpackage���Xdouble array D(�N��matrix p)�H��double array F(matrix F)
				double[] retrieveD = data.getD();
				int[] retrieveF = data.getF();
				
				// �Q��matrix F�ѽXmatrix P�A�o��@���٭쪺matrix R(�s��integer array R)
				int[] R = new int[retrieveD.length];
				R = decodeFunction.decode(retrieveF, retrieveD);
				
				// �Narray R�̷�ASCII table��^������String
				String password = decodeFunction.ASCIItoString(R);
				System.out.println(password);

                in.close();
                in = null;
                socket.close();
 
            } catch (java.io.IOException e) {
                System.out.println("Socket�s�u�����D !");
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

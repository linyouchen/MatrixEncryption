import MatrixEncryption.client;
import MatrixEncryption.server;

import org.junit.Test;

public class socketTest {

	@Test
	public void test() throws Exception{
		server sr = new server();
		Thread serverThread = new Thread(sr);
		serverThread.start();
		
		new client();
	}

}

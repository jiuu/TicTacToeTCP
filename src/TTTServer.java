import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TTTServer {
	
    public static void main(String[] args) throws IOException {
    	ServerSocket serverSocket = null;
		boolean listening = true;
		try {
			serverSocket = new ServerSocket(4445);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4445.");
			System.exit(-1);
		}
        System.out.println("Quote Server is up and running.....");
		while (listening)
		{
			Socket clntSock = serverSocket.accept();  //accept the incoming call, and pass the NEW socket to the thread
			TTTServerThread quote = new TTTServerThread(clntSock);
			Thread T = new Thread(quote);
			T.start();
		}
		serverSocket.close();
    }
}

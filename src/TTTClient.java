import java.io.*;
import java.net.*;
import java.util.*;
public class TTTClient {
	private static final int ECHOMAX = 256;
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		if (args.length != 1) {
			System.out.println("Usage: java QuoteClient <hostname>");
			return;
		}
		// get a TCP socket
		// set up reader from the server and writer from the client
		Socket socket = new Socket("localhost", 4445); // make TCP connection
		OutputStream sendStream;
		InputStream recvStream;
		int gameOver = 0;
		char[] board = new char[9];
		Arrays.fill(board, '-');
		System.out.println(board);
		EchoData data;
		EchoData received;
		sendStream = socket.getOutputStream();
		recvStream = socket.getInputStream();

		while (true) { 

			// send request for quote

			// get quote
			received = getResponse(recvStream, socket);
			System.out.println(received.toString());
			board = received.getBoard();
			System.out.println("" + board[0] + board[1] + board[2] + "\n" + board[3] + board[4] + board[5] + "\n" +board[6] + board[7] + board[8]);
			gameOver = received.getGameOver();
			
			if (gameOver != 0) {
				if (gameOver == 3) {
					System.out.println("Draw, exiting...");
					break;
				}
				String victor = (gameOver == 1) ? "Server" : "Client";
				System.out.println(victor + " has won, exiting...");
				break;
			}
			System.out.println("Input your move: ");
			BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
			int move = Integer.parseInt(fromKeyboard.readLine());
			while (board[move] == 'O' || board[move] == 'X' || move > 8 || move < 0) {
				
				System.out.println("Illegal move, try again");
				move = Integer.parseInt(fromKeyboard.readLine());
			}

			data = new EchoData(move, board, gameOver);
	        sendRequest(data, sendStream);
			
			
		}
		

        sendStream.close();
		recvStream.close();
		socket.close(); // when all done asking for quotes, close the socket
	}
	/*static boolean isGameOver(char[] board) {
		if (board[1] && board[2] && board[3])
		System.out.println("Game Over");
		return false;
	}*/

	static void sendRequest(EchoData data, OutputStream sendStream) throws ClassNotFoundException {
		try {
			byte[] sendBuff = new byte[ECHOMAX];
			sendBuff = makeSerializedEchoData(data);
			//System.out.println("sending data to server...");
			sendStream.write(sendBuff, 0, sendBuff.length);
			//System.out.println("sent request...");
		} catch (IOException ex) {
			System.err.println("IOException in sendRequest");
		}
	}

	static EchoData getResponse(InputStream recvStream, Socket socket) throws IOException, ClassNotFoundException {
		EchoData response = null;
		try {
			byte[] recvBuff = new byte[ECHOMAX];
			recvStream.read(recvBuff, 0, ECHOMAX);
		    ByteArrayInputStream byteStream2 = new ByteArrayInputStream(recvBuff);
	 		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream2));
	 		response = (EchoData) is.readObject();

		} catch (SocketException e) {
			System.out.println("Connection closed prematurely");
			socket.close();

		} catch (IOException ex) {
			System.err.println("IOException in getResponse");
		}
		return response;
	} // End getResponse
	
	static byte[] makeSerializedEchoData(EchoData data) throws IOException, ClassNotFoundException {	
		//System.out.println("Client name: " + data.getName() + " echo: " + data.getData());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(ECHOMAX); //create a byte array big enough to hold serialized object
		ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream)); //wrap the byte stream with an Object stream
		os.flush();
		os.writeObject(data); //now write the data to the object output stream (still have not sent the packet)
		os.flush();
		//retrieves byte array
		byte[] sendBuf = byteStream.toByteArray();  //get a byte array from the serialized object	
		os.close();
		return sendBuf;
	}

}

import java.io.*;
import java.net.*;
import java.util.*;

public class TTTServerThread implements Runnable {
	private static final int ECHOMAX = 256;

	Socket socket;
	InputStream recvStream;
	OutputStream sendStream;
	EchoData data;
	EchoData received;
	int gameOver = 0;
	int draw = 0;
	char[] board = new char[9];
	
	int move = 1;


	// protected DatagramSocket socket = null;
	protected BufferedReader in = null;
	protected boolean moreQuotes = true;

	public TTTServerThread(Socket clntSock) throws IOException {
		this.socket = clntSock;
		this.recvStream = clntSock.getInputStream();
		this.sendStream = clntSock.getOutputStream();
	
	}

	public void run(){
		Arrays.fill(board, '-');
		System.out.println(board);
		
		while (!isGameOver(board,draw)) {
			System.out.println("Input your move: ");
			BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
			try {
				move = Integer.parseInt(fromKeyboard.readLine());
				makeMove(board,move,1);
				if (isGameOver(board,draw)) {
					if (draw == 1) {
						System.out.println("Draw, exiting...");
						gameOver = 3;
					}
					else{
						System.out.println("Server has won, exiting...");
						gameOver = 1;
					}
					
					data = new EchoData(move, board, gameOver);
					
					sendRequest(data, sendStream);
					break;
				}
				data = new EchoData(move, board, gameOver);
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			sendRequest(data, sendStream);
			try {
				received = getResponse(recvStream, socket);
				board = received.getBoard();
				makeMove(board, received.getMove(), 2);
				System.out.println("" + board[0] + board[1] + board[2] + "\n" + board[3] + board[4] + board[5] + "\n" +board[6] + board[7] + board[8]);

					if (isGameOver(board, draw)) {
						if (draw == 1) {
							System.out.println("Draw, exiting...");
							received.setGameOver(3);
						}
						else {
							System.out.println("Server has won, exiting...");
						}						received.setGameOver(2);
						sendRequest(received, sendStream);
						break;
					}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

		}
		try {
			System.out.println("Server Thread [" + Thread.currentThread().getName() + "] is now gone!");
			socket.close();
			sendStream.close();
			recvStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void makeMove(char[] board, int move, int player) throws NumberFormatException, IOException { //1 = Server, 2 = Client
	while (board[move] == 'O' || board[move] == 'X' || move > 8 || move < 0) {
		
		System.out.println("Illegal move, try again");
		BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
		move = Integer.parseInt(fromKeyboard.readLine());
	}
	board[move] = (player == 1) ? 'O' : 'X';
	System.out.println("Player " + player + " has chosen " + move);
	}
	
	static boolean isGameOver(char[] board, int d) {
	if ((board[0] != '-' && board[0] == board[1] && board[1] == board[2]) ||
			(board[3] != '-' && board[3] == board[4] && board[4] == board[5]) ||
			(board[6] != '-' && board[6] == board[7] && board[7] == board[8]) ||
			(board[0] != '-' && board[0] == board[3] && board[3] == board[6]) ||
			(board[1] != '-' && board[1] == board[4] && board[4] == board[7]) ||
			(board[2] != '-' && board[2] == board[5] && board[5] == board[8]) ||
			(board[0] != '-' && board[0] == board[4] && board[4] == board[8]) ||
			(board[2] != '-' && board[2] == board[4] && board[4] == board[6])) {
		
		System.out.println("Game Over");
		return true;
	}
	if (board[0] != '-' && board[1] != '-' && board[2] != '-' && board[3] != '-' && board[4] != '-' && board[5] != '-' && board[6] != '-' && board[7] != '-' && board[8] != '-') {
		System.out.println("Draw");
		d = 1;
		return true;
	}

	return false;
	}
	
	static byte[] makeSerializedEchoData(EchoData data) throws IOException {	
		//System.out.println("Client name: " + data.getName() + " echo: " + data.getData());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(ECHOMAX); //create a byte array big enough to hold serialized object
		ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream)); //wrap the byte stddream with an Object stream
		os.flush();
		os.writeObject(data); //now write the data to the object output stream (still have not sent the packet)
		os.flush();
		//retrieves byte array
		byte[] sendBuf = byteStream.toByteArray();  //get a byte array from the serialized object	
		os.close();
		return sendBuf;
	}
	static void sendRequest(EchoData data, OutputStream sendStream) {
		try {
			byte[] sendBuff = new byte[ECHOMAX];
			sendBuff = makeSerializedEchoData(data);
			System.out.println("sending data to client...");
			sendStream.write(sendBuff, 0, sendBuff.length);
			System.out.println("sent request...");
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
	

}

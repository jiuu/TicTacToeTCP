import java.io.Serializable;


public class EchoData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int move;
	private char[] board ;
	private int gameOver = 0; //0 = not over, 1 = server, 2 = client
	
	public EchoData(int m, char[] b, int g)
	{
		this.move = m;
		this.board = b;
		this.gameOver = g;
	}
	public int getMove()
	{
		return move;	
	}
	public char[] getBoard()
	{
		return board;	
	}
	public int getGameOver() {
		return gameOver;
	}
	public void setMove(int m)
	{
		this.move = m;	
	}
	public void setBoard(char[] b) {
		this.board = b;
	}
	public void setGameOver(int g)
	{
		this.gameOver = g;	
	}
	public void printIt(){
		System.out.println("Move: " + move);
	}
	public String toString(){
		return ("Move: " + move + " Board: " + String.copyValueOf(board) + " GameOver: " + gameOver);
	}
}
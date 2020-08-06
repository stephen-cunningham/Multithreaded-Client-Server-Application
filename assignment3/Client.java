package assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

//source: https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/ for where to close the socket
public class Client {
	public static void main(String[] args) {
		try {
			Socket s = new Socket("localhost", 9999);
			System.out.println("Client: connected to server");
			//this BufferedReader takes input from the clients(s)/user(s), from the console
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			//this BufferedReader takes the output of the client thread
			BufferedReader fromThread = new BufferedReader(new InputStreamReader(s.getInputStream()));
			//this sends data to the server
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			
			while (true) {//infinite loop
                String output = userInput.readLine();//this assigns the user input to a String variable
                writer.println(output);//the user input is printed to the PrintWriter
                System.out.println("Client: sent '" + output + "' to server");//displays what was sent to the server in the console
                writer.flush();
                if(output.equals("QUIT")){//if 'QUIT' is entered, the socket is closed, and the loop is broken
                    s.close(); 
                    System.out.println("Client: closed connection to server"); 
                    break; 
                } 
                String received = fromThread.readLine();
                System.out.println("Client: received message from server '" + received + "'"); 
            } 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
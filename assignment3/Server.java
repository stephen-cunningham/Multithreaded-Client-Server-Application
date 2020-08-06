package assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;

public class Server{
	//using a TreeSet to have non-duplicating tokens, stored in lexicographical order. It is made static to ensure only one instance exists
	private static Set<String> list = new TreeSet<>();
	
	public Set<String> getList(){
		return list;
	}
	
	public static void main(String[] args) {
		ServerSocket server;
		try {
			server = new ServerSocket(9999);
			server.setReuseAddress(true);//ensures the port 9999 can be used when the application is terminated and re-ran
			while(true) {//infinite loop
				System.out.println("Server: waiting for a client to connect");
				Socket s = server.accept();//accepting the connection from client(s)
				BufferedReader inStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter outStream = new PrintWriter(s.getOutputStream());
				Thread t = new ClientThread(s, inStream, outStream, list);//calling the ClientThread constructor to create a client thread object
				t.start();//starting the thread
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
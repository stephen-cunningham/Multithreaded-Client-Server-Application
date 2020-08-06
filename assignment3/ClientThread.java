package assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThread extends Thread{//extending thread so that ClientThread inherits its functionality
	private Set<String> list;
	private static AtomicInteger clientNum = new AtomicInteger(1);//this will be used to count and identify each unique thread
	private int uniqueClientNum;//declaring private integer field that will be used to store the unique id

	public ClientThread(Socket socket, BufferedReader inStream, PrintWriter outStream, Set<String> list) {
		this.socket = socket;
		this.inStream = inStream;
		this.outStream = outStream;
		this.list = list;
	}
	
	public void setUniqueClientNum() {
		this.uniqueClientNum = clientNum.getAndIncrement();
	}
	
	public int getUniqueClientNum() {
		return uniqueClientNum;
	}
	
	final BufferedReader inStream;
	final PrintWriter outStream;
	final Socket socket;

	@Override
	public void run() {
		String input;//this will be used to store the message from the client
		String response;//this will be used to store the response to the client

		synchronized(this) {//preventing race conditions
			try {
				this.setUniqueClientNum();//setting the client thread's unique id number
				System.out.println("Server: client" + this.getUniqueClientNum() + " connected");
				input = inStream.readLine();
				
				while(!input.equals("QUIT")){//this keeps the loop going until the client sends 'QUIT'
					response = "";
					System.out.println("Server: received message from client" + this.getUniqueClientNum() +" '" + input + "'");
					if(input.equals("RETRIEVE")){
						/*Replying with the sorted global list of tokens currently on 
						 * the server (with the tokens separated by whitespace). If
						 * the global tokens list is currently empty, replying with 'ERROR'.*/
						if(list.size() == 0) {
							response = "ERROR";
						}else {
							for(String o: list) {
								response += o + " ";
							}
						}
					}else if(input.contains("SUBMIT")) {//checking if 'SUBMIT' is contained in the client's input
						/*If the global tokens list already contains the submitted token, 
						 * don’t modify the list, just send message 'OK' to the client. If 
						 * the token is not yet in the list and the list is not yet full, 
						 * add the token to the list and send message OK to the client. 
						 * Otherwise (list is full and token is not yet in the list), 
						 * don’t add anything to the list and respond to the client with 
						 * message 'ERROR'.*/
						//e.g. if the client sends 'SUBMIT car horse', 'SUBMIT ' is stored in token[0] and 'car horse' is stored in token[1]
						String token[] = input.split("SUBMIT ");
						String submitted = token[1];
						if(token[1].contains(" ")){//ensuring that tokens with whitespace aren't added to the global token list
							response = "tokens can't contain whitespace - try again";
						}else {
							if(list.contains(submitted)) {//checking if the token already exists in the global token list
								response = "OK";
							}else {
								if(list.size()<10) {//ensuring that the global token list only stores a maximum of 10 tokens
									list.add(submitted);
									response = "OK";
								}else {
									response = "ERROR";
								}
							}
						}
					}else {//ensuring the client can only use the commands 'RETRIEVE', 'QUIT', or 'SUBMIT token'
						response = "Incompatible input.";
					}
					System.out.println("Server: sent response '" + response + "' to client" + this.getUniqueClientNum());
					outStream.println(response);
					outStream.flush();
					response = null;
					
					input = inStream.readLine();
				}
				//handling the 'QUIT' input from the client
				System.out.println("Server: received message from client '" + input + "'");
				System.out.println("Server: client" + this.getUniqueClientNum() + " disconnected");
				socket.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
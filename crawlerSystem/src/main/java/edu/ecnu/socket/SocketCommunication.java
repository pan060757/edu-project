package edu.ecnu.socket;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketCommunication {
	private static int port;
	private static String ip;
	private static int sourceID;
	private static Socket socket;
	private static PrintWriter os;

	public SocketCommunication(String ip, int port, int sourceID) {
		this.ip = ip;
		this.port = port;
		this.sourceID = sourceID;
		try {
			socket = new Socket(ip, port);
			socket.setKeepAlive(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(String message){
		if(message!= null && !message.equals("") && !message.equals("bye")){
			os.println(message);
			os.flush();
		}
		else{
			os.close();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
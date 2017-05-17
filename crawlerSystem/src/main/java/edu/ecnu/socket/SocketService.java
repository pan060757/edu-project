package edu.ecnu.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService extends Thread {

    class Receive extends Thread {
        private Socket socket;
        public Receive(Socket socket) {
            this.socket = socket;
        }
        public void run() {

            try {
                System.out.println("New connection accepted " +
                        socket.getInetAddress() + ":" + socket.getPort());
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String readline="";
                while(socket.isConnected()) {
                    readline = br.readLine();
                    try {
                        if (readline == null || readline.equals("") || readline.equals("bye"))
                            break;
                        System.out.println(readline + "\t" + socket.getPort());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private int port = 4700;
    private ServerSocket serverSocket;

    public SocketService() throws Exception {
        serverSocket = new ServerSocket(port);
    }

    private void service() {
        System.out.println("Init MessageServer");

        while (true) {
            System.out.println("Waiting Client");
            try {
                Socket socket = serverSocket.accept();
                Receive receive = new Receive(socket);
                receive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void run() {
        service();
    }
    public static void main(String[] args) {
        try {
            new SocketService().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

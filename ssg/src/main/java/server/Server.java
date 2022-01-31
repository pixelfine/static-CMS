package server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import parser.Builder;
import watch.FileWatcher;

public class Server {

    String inputFolder;
    String outputFolder;
    int cores;
    int portNumber;

    public Server(String input, String output, int cores, int portNumber){
        this.inputFolder = input;
        this.outputFolder = output;
        this.cores = cores;
        this.portNumber = portNumber;
    }

    public void launch(){
        try {
            final ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress("0.0.0.0", portNumber), 10);

            System.out.println("ServerSocket waiting on port " + portNumber);

            System.out.println("Building initial output files");
            new Builder(inputFolder, outputFolder, Runtime.getRuntime().availableProcessors()).buildRoot(false,true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client detected");

                if(FileWatcher.anyModified()){
                    new Builder(inputFolder, outputFolder, Runtime.getRuntime().availableProcessors()).buildRoot(false,true);
                }

                new ClientManager(inputFolder, outputFolder, clientSocket).start();
            }
        }
        catch (Exception e) {
            System.err.println("Server crashed");
            System.exit(1);
        }
    }
}

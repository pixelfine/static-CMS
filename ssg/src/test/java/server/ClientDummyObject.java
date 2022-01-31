import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import server.Server;
import server.ClientManager;
import validator.HtmlValidator;

import static org.junit.jupiter.api.Assertions.*;

class ClientDummyObject {
    static String inputPath  = "./test/";
    static String outputPath = "./test/_output/";
    static int cores = Runtime.getRuntime().availableProcessors();
    static int port = 8080;
    static Thread serverThread;

    String file = "test";

    private void waitForSocketToReset(int time){
        try{
            Thread.sleep(time);
        } catch(Exception e){
            fail();
            e.printStackTrace();
        }
    }

    @BeforeAll
    public static void createServerThread(){
        serverThread = new Thread(){
            @Override
            public void run(){
                new Server(inputPath, outputPath, cores, port).launch();
            }
        };
        serverThread.start();
    }

    @AfterAll
    public static void closeServerThread(){
        serverThread.interrupt();
    }

    @Test
    @Disabled
    public void sendHtmlPage(){
        try (Socket clientSocket = new Socket("127.0.0.1", port)) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner scanner = new Scanner(clientSocket.getInputStream(), StandardCharsets.UTF_8);

            out.println("GET /" + file + ".html HTTP/1.1");
            out.flush();

            StringBuilder sb = new StringBuilder();
            boolean startConcat = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (startConcat) sb.append(line);
                if (line.contains("</form>")) startConcat = true;
            }

            String fileHtml = HtmlValidator.getContentFromFile(outputPath + file + ".html");
            assertEquals(fileHtml, sb.toString(), "Not same file returned by client manager in getHtmlPage");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
        waitForSocketToReset(3);
    }

    @Test
    @Disabled
    public void sendEditPage(){
        try (Socket clientSocket = new Socket("127.0.0.1", port)) {

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner scanner = new Scanner(clientSocket.getInputStream(), StandardCharsets.UTF_8);

            out.println("GET /" + file + ".html?edit=true HTTP/1.1");
            out.flush();

            StringBuilder sb = new StringBuilder();
            boolean concat = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("</textarea>")) concat = false;
                else if (concat) sb.append(line);
                else if (line.contains("<textarea id=\"Edited\" name=\"edited\" rows=\"100\" cols=\"100\">"))
                    concat = true;
            }

            String fileMd = HtmlValidator.getContentFromFile(inputPath + "content/" + file + ".md");
            assertEquals(fileMd, sb.toString(), "Not same file returned by client manager in getEditPage");
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
        waitForSocketToReset(3);
    }
}
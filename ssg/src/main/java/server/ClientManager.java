package server;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import parser.Builder;

public class ClientManager extends Thread {

    String inputFolder;
    String outputFolder;
    Socket clientSocket;
    static String htmlFile;
    static String mdFile;
    static String mwFile;

    public ClientManager(String input, String output, Socket sock){
        inputFolder = input;
        outputFolder = output;
        clientSocket = sock;
    }

    private void sendEditTextArea(String path) throws Exception {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        FileInputStream fs = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fs));

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("\r\n");

        out.println("<!DOCTYPE HTML>\n<html>\n<head></head>\n<body>\n");
        out.flush();

        out.println("<form action=\"" + htmlFile + "?edit=false\" method=\"post\">\n" +
                "<button type=\"submit\" name=\"edit\" value=\"false\">Save</button>\n");
        out.flush();

        out.println("<textarea id=\"Edited\" name=\"edited\" rows=\"100\" cols=\"100\">");
        out.flush();

        String line;
        while ((line = br.readLine()) != null) {
            out.println(line);
            out.flush();
        }

        out.println("</textarea>\n</form>\n</body>\n</html>");
        out.flush();

        out.close();
        fs.close();
        br.close();
    }

    private void sendWebFile(String path, boolean refresh) throws Exception {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        FileInputStream fs = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fs));

        out.println("HTTP/1.1 200 OK");
        if(path.endsWith(".html")) out.println("Content-Type: text/html");
        else if(path.endsWith(".css")) out.println("Content-Type: text/css");
        out.println("\r\n");

        String[] splitLine = path.split("/");
        path = splitLine[splitLine.length-1];

        if(path.endsWith(".html")) {
            out.println("<form action=\"" + path + "\" method=\"get\">\n" +
                    "<button type=\"submit\" name=\"edit\" value=\"true\">Edit</button>\n" +
                    "</form>");
            out.flush();
        }

        String line;
        while ((line = br.readLine()) != null) {
            out.println(line);
            out.flush();
            if(refresh && line.contains("<head>") && path.endsWith(".html")){
                out.println("<meta http-equiv=\"refresh\" content=\"5; URL="+ path +"\">");
            }
        }

        out.close();
        fs.close();
        br.close();
    }

    private void saveFile(String editedArea, String path) throws Exception {
        Files.writeString(Path.of(path), editedArea);
    }

    @Override
    public void run() {
        try (InputStream inputToServer = clientSocket.getInputStream();
             Scanner scanner = new Scanner(inputToServer, StandardCharsets.UTF_8)) {

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();

                //DEBUG
                //System.out.println("Got: [" + line +"];");

                String[] splitline = line.split(" ");

                //if we get a line starting with GET or POST
                if(splitline.length > 1 && (splitline[0].equals("GET") || splitline[0].equals("POST"))) {

                    //splitline[1] = 'page.html?edit=value'
                    //split to check if GET value 'edit' exist
                    //splitLine[0] = 'page.html'
                    //splitLine[1] = 'edit=value'
                    splitline = splitline[1].split("\\?");

                    //if we aren't getting a html or css file that exist
                    if ((!splitline[0].endsWith(".html") && !splitline[0].endsWith(".css")) || !(new File(outputFolder + splitline[0])).exists()) continue;

                    //save the name of the html file for the md file
                    if (splitline[0].endsWith(".html")) htmlFile = splitline[0];

                    //Didn't receive GET value, no edit
                    if (splitline.length == 1) {
                        sendWebFile(outputFolder + splitline[0], false);
                    }
                    //if edit=true, we send the source md file in a textarea
                    else if (splitline[1].contains("true")) {
                        mdFile = htmlFile.replace(".html", ".md");
                        mwFile = htmlFile.replace(".html", ".mw");
                        String pathForMd = inputFolder + "content/" + mdFile;
                        String pathForMw = inputFolder + "content/" + mwFile;
                        if((new File(pathForMd)).exists()) sendEditTextArea(pathForMd);
                        else sendEditTextArea(pathForMw);
                    }
                    //we received edit=false, we know that we will get a POST data with the edited source file
                    //so we send the original page with a timer for a auto-refresh
                    else sendWebFile(outputFolder + splitline[0], true);
                }
                //we got the POST form values : 'edit=false&edited=edited_textarea'
                else if(splitline.length == 1 && splitline[0].startsWith("edit=false")){
                    String editedArea = splitline[0].split("=")[2];
                    editedArea = URLDecoder.decode(editedArea, StandardCharsets.UTF_8);

                    //DEBUG
                    //System.out.println(editedArea);

                    //save the file, watchdog will then build the new html file and the refresh will
                    //show the new html file produced

                    if((new File(inputFolder + "content/" + mdFile)).exists()){
                        saveFile(editedArea, inputFolder + "content" + mdFile);
                    } 
                    else{
                        saveFile(editedArea, inputFolder + "content" + mwFile);
                    }

                    
                }
            }
        } catch (Exception e) {
            System.out.println("Error while processing Client");
            e.printStackTrace();
        }
    }
}

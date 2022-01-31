import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import console.*;
import parser.Analyzer;
import parser.Builder;
import parser.FileManager;
import parser.Metadata;
import parser.record.TimeRecorder;

/**
 * Main class of the project
 * Init of commands
 * Launch command provided by the user
 */
public class Main {

    /**
     * Usage of commands: command [options] [arguments]
     * @param args
     */
    public static void main(String[] args) {
        try{
            CommandUser command = (new CommandUser(Console.getInstance(), args));
            command.run();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

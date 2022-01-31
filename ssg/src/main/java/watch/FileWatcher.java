package watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.nio.charset.Charset;

public class FileWatcher{

    static File saveFile = new File("./src/main/resources/data/pathsSaved");
    static Path savePath = Paths.get(saveFile.getAbsolutePath());

    public static boolean savePath(Path filePath){
        try{
            saveFile.createNewFile();

            File inputFile = new File(filePath.toString());

            int line;
            if((line = FileWatcher.existsInSave(filePath)) != -1)
                if(!FileWatcher.hasBeenModified(line))
                    return false;

            if(line == -1){
                String newLine = filePath + ","+inputFile.lastModified()+ System.lineSeparator();
                Files.write(savePath,newLine.getBytes(),StandardOpenOption.APPEND);
            } else {
                List<String> saveData = Files.readAllLines(savePath);
                saveData.set(line, filePath + ","+inputFile.lastModified());
                Files.write(savePath,saveData);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    //Returns the line of the path given in argument or -1 if it isn't present
    public static int existsInSave(Path path){
        try{
            List<String> saveData = Files.readAllLines(savePath);
            for(int i = 0; i < saveData.size(); i++){
                String[] tokens = saveData.get(i).split(",");
                if(tokens[0].equals(path.toString()))
                    return i;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean hasBeenModified(int line){
        try{
            List<String> saveData = Files.readAllLines(savePath);
            String[] tokens = saveData.get(line).split(",");
            if(tokens[1].equals(Long.toString(new File(tokens[0]).lastModified())))
                return false;
        } catch (Exception e){
            e.printStackTrace();
        }
 
        return true;
    }

    public static boolean anyModified(){
        try{
            List<String> saveData = Files.readAllLines(savePath);
            for(String s : saveData){
                String[] tokens = s.split(",");
                if(!tokens[1].equals(Long.toString(new File(tokens[0]).lastModified())))
                    return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
 
        return false;
    }
    
    public static void wipeSave(){
        try{
            saveFile.createNewFile();
            Files.write(savePath,"".getBytes());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
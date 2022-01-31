package watch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.nio.charset.Charset;

import watch.FileWatcher;
import parser.Builder;

public class WatchdogTest {

    @Test
    public void testWatchdog() {
        Path inputPath = Paths.get("./test/content");
        Path outputPath = Paths.get("./output_test/");

        //Wipe the save file
        FileWatcher.wipeSave();

        try{
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace();
        }
        
		File[] filesList = inputPath.toFile().listFiles();

        //Assert that no seen files are in the save file
		for (File file : filesList) {
            assertTrue(FileWatcher.existsInSave(file.toPath()) == -1);
        }

        //Save the files
        for (File file : filesList) {
            FileWatcher.savePath(file.toPath());
        } 

        try{
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace();
        }
        
        //Assert that all seen files are in the save file
        for (File file : filesList) {
            assertTrue(FileWatcher.existsInSave(file.toPath()) != -1);
        }

        //Assert that all seen files have not been modified since building
        for (File file : filesList) {
            assertFalse(FileWatcher.hasBeenModified(FileWatcher.existsInSave(file.toPath())));
        }

        //Create a new path for our future test file
        Path newFilePath = Paths.get("./test/content/WatchdogTester.md");

        //Assert that it doesn't exist yet in the save
        assertTrue(FileWatcher.existsInSave(newFilePath) == -1);

        //Copy the first file of the folder to the path
        try{
            Files.write(newFilePath,"base text".getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
        
        //Assert that it still doesn't exist yet in the save
        assertTrue(FileWatcher.existsInSave(newFilePath) == -1);

        //Assert that the new path was added to the savefile
        assertTrue(FileWatcher.savePath(newFilePath));

        //Assert that the new path exists in the savefile now
        assertTrue(FileWatcher.existsInSave(newFilePath) != -1);

        //Assert that it hasn't been modified
        assertFalse(FileWatcher.hasBeenModified(FileWatcher.existsInSave(newFilePath)));

        //Append a character to our new file
        try{
            Thread.sleep(1000);
            Files.write(newFilePath,"new text".getBytes());
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace();
        }

        //Assert that the file has now been modified
        assertTrue(FileWatcher.hasBeenModified(FileWatcher.existsInSave(newFilePath)));

        //Assert that the global detection sees it too
        assertTrue(FileWatcher.anyModified());

        //Assert that the file was correctly saved
        assertTrue(FileWatcher.savePath(newFilePath));

        //Assert that the file is not counted as modified anymore
        assertFalse(FileWatcher.hasBeenModified(FileWatcher.existsInSave(newFilePath)));

        //Assert that the global detection sees it too
        assertFalse(FileWatcher.anyModified());

        try{
            Files.delete(newFilePath);
        } catch (IOException e){
            e.printStackTrace();
        }

        FileWatcher.wipeSave();
    }
}

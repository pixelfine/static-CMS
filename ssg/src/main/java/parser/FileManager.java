package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import parser.record.RecordRoot;
import parser.record.TimeRecorder;

public class FileManager {
	public enum CopyMethod{
		COPY, HARDLINK, SYMLINK
	}
	
	
	public static boolean modifiedCopyMethods(Path src, Path dst, boolean replace, CopyMethod method) throws IOException {
		File srcfile		= src.toFile();
		if(!srcfile.exists()) { throw new FileNotFoundException("Source file not found :"+srcfile.getAbsolutePath());}
		if(replace) Files.deleteIfExists(dst);
		//System.out.println("copying "+src+" to "+dst);
		switch(method) {
			case COPY : 	Files.copy					(src, dst);										return true;
			case HARDLINK : Files.createLink			(dst.toAbsolutePath(), src.toAbsolutePath());	return true;
			case SYMLINK :  Files.createSymbolicLink 	(dst.toAbsolutePath(), src.toAbsolutePath());	return true;
			default : throw new IOException("Unexpected copy method");
		}
	}
	
	public static void createDir(Path dest) {
		try {
			java.nio.file.Files.createDirectories(dest);
		} catch (IOException e) {}
	}
	
	
	public static void copyFolder(Path src, Path dest, boolean replace, CopyMethod method) {
		try {
			createDir(dest);
			java.nio.file.Files.walk(src).forEach(sourcePath -> {
	        	Path targetPath = dest.resolve(src.relativize(sourcePath));
	        	if(sourcePath.toFile().isDirectory()) createDir(targetPath);
	        	else {
	        		try {
		            	modifiedCopyMethods(sourcePath, targetPath, replace, method);
		            } catch (IOException ex) {
		            	//ex.printStackTrace();
		            }
	        	}
	        });
		}catch(IOException e) {}
	}
	
	public static void copyFolder(Path src, Path dest, boolean replace, CopyMethod method, HashSet<String> contentFile) {
		try {
			createDir(dest);
			java.nio.file.Files.walk(src).forEach(sourcePath -> {
	        	Path targetPath = dest.resolve(src.relativize(sourcePath));
	        	if(sourcePath.toFile().isDirectory()) createDir(targetPath);
	        	else {
	        		try {
	        			if(!contentFile.contains(sourcePath.toFile().getName())) {
	        				modifiedCopyMethods(sourcePath, targetPath, replace, method);
	        			}
		            } catch (IOException ex) {
		            	//ex.printStackTrace();
		            }
	        	}
	        });
		}catch(IOException e) {}
	}
	
	public static void copyModifiedFolder(Path src, Path dest, boolean replace, CopyMethod method, TimeRecorder record, RecordRoot root) {
		try {
			createDir(dest);
			java.nio.file.Files.walk(src).forEach(sourcePath -> {
	        	Path targetPath = dest.resolve(src.relativize(sourcePath));
	        	if(sourcePath.toFile().isDirectory()) createDir(targetPath);
	        	else {
	        		try {
	        			if(record.isModified(root, sourcePath) && modifiedCopyMethods(sourcePath, targetPath, replace, method)) {
	        				record.addTime(root, sourcePath);
	        			}
		            } catch (IOException ex) {
		            	//ex.printStackTrace();
		            }
	        	}
	        });
		}catch(IOException e) {}
	}
}

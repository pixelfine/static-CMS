package parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.*;

import com.google.common.io.Files;

import parser.record.RecordRoot;
import parser.record.TimeRecorder;
import parser.toml.DefaultKey;
import parser.toml.TomlParser;
import parser.html.HtmlParser;
import watch.FileWatcher;

public class Builder {
	public int maxCores;
	public List<Thread> threadList = new ArrayList<Thread>();
	public String input;
	public String output;
	public String theme;
	public TimeRecorder time;

	public Builder(String input, String output, int cores) {
		this.maxCores      	= cores;
		this.input 			= input;
		this.output			= output;
		this.time			= new TimeRecorder(new File(output+TimeRecorder.filename));
	}

	public synchronized void buildRoot(boolean rebuildAll, boolean watch) {
		HashSet<String> directories   = new HashSet<String>();
		HashSet<String> filesGenerate = new HashSet<String>();
		Metadata metadata = new Metadata();
		TimeRecorder recorder = new TimeRecorder(new File(output+TimeRecorder.filename))
				.create()
				.fetch()
				//.printFile()
		;
		RecordRoot root 	= null;
		File folder = new File(input);
		if(!folder.exists()){
			System.out.println("The input folder does not exist !");
			System.exit(1);
		} else if(!folder.isDirectory()){
			System.out.println("The input is not a folder !");
			System.exit(1);
		}
		File[] filesList = folder.listFiles();
		for (File file : filesList) {
			String filename = file.getName();
			if (file.isFile()) {
				if(watch){
					if(!FileWatcher.savePath(file.toPath())){//Save the file and build if it wasn't here previously
						if(!rebuildAll)
							continue;
					}
				}
				String extension = Analyzer.getFileExtension(filename).orElse("");
				switch(extension) {
					case "toml" :  metadata.add(new TomlParser().parse(file.toPath()));
					root = new RecordRoot(file.getAbsolutePath(), null);
					if(recorder.isModified(root, file.toPath())) {
						recorder.addTime(new RecordRoot(file.getAbsolutePath(), null),  file.toPath());
						rebuildAll=true;
					}break;
				}
			}else if(file.isDirectory()) {
				if(filename.equals("content") || filename.equals("static") || filename.equals("templates") || filename.equals("themes")) {
					for(String s : HtmlParser.list_files(file.toString(), true)){//Of each file under the list 
						if(watch){
							if(!FileWatcher.savePath(Paths.get(input+filename+s))){//Save the content of the directory into the save file and add it to the copy list if it wasn't here previously
								if(!rebuildAll)
									continue;
							}
						}

						if(!directories.contains(filename))
							directories.add(filename);
					}
				}
			}
		}
		if(directories.contains("content")) {
			filesGenerate = buildContent(metadata, new File(input+"/content/"), recorder, rebuildAll, watch);
		}
		if(directories.contains("static")) {
			FileManager.copyFolder(Paths.get(input+"/static/"), Paths.get(output+"/static/")   , true, FileManager.CopyMethod.HARDLINK);
		}
		if(directories.contains("templates")) {
			FileManager.copyFolder(Paths.get(input+"/templates/"), Paths.get(output), true, FileManager.CopyMethod.HARDLINK, filesGenerate);
		}
	}
	
	
	public HashSet<String> buildContent(Metadata metadata, File contentRoot, TimeRecorder recorder, boolean rebuildAll, boolean watch) {
		HashSet<String> files = new HashSet<String>();
		for (File file: Files.fileTraverser().breadthFirst(contentRoot)){
			if (file.isFile()) {
				
				RecordRoot r = recorder.getRoot(file.getAbsolutePath(), recorder.fileMap);
				files.add(Analyzer.convertExtToHtml(file.getName()));
				
				while(threadList.size() >= maxCores){
					for(int i = 0; i < maxCores; i++){
						if(!threadList.get(i).isAlive()){
							threadList.remove(i);
							break;
						}
					}
				}

				Thread thread = new Thread() {
					public void run(){
						if(rebuildAll || r==null || recorder.isModifiedRoot(r) ) {
							new Analyzer(new Metadata (metadata), input, output, file.getAbsolutePath(), recorder).parse();
						} else {
							try {
								RecordRoot key = null;
								for(RecordRoot k : recorder.fileMap.keySet()) {
									if(k.extension.equals(file.getAbsolutePath())) { key = k; break;}
								}
								if(key!=null) {
									File theme = Builder.toTheme(input, key.theme);
									if(theme!=null && theme.exists()) {
										Builder.copyTheme(theme.toPath(), new File(output).toPath());
									}
								}
							} catch (IOException e) {}
						}		
					}
				};

				threadList.add(thread);
				thread.start();
				
			}
		}

		while(threadList.size() > 0){
			for(int i = 0; i < threadList.size(); i++){
				if(!threadList.get(i).isAlive()){
					threadList.remove(i);
					break;
				}
			}
		}

		recorder.save();
		return files;
	}
	
	
	public static File findTemplate(String filename, String root, Metadata data) {
		File found = null;
		File folder = new File(root+"/templates/");
		if(folder.exists()) {
			found = findFile(folder, filename, true);
			if(found!=null) {
				return found;
			}
		}
		folder = toTheme(root, data);
		if(folder!=null && folder.exists()) {
			found = findFile(folder, filename, true);
			if(found!=null) return found;
		}
		return null;
	}
	
	public static File toTheme(String root, Metadata data) {
		String theme = data.getContent("theme");
		if(!theme.equals(DefaultKey.metadataMap.get("theme"))) {
			return toTheme(root, theme);
		}return null;
	}
	
	private static File toTheme(String root, String data) {
		File themeFolder = new File(root+"/themes/"+data+"/");
		if(themeFolder.exists()) return themeFolder;
		return null;
	}
	
	
	public static File findFile(File from, String filename, boolean findFile) {
		for (File file: Files.fileTraverser().breadthFirst(from)){
			if(findFile) {
				if(file.isFile() && file.getName().equals(filename)) {
					return file;
				}
			}else {
				if(file.isDirectory() && file.getName().equals(filename)) {
					return file;
				}
			}
		}return null;
	}
	
	
	public static  void copyFolder(Path src, Path dest, boolean replace) {
		try {
			java.nio.file.Files.walk(src)
	        .forEach(sourcePath -> {
	        	Path targetPath = dest.resolve(src.relativize(sourcePath));
	            try {
	            	//System.out.println("Copy  : "+sourcePath.toString()+" "+targetPath.toString());
	            	if(replace) java.nio.file.Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
	            	else 		java.nio.file.Files.copy(sourcePath, targetPath);
	            } catch (IOException ex) {
	            	//System.out.println("Couldn't copy : "+sourcePath.toString()+" "+targetPath.toString());
	            }
	        });
		}catch(IOException e) {}
	}
	
	public static void copyTheme(Path src, Path dest) throws IOException {
		File folder = new File(src+"/static/");
		
		if(folder.exists()) {
			FileManager.copyFolder(folder.toPath(), Paths.get(dest.toString(),"/static/"), false, FileManager.CopyMethod.HARDLINK);
		}
		folder      = new File(src+"/templates/");
		if(folder.exists()) {
			FileManager.copyFolder(folder.toPath(), Paths.get(dest.toString()),            false, FileManager.CopyMethod.HARDLINK);
		}
	}
	
}

package parser.record;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import serializer.Serializer;


public class TimeRecorder {
	public static File defaultSource = new File(  "./test/_output/.settings/time.ser" );
	public static String filename = "/.settings/time.ser";

	public File recordFile;
	public HashMap<RecordRoot, HashMap<String, Long>> pathMap;
	public HashMap<RecordRoot, HashMap<String, Long>> fileMap;


	public TimeRecorder(File sourceFile){
		this.recordFile=sourceFile;
		this.pathMap = new HashMap<RecordRoot, HashMap<String, Long>>();
		this.fileMap = new HashMap<RecordRoot, HashMap<String, Long>>();
	}
	
	public TimeRecorder create() {
		try {
			Files.createDirectories(this.recordFile.getParentFile().toPath());
			this.recordFile.createNewFile();
			return this;
		} catch (IOException e) {
			return this;
		}
	}

	public TimeRecorder addTime(RecordRoot fileKey, Path path) {
		try {
			//System.out.println("Adding : "+fileKey+"\n\t= "+path);
			add(fileKey, path, this.pathMap);
		} catch (IOException e) {e.printStackTrace();}
		return this;
	}

	protected void add(RecordRoot fileKey, Path path, HashMap<RecordRoot, HashMap<String, Long>> dstMap) throws IOException {
		HashMap<String, Long> map = this.pathMap.getOrDefault(fileKey, null);
		if(map==null) {
			map = new HashMap<String, Long>();
			dstMap.put(fileKey, map);
		}
		map.put(path.toString(), java.nio.file.Files.getLastModifiedTime(path).toMillis());
	}


	public TimeRecorder printFile() {
		printMap(this.fileMap);
		return this;
	}

	public TimeRecorder printPath() {
		printMap(this.pathMap);
		return this;
	}

	protected void printMap(HashMap<RecordRoot, HashMap<String, Long>> map) {
		if(map.isEmpty()) return;
		for( Entry<RecordRoot, HashMap<String, Long>> e :  map.entrySet()) {
			HashMap<String, Long> m = e.getValue();
			System.out.println(e.getKey()+" :");
			for(Entry <String, Long> e2 : m.entrySet()) {
				System.out.println("\t["+e2.getKey()+"|"+   e2.getValue()+"]");
			}
		}
	}

	@SuppressWarnings("unused")
	public TimeRecorder save() {
		if(this.fileMap!=null) this.pathMap.putAll(fileMap);
		Serializer.<HashMap<RecordRoot, HashMap<String, Long>>>save(this.pathMap, this.recordFile.getAbsolutePath());
		return this;
	}

	public TimeRecorder fetch() {
		this.fileMap = Serializer.<HashMap<RecordRoot, HashMap<String, Long>>>load(this.recordFile.getAbsolutePath()).orElse(
				new HashMap<RecordRoot, HashMap<String, Long>>()
		);
		/*
		if(this.fileMap==null) {
			System.out.println("It is null");
		}else if(this.fileMap.isEmpty()){
			System.out.println("It is empty");
		}else {
			System.out.println("It contains data");
		}
		*/
		return this;
	}

	public boolean isModified(RecordRoot key, Path path){
		try {
			if(!exists(key, path, fileMap)) return true;
			//System.out.println(java.nio.file.Files.getLastModifiedTime(path).toMillis()+" vs "+ fileMap.get(key).get(path.toString()));
			return java.nio.file.Files.getLastModifiedTime(path).toMillis() != fileMap.get(key).get(path.toString());
		} catch (IOException e) {
		    System.err.println("Cannot get the last modified time - " + e);
		    return false;
		}
	}

	public boolean exists(RecordRoot key, Path path, HashMap<RecordRoot, HashMap<String, Long>> map) {
		HashMap<String, Long> k =  map.getOrDefault(key, null);
		//System.out.println("Exist Check");
		if(k!=null) {
			//System.out.println("Is not null");
			return k.containsKey(path.toString());
		}
		//System.out.println(key+ " \t "+path+" is null");
		return false;
	}
	
	public boolean isModifiedRoot(RecordRoot root) {
		HashMap<String, Long> m = fileMap.get(root);
		if(m==null) return true;
		for(Entry <String, Long> e2 : m.entrySet()) {
			if(isModified(root,   Paths.get(e2.getKey()) )) {
				return true;
			}
		}
		return false;
		
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public RecordRoot getRoot(String src, HashMap<RecordRoot, HashMap<String, Long>> map) {
		if(map.isEmpty()) return null;
		for ( RecordRoot key : map.keySet()) {
			if(key.equals(src)) return key;
		}return null;
	}



	public static void testSave() {
		new TimeRecorder(TimeRecorder.defaultSource)
				.create()
				.addTime(new RecordRoot("Hoho", "hello"),   TimeRecorder.defaultSource.toPath())
				.addTime(new RecordRoot("Hoho", "hello"),   TimeRecorder.defaultSource.toPath())
				.save()
				.printPath()
		;
		
		RecordRoot a = new RecordRoot("Hoho", "hello");
		RecordRoot b = new RecordRoot("Hoho", "hello");
		System.out.println("a equals b : "+ a.equals(b));
	}

	public static void testFetch() {
		TimeRecorder recorder = new TimeRecorder(TimeRecorder.defaultSource)
				.fetch()
				.printFile()
		;
		System.out.println(recorder.isModified(new RecordRoot("Hello", "hello"), TimeRecorder.defaultSource.toPath()));
		var a = recorder.getRoot("Hello", recorder.fileMap);
		System.out.println("get : "+a);
		a.theme="lol";
		
		System.out.println("Modif : "+ recorder.getRoot("Hello", recorder.fileMap));
		
	}
}
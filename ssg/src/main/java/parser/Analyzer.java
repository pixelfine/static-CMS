package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import parser.extension.md.Extension;
import parser.extension.md.MarkdownToHtml;
import parser.extension.md.WikiToHtml;
import parser.html.HtmlFormat;
import parser.html.HtmlParser;
import parser.record.RecordRoot;
import parser.record.TimeRecorder;
import parser.toml.DefaultKey;
import parser.toml.TomlParser;


/**
 * Analyzer is the root of the parsing process. It handles the files location,
 * the generated files output location. It parse a Toml file format and an extension file format
 * to render it to a html file format
 */
public class Analyzer {
	public String path;
	public String rootPath;
	public String output;
	public String extension;
	public Metadata metadata;
	public TimeRecorder recorder;

	
	public Analyzer (Metadata metadata, String rootPath, String output, String filePath){
		this.path 		= filePath;
		this.rootPath	= rootPath;
		this.output		= output;
		this.extension 	= Analyzer.getFileExtension(path).orElse("");
		this.metadata	= metadata;
	}
	
	public Analyzer (Metadata metadata, String rootPath, String output, String filePath, TimeRecorder recorder){
		this(metadata, rootPath, output, filePath);
		this.recorder=recorder;
	}
	

	public synchronized void parse()  {
		TomlParser tomlParser = null;
		Extension ext		  = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
			if(readUntil("+++", reader)) {
				tomlParser = new TomlParser().parse(stringifyUntil("+++", reader));
				Boolean draft = tomlParser.getObject("draft", Boolean.class);
				if(draft!=null && draft) {
					reader.close();
					return;
				}else {
					this.metadata.add(tomlParser);			
					try {
						Builder.copyTheme(Builder.toTheme(rootPath, metadata).toPath(), new File(output).toPath());
					}catch(Exception e) {}
				}
			}
			ext = parseExtension(stringifyUntil("\u001a", reader));
			reader.close();
			if(recorder!=null) recorder.addTime(new RecordRoot(path, this.metadata.getContent("theme")), Paths.get(path));
			
			render(this.metadata, ext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void render(Metadata toml, Extension ext) throws FileNotFoundException {
		File template   = null;
		if(toml.getContent("template")!=null)       template = Builder.findTemplate(toml.getContent("template"), rootPath, metadata);
		HtmlFormat html = new HtmlFormat(
				new HtmlParser(template, toml, ext)
		);
		String name = /*template.getName();*/ new File(path).getName();
		
		try {
			Files.createDirectories(Paths.get(output));
		} catch (IOException e) {}
		System.out.println("Render at : "+new File(output+"/"+name).getPath()
				/*+"\n\twith template : "+template.getPath()+"\n\tMetadata="+toml.getMap()*/);
		RenderingProvider.render(html, output+name);
		if(template!=null) {
			RecordRoot root = new RecordRoot(path, this.metadata.getContent("theme"));
			html.getParseResult().includeSet.forEach(s->recorder.addTime(root, Paths.get(s)));
		}
	}

	protected Extension parseExtension(String input) {
		switch(extension) {
			case "md" : return new MarkdownToHtml(input);
			case "mw" : return new WikiToHtml(input);
			default : return new MarkdownToHtml(input);
		}
	}

	public static Optional<String> getFileExtension(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
	
	public static String convertExtToHtml(String filename) {
		return filename.substring(0, filename.lastIndexOf("."))+".html";
	}

	public boolean readUntil(String limit, BufferedReader reader) throws IOException {
		reader.mark(10000);
		String line= null;
		while((line = reader.readLine())!=null) {
			if(line.startsWith(limit)) return true;
		}
		reader.reset();
		return false;
	}

	private String stringifyUntil(String limit, BufferedReader reader) throws IOException {
		String line= null;
		StringBuilder builder = new StringBuilder();
		while((line = reader.readLine())!=null) {
			if(line.startsWith(limit)) return builder.toString();
			builder.append(line).append('\n');
		}return builder.toString();
	}
	
	/*
	private void copyTheme(RecordRoot root) {
		if(recorder!=null) {
			File theme = Builder.toTheme(rootPath, metadata);
			if(theme!=null) {
				FileManager.copyFolder        (Paths.get(theme.getAbsolutePath(), "/static/")   , Paths.get(output), false,FileManager.CopyMethod.HARDLINK);
				FileManager.copyModifiedFolder(Paths.get(theme.getAbsolutePath(), "/templates/"), Paths.get(output), true ,FileManager.CopyMethod.HARDLINK, recorder, root);
			}
		}
	}*/
}

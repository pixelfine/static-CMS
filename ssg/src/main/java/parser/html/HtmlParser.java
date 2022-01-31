package parser.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition;

import parser.Metadata;
import parser.Metadatable;
import parser.extension.md.Extension;
import parser.toml.DefaultKey;
import parser.toml.TomlParser;

public class HtmlParser {
	protected Metadata configuration;
	protected Extension ext;
	protected List<String> output;
	protected StringBuilder build;
	protected File file;
	
	protected HashSet<String> includeSet;
	
	public HtmlParser(File file, Metadata toml, Extension ext) {
		this.configuration=toml;
		this.ext=ext; 
		this.output=new ArrayList<String>();
		this.build =new StringBuilder();
		this.file=file;
		this.includeSet = new HashSet<String>();
	}
	
	private HtmlParser(File file, Metadata toml, Extension ext, HashSet<String> includeSet) {
		this(file, toml, ext);
		this.includeSet=includeSet;
	}
	
	public ParseInfo parse(){
		try {
			Lexer lexer;
			if(file!=null) {
				if(includeSet.contains(this.file.toString())) {
					System.err.println("Circular Include detected on : "+file.getAbsolutePath());
					return new ParseInfo(this.file, Arrays.asList("Circular Include detected on : "+file.getAbsolutePath()), this.configuration);
				}
				includeSet.add(this.file.toString());
				lexer = new Lexer(new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
			}else {
				lexer = new Lexer(new BufferedReader(new InputStreamReader(DefaultKey.class.getClassLoader().getResourceAsStream(DefaultKey.defaultTemplate), StandardCharsets.UTF_8)));
			}
			Token token = lexer.yylex();
			while(!token.symbol.equals(Sym.EOF)){
				interpretHtml(token, lexer);
				token=lexer.yylex();
			}
			output.add(build.toString());
			lexer.yyclose();
			
			applyJinjava();
			
			
			
			return new ParseInfo(file, output, new ArrayList<String>(), this.configuration, includeSet);
		} catch (IOException | LexerException e) {
			e.printStackTrace();
			return new ParseInfo(this.file, Arrays.asList(e.getMessage()), this.configuration);
		}
	}
	
	
	public void applyJinjava() {
		String template = "";
		Jinjava jinjava = new Jinjava();
		jinjava.getGlobalContext().registerFunction(
				new ELFunctionDefinition("", "list_files", 
						HtmlParser.class, "list_files", String.class, Boolean.class
			    )
		);
		for(String e : output) template+=e;
		Map<String, Object> context = configuration.getMap();
		
		String rendered = jinjava.render(template, context);
		
		
		output=new ArrayList<String>(Arrays.asList( new String[] {rendered} ));
		//System.out.println(context);
		
		
	}
	
	
	protected void interpretHtml(Token token, Lexer lexer) throws IOException, LexerException{
		while(!token.symbol().equals(Sym.EOF)) {
			switch(token.symbol()) {
			case OPEN  : interpret(token, lexer); break;
			case CLOSE : break;
			case EOPEN : build=append("{{"); break;
			case ECLOSE: build=append("}}"); break;
			default : 
				build = append(lexer.yytext()); break;
			}
			token = lexer.yylex();
		}
	}

	protected void interpret(Token token, Lexer lex) throws IOException, LexerException {
		switch(token.symbol) {
		case DATA	 : interpretData(token, lex); break;
		case OPEN 	 : interpretOpen(token, lex); break;
		case CLOSE	 : interpretHtml(token,lex); break;
		case CONTENT : interpretContent(token, lex); break;
		case INCLUDE : interpretInclude(token, lex); break;
		case META	 : interpretMeta(token, lex); break;
		case EOPEN   : interpretJinja(token, lex); break;
		case ECLOSE  : interpretJinja(token, lex); break;
		case EOF	 : break;
		default		 : break;
		}
	}
	
	protected void interpretJinja(Token token, Lexer lex)throws IOException, LexerException {
		switch(token.symbol) {
		case EOPEN  : build=append("{{"); break;
		case ECLOSE : build=append("}}"); break;
		default : break;
		}
	}
	
	protected void interpretData(Token token, Lexer lex) throws IOException, LexerException {
		//Code
		build = append(((StringToken)token).value());
		//End
		token=lex.yylex();
		switch(token.symbol) {
		case DATA	 : interpretData(token, lex); break;
		case OPEN 	 : interpretOpen(token, lex); break;
		case CLOSE	 : interpretHtml(token,lex); break;
		case CONTENT : interpretContent(token, lex); break;
		case META	 : interpretMeta(token, lex); break;
		case INCLUDE : interpretInclude(token, lex); break;
		case EOPEN  : build=append("{{"); break;
		case ECLOSE : build=append("}}"); break;
		case EOF	 : break;
		default		 : break;
		}
	}
	
	protected void interpretOpen(Token token, Lexer lex) throws IOException, LexerException {
		//Code
		//End
		token = lex.yylex();
		switch(token.symbol) {
		case DATA	: interpretData(token, lex);		break;
		case CONTENT: interpretContent(token, lex); 	break;
		case META	: interpretMeta(token, lex);	   	break;
		case INCLUDE: interpretInclude(token, lex);		break;
		case EOPEN  : build=append("{{"); break;
		case ECLOSE : build=append("}}"); break;
		default		: break;
		}
	}
	
	protected void interpretContent(Token token, Lexer lex) throws IOException, LexerException {
		//Code
		if(ext!=null) {
			appendNew(ext.translate());
		}
		//End
		token = lex.yylex();
		switch(token.symbol) {
		case DATA	: interpretData(token, lex);		break;
		case CLOSE	: interpretHtml(token, lex); 		break;
		case EOPEN  : build=append("{{"); break;
		case ECLOSE : build=append("}}"); break;
		default		: break;
		}
	}
	
	
	protected void interpretMeta(Token token, Lexer lex) throws IOException, LexerException {
		//Code
		if(configuration!=null) {
			String key = (((StringToken)token).value()).trim().replaceFirst("^metadata.", "");
			appendNew(configuration.getContent(key));
		}
		//End
		token = lex.yylex();
		switch(token.symbol) {
		case DATA	: interpretData(token, lex);		break;
		case CLOSE	: interpretHtml(token, lex); 		break;
		case EOPEN  : build=append("{{"); break;
		case ECLOSE : build=append("}}"); break;
		default		: break;
		}
	}
	
	protected void interpretInclude(Token token, Lexer lex) throws IOException, LexerException {
		//Code
		interpretIncludeData(  ((StringToken)token).value()   );
		//appendNew(readerToString(((StringToken)token).value()));
		//End
		token = lex.yylex();
		switch(token.symbol) {
		case DATA	: interpretData(token, lex);		break;
		case CLOSE	: interpretContent(token, lex); 	break;
		case EOPEN  : build=append("{{"); break;
		case ECLOSE : build=append("}}"); break;
		default		: break;
		}
	}
	
	
	
	
	private StringBuilder append(String data) {
		if(build.length()<Integer.MAX_VALUE) return build.append(data);
		output.add(build.toString());
		return new StringBuilder();
	}
	
	private void appendNew(String data) {
		output.add(build.toString());
		output.add(data);
		build = new StringBuilder();
	}
	public void interpretIncludeData(String src) {
		this.output.add(this.build.toString());
		String srcFile = file.getParent()+"/"+src.substring(src.indexOf("\"") + 1,  src.lastIndexOf("\""));
		File includeFile = new File(srcFile);
		if(!includeFile.exists()) {
			System.err.println("Include file doesn't exist :"+srcFile);
			return;
		}
		HtmlParser nextInclude = new HtmlParser(includeFile, this.configuration, this.ext, this.includeSet);
		nextInclude.parse();
		this.output.addAll(
				nextInclude.getOutput()		
		);
		this.build=new StringBuilder();
	}
	/*
	public String readerToString(String src) {
		String srcFile = file.getParent()+"/"+src.substring(src.indexOf("\"") + 1,  src.lastIndexOf("\""));
		InputStream input;
		try {
			input = new FileInputStream(new File(srcFile));
			return CharStreams.toString(new InputStreamReader(input, StandardCharsets.UTF_8));
		} 
		catch (FileNotFoundException e) {System.out.println("File :"+new File(srcFile).getPath());}
		catch (IOException e1) {}	
		return "";
	}
	*/
	public void printOutput() {
		output.forEach(s->{System.out.println(s);});
	}
	
	public List<String> getOutput(){
		return this.output;
	}
	
	public void toFile(String path) throws IOException {
	    File file = new File(path);
	    CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
	    sink.writeLines(output);;
	}
	
	public static List<String> list_files(String path, Boolean rec) {
		ArrayList<String> paths = new ArrayList<String>();
		Path p = Paths.get(path);
		File folder = p.toFile();
		
		if(!rec) {
			File[] filesList = folder.listFiles();
			for(File file : filesList) {
				if(!file.isDirectory()) paths.add(  file.getPath().substring(p.toString().length()) );
			}
		}else {
			for (File file: Files.fileTraverser().breadthFirst(folder)){
				if(!file.isDirectory()) paths.add(  file.getPath().substring(p.toString().length()) );
			}
		}
		
		
		return paths;
	}
	
	

}

package parser.html;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import parser.Metadata;
import parser.Metadatable;

public class ParseInfo {
	public File file;
	public List<String> output;
	public List<String> errors;
	public Metadata configuration;
	public HashSet<String> includeSet;
	
	public ParseInfo(File file, List<String> errors, Metadata configuration) {
		this.file=file;
		this.errors=errors;
		this.configuration=configuration;
	}
	
	public ParseInfo(File file, List<String> output, List<String> errors, Metadata configuration, HashSet<String> includeSet) {
		this(file, errors, configuration);
		this.output=output;
		this.includeSet=includeSet;
	}
	
}

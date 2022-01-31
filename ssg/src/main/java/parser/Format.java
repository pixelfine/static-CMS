package parser;

import java.util.List;

import parser.html.ParseInfo;

public interface Format {
	public List<String> prepare();
	public String getExtension();
	public ParseInfo getParseResult();
}

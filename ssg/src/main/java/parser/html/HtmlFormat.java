package parser.html;

import java.util.List;

import parser.Format;

public class HtmlFormat implements Format{
	public HtmlParser html;
	private ParseInfo info;
	public HtmlFormat(HtmlParser html) {
		this.html=html;
	}

	@Override
	public List<String> prepare() {
		info = html.parse();
		return html.getOutput();
	}
	
	

	@Override
	public String getExtension() {
		return "html";
	}

	@Override
	public ParseInfo getParseResult() {
		return this.info;
	}
	
}

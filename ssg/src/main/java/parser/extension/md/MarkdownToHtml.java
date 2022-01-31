package parser.extension.md;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownToHtml extends Extension{
	private Node document;

	public MarkdownToHtml(String data) {
		super(data);
		this.document = Parser.builder().build().parse(data);
	}

	@Override
	public String translate() {
		return HtmlRenderer.builder().build().render(document);
	}

}

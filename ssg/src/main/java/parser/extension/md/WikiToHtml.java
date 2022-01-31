package parser.extension.md;

import info.bliki.wiki.model.WikiModel;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class WikiToHtml extends Extension{
	private Node document;

	public WikiToHtml(String data) {
		super(data);
		this.document = Parser.builder().build().parse(WikiModel.toHtml(data));
	}

	@Override
	public String translate() {
		return HtmlRenderer.builder().build().render(document);
	}

}
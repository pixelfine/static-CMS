package parser.html;

public class StringToken extends Token {
    private String value;
    public StringToken(Sym c, String v) {
		super(c);
		value=v;
    }
    public String value() {
    	return value;
    }
    public String toString(){
    	return "Symbol : "+this.symbol+" | Value : "+this.value();
    }
}
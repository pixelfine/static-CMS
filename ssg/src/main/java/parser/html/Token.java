package parser.html;

public class Token {
	protected Sym symbol;
    public Token(Sym s) {
    	symbol=s;
    }
    public Sym symbol() {
    	return symbol;
    }
    public String toString(){
    	return "Symbol : "+this.symbol;
    }
}
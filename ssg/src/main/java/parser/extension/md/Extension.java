package parser.extension.md;

public abstract class Extension {
	protected String data;
	public Extension(String data){
		this.data=data;
	}
	
	public abstract String translate();
	
	/*Return a copy of the string to translate*/
	public String getData() {
		return data;
	}
}

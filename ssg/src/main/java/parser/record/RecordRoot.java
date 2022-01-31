package parser.record;

import java.io.Serializable;

public class RecordRoot implements Serializable, Comparable<String>{
	private static final long serialVersionUID = 1L;
	public String extension;
	public String theme;
	public RecordRoot(String extension, String theme) {
		this.extension=extension;
		this.theme=theme;
	}
	
	@Override
	public int compareTo(String o) {
		return this.extension.compareTo(o);
	}
	
	@Override
	public int hashCode() {
		return this.extension.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof RecordRoot)
			return extension.equals(((RecordRoot)o).extension);
		else return extension.equals(o);
	}
	
	@Override
	public String toString() {
		return this.extension+" "+this.theme;
	}
}

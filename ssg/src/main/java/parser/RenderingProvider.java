package parser;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class RenderingProvider {
	public static File render(Format format, String filepath) {
	    try {
	    	StringBuilder path = new StringBuilder(filepath.substring(0, filepath.lastIndexOf('.')+1));
		    path.append(format.getExtension());
	    	File file = new File(path.toString());
	    	Files.asCharSink(file, Charsets.UTF_8).writeLines(format.prepare());
	    	return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return null;
	}
}

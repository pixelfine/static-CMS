package parser.toml;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import app.App;
import parser.Metadatable;


/**
 * Class defining a static default key used to map a default configuration
 * It has a default key stored on initialization : 
 * <ul>
 * <li> "title" : "Titre"
 * <li> "draft" : false
 * <li> "date"  : "10-20-2000"
 * </ul>
 */
public class DefaultKey implements Metadatable{
	public static HashMap<String, Object> metadataMap = new HashMap<String, Object>();
	public static String defaultTemplate = "template/default.html";
	static {
		metadataMap.put("title", new String("Title"));
		metadataMap.put("theme", null);
		metadataMap.put("draft", false);
		metadataMap.put("date" , new String("10-20-2000"));
	}
	
	public static boolean isSameType(String key, Map<String, Object> map) {
		if(metadataMap.containsKey(key) && map.containsKey(key)) {
			return metadataMap.get(key).getClass().equals(map.get(key).getClass());
		}return false;
	}
	
	public static String absolutePath(String path) {
		File file = new File(path);
		return file.getAbsolutePath();
	}

	@Override
	public String getContent(String key) {
		var data = metadataMap.getOrDefault(key, null);
		if(data == null) return null;
		return data.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String str, Class<T> data) {
		var item = metadataMap.getOrDefault(str, null);
		if(item!=null && data.isInstance(item)) {
			return (T) item;
		}else return null;
	}

	@Override
	public Map<String, Object> getMap() {
		return DefaultKey.metadataMap;
	}

}
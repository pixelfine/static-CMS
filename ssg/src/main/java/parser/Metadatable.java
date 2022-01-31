package parser;

import java.util.Map;

public interface Metadatable {
	public String getContent(String key);
	public <T> T getObject(String str, Class<T> data);
	public Map<String, Object> getMap();
}

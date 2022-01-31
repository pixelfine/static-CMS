package parser.toml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import parser.Metadatable;

/**
 * TomlParser is the base class for parsing a toml data to store it on a map.
 * It has a default key stored on initialization : 
 * <ul>
 * <li> "title" : "Titre"
 * <li> "draft" : false
 * <li> "date"  : "10-20-2000"
 * </ul>
 * <p>It maps the metadata by calling {@link #parse()}</p>
 * <p>Access to mapped data is possible by calling {@link #getObject(String, Class)} </p>
 */
public class TomlParser implements Metadatable{
	private TomlParseResult result;
	
	/**
	 * parse the current metadata and store them in a map
	 */
	public TomlParser parse(String metadata) {
		result = Toml.parse(metadata);
		result.errors().forEach(error -> System.err.println(error.toString()));
		return this;
	}
	
	public TomlParser parse(Path metadata) {
		try {
			result = Toml.parse(metadata);
			result.errors().forEach(error -> System.err.println(error.toString()));
			return this;
		} catch (IOException e) {
			return null;
		}
	}
	
	
	
	/**
	 * --get the specified object from the map and convert it to String, return null on fail
	 * @param str		key of the mapped object
	 * @return 			<code>str</code> on success, <code>null</code> on fail
	 * <p> Usage example : </p>
	 * <pre>
	 * {@code 
	 * 	String a = instance.getObject("draft", Boolean.class);
	 *	if(a != "") 	System.out.println("not null !");
	 *	else 		System.out.println("null");
	 * }
	 * </pre>
	 */
	
	@Override
	public String getContent(String str) {
		var data = getObject(splitDot(str));
		if(data==null) return null;
		return data.toString();
	}
	

	/**
	 * --get the specified object from the map, return null on fail
	 * @param str		key of the mapped object
	 * @param data		class of the mapped type
	 * @return 			<code>T</code> on success, <code>null</code> on fail
	 * <p> Usage example : </p>
	 * <pre>
	 * {@code 
	 * 	var a = instance.getObject("draft", Boolean.class);
	 *	if(a != null) 	System.out.println("not null !");
	 *	else 		System.out.println("null");
	 * }
	 * </pre>
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String str, Class<T> data) {
		var item = getObject(splitDot(str));
		if(item!=null && data.isInstance(item)) {
			return (T) item;
		}else return null;
	}
	
	
	
	protected Object getObject(List<String> path) {
		List<String> keys = splitArray(path.get(0));
		
		var value = result.get(keys.get(0));
		
		if(value instanceof TomlTable) {
			path.remove(0);
			return getObjectFrom((TomlTable)value, path);
		}else if(value instanceof TomlArray) {
			return getObjectFrom((TomlArray)value, path);
		}else if(path.size()<=1)	return value;
		
		return null;
	}
	
	protected Object getObjectFrom(TomlTable toml, List<String> path) {
		List<String> key = splitArray(path.get(0));
		var value = toml.get(key.get(0));
		
		if(value instanceof TomlArray) {
			path.remove(0);
			return getObjectFrom((TomlArray)value, path);
		}else if(value instanceof TomlTable) {
			return getObjectFrom((TomlTable)value, path);
		}else if(path.size()<=1) {
			return value;
		}
		//error : (case e.g) metadata = fruit.banana, path = fruit.banana.yellow
		return null;
	}
	
	
	
	/*  BROUILLON
	 * 
	 * Map<String, [Map<String, String>]>
	 * [[fruit]]
	 * name = banana
	 * -----------------------
	 * fruit[0].banana = "yellow"
	 * path = [fruit[0], banana]
	 * 
	 */
	protected Object getObjectFrom(TomlArray toml, List<String> path) {
		List<String> key = splitArray(path.get(0));
		if(key.size()==1) return toml;
		try {
			List<Integer> indexList = getArrayIndex(key);
			for(int i = 0; i<indexList.size()-1; i++) {
				toml = toml.getArray(indexList.get(i));
			}
			var value = toml.get(indexList.get(indexList.size()-1));
			
			if(value instanceof TomlArray) {
				path.remove(0);
				return getObjectFrom((TomlArray)value, path);
			}else if(value instanceof TomlTable) {
				path.remove(0);
				return getObjectFrom((TomlTable)value, path);
			}else if(path.size()<=1) return value;
			return null;
		}catch(Exception e) {
			System.err.println("Invalid index format");
			return null;
		}
	}
	
	protected Object getTomlArrayValue(List<Integer> path, TomlArray array) {
		TomlArray arr = array;
		for(int i=0; i<path.size()-1; i++) {
			arr = arr.getArray(path.get(i));
		}return arr.get( path.get(path.size()-1) );
	}
	
	
	private List<String> splitDot(String str) {
		return Arrays.asList( str.split("\\.") )
				.stream()
				.map(String::trim)
				.collect(Collectors.toList());
	}
	
	private List<String> splitArray(String str){
		return Arrays.asList(  str.split("[\\[\\]]"))
				.stream()
				.filter(s->!s.isEmpty())
				.collect(Collectors.toList());
	}
	
	private List<Integer> getArrayIndex(List<String> splitArray) throws Exception{
		ArrayList<Integer> lst = new ArrayList<Integer>();
		for(int i=1; i<splitArray.size(); i++) {
			lst.add( Integer.valueOf( splitArray.get(i) )  );
		}return lst;
	}

	@Override
	public Map<String, Object> getMap() {
		Map<String, Object> map = result.toMap();
		Map<String, Object> res = new HashMap<String, Object>();
		
		
		for(Entry<String, Object> entry : map.entrySet()) {
			if(entry.getValue() instanceof TomlTable) {
				res.put(entry.getKey(), tomlTabletoMap ((TomlTable) entry.getValue(), new HashMap<String, Object>()));
			}else if(entry.getValue() instanceof TomlArray) {
				res.put(entry.getKey(),  tomlArraytoMap((TomlArray)entry.getValue()));
			}else {
				res.put(entry.getKey(), entry.getValue());
			}
		}
		return res;
	}

	
	protected Map<String, Object> tomlTabletoMap(TomlTable toml, Map<String, Object> map){
		for(String key :  toml.keySet() ) {
			var value = toml.get(key);
			if(value instanceof TomlTable) {
				map.put(key,  tomlTabletoMap((TomlTable)value, new HashMap<String, Object>()));
			}else if(value instanceof TomlArray) {
				map.put(key,  tomlArraytoMap((TomlArray)value));
			}
			else {
				map.put(key, value);
			}
		}
		return map;
	}
	
	
	protected List<Object> tomlArraytoMap(TomlArray value) {
		List<Object> lst = new ArrayList<Object>();
		for(int i=0; i<value.size(); i++) {
			var val = value.get(i);
			if(val instanceof TomlTable) {
				lst.add( tomlTabletoMap((TomlTable) val, new HashMap<String, Object>()) );
			}else if(val instanceof TomlArray) {
				lst.add( tomlArraytoMap((TomlArray) val)  );
			}else {
				lst.add(val);
			}
		}
		return lst;
	}
	
}

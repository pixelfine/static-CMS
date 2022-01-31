package parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import parser.toml.DefaultKey;

public class Metadata{
	protected List<Metadatable> datas;
	
	
	public Metadata() {
		this.datas = Collections.synchronizedList(new LinkedList<Metadatable>());
		this.datas.add(new DefaultKey());
	}
	public Metadata(Metadata other) {
		this.datas=Collections.synchronizedList(new LinkedList<Metadatable>(other.datas));
	}
	
	
	public void add(Metadatable data) {
		datas.add(0, data);
	}
	
	public String getFromLastContent(String key) {
		String res ="";
		for(Metadatable e : Lists.reverse(datas)) {
			res = e.getContent(key);
			if(e.getContent(key)!=null) return res;
		}return res;
	}
	
	public int size() {
		return datas.size();
	}
	

	public String getContent(String key) {
		String res ="";
		for(Metadatable e : datas) {
			res = e.getContent(key);
			if(e.getContent(key)!=null) return res;
		}return res;
	}

	public <T> T getObject(String str, Class<T> data) {
		T obj = null;
		for(Metadatable e : datas) {
			obj = e.getObject(str, data);
			if(obj!=null && data.isInstance(obj)) return (T) obj;
		}return obj;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for(Metadatable e : datas) {
			e.getMap().forEach(map::putIfAbsent);
		}
		return map;
	}
}

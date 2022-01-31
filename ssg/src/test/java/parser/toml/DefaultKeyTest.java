package parser.toml;

import org.junit.jupiter.api.Test;
import parser.Metadata;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DefaultKeyTest {
    DefaultKey defaultKey = new DefaultKey();

    @Test
    public void isSameType() {
        HashMap<String, Object> mapString1 = new HashMap<String, Object>();
        HashMap<String, Object> mapString2 = new HashMap<String, Object>();
        HashMap<String, Object> mapInteger = new HashMap<String, Object>();

        mapString1.put("title", new String("Title"));
        mapString2.put("title", new String("Test"));
        mapInteger.put("title", 1);

        assertTrue(DefaultKey.isSameType("title", mapString1), "Test with same value and type");
        assertTrue(DefaultKey.isSameType("title", mapString2), "Test with same type");
        assertFalse(DefaultKey.isSameType("title", mapInteger), "Test with different value and type");
    }

    @Test
    public void absolutePath() {
        assertNotNull(DefaultKey.absolutePath(""), "Check that absolutePath isn't returning null");
    }

    @Test
    public void testGetContent() {
        assertEquals("Title", defaultKey.getContent("title"), "");
        //assertNull(defaultKey.getContent("test"), "Try to get content from non-existent key");
    }

    @Test
    public void getObject() {
        assertEquals("Title", defaultKey.getObject("title", String.class), "Get String from 'title' key");
        assertNull(defaultKey.getObject("title", Integer.class), "Get Integer from 'title' key");
        //assertNull(defaultKey.getObject("test", String.class), "Get String from non-existent key");
    }
}

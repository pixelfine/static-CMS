package parser;

import org.junit.jupiter.api.Test;
import parser.toml.DefaultKey;

import static org.junit.jupiter.api.Assertions.*;

class MetadataTest {
    Metadata metadata = new Metadata();

    @Test
    public void testAdd(){
        metadata.add(new DefaultKey());
        assertEquals(2, metadata.size(), "Test size after adding 1 element");
    }

    @Test
    public void testGetFromLastContent() {
        assertEquals("Title", metadata.getFromLastContent("title"), "Is 'Title' is last content for 'title' key");
        assertNotEquals("", metadata.getFromLastContent("title"), "Equality with wrong String for 'title' key");
        DefaultKey.metadataMap.put("test", new String("TEST"));
        DefaultKey.metadataMap.put("test", new String("Test"));
        assertEquals("Test", metadata.getFromLastContent("test"), "Test if last content is correctly 'Test' with 'test' key");
        assertEquals("Test", metadata.getFromLastContent("test"), "Check that last content didn't change for 'test' key");
    }

    @Test
    public void testSize() {
        assertEquals(1, metadata.size());
        assertNotEquals(0, metadata.size(), "Test if size is 0");
        assertNotEquals(2, metadata.size(), "Test if size is 2");

        for(int i = 0; i < 10; i++) metadata.add(new DefaultKey());
        assertEquals(11, metadata.size(), "Test size after adding 10 elements");
    }

    @Test
    public void testGetContent() {
        assertEquals("Title", metadata.getContent("title"), "'Title' is content of 'title'");
        assertNull(metadata.getContent("theme"), "null is content of 'theme'");
        assertEquals("10-20-2000", metadata.getContent("date"), "'10-20-2000' is content of 'date'");
        assertNotEquals("", metadata.getContent("title"), "Equality with wrong String for 'title' key");

        DefaultKey.metadataMap.put("test", new String("TEST"));
        assertEquals("TEST", metadata.getContent("test"), "'TEST' is content of 'test'");
        assertNull(metadata.getContent("Test"), "Try to get content from non-existent 'Test' key");
    }

    @Test
    public void testGetObject() {
        assertEquals("Title", metadata.getObject("title", String.class), "Get String from 'title' key");
        assertEquals(false, metadata.getObject("draft", Boolean.class), "Get boolean from 'draft' key");
        assertNull(metadata.getObject("title", Integer.class), "Try to get Integer from 'title' key"); //should be null(?) but get a value

        DefaultKey.metadataMap.put("test", new String("TEST"));
        assertEquals("TEST", metadata.getObject("test", String.class), "Test with equals String");
        assertNull(metadata.getObject("Test", String.class), "Try to get String from non-existent key");
        assertNull(metadata.getObject("test", Boolean.class), "Try to get boolean from 'test' key whereas 'test' contains String value");
    }
}
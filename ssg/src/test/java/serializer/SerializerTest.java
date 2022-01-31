package serializer;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;
import java.io.File;
import java.util.Optional;

import serializer.Serializer;

/**
 * Tests for Serializer
 * @author Lucian Petic
 */
public class SerializerTest {

    private static class A implements Serializable {

        public String a;
        public int b;
        public double c;
        private static final long serialVersionUID = 5373035251391293536L;

        public A(String a, int b, double c){
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    @Test()
    public void testSerializerSaveAndLoadOk() {
        String path = "./test/test.a.data";
        A a = new A("String", 1, 3.1415);
        boolean stateSave = Serializer.save(a, path);
        Optional<A> optional = Serializer.<A>load(path);

        assertTrue(stateSave, "Serialization: save faild");
        assertTrue(optional.isPresent(), "Serialization: object not present");
        assertEquals(a.a, optional.get().a, "Serialization: load field 'a' not the same");
        assertEquals(a.b, optional.get().b, "Serialization: load field 'b' not the same");
        assertEquals(a.c, optional.get().c, "Serialization: load field 'c' not the same");

        new File(path).delete();
    }

    @Test()
    public void testSerializerLoadKo() {
        Optional<A> optional = Serializer.<A>load("./test/test.b.data");
        assertFalse(optional.isPresent(), "Serialization: object should be empty");
    }
}

package parser.html;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class StringTokenTest {

    @ParameterizedTest
    @ValueSource(strings = {"null", "", "\\", "\n", "\ttest"})
    public void testValue(String string) {
        StringToken stringToken = new StringToken(Sym.OPEN, string);
        assertEquals(string, stringToken.value(), "Test with equals String");
        assertNotEquals("test", stringToken.value(), "Test with non-equals String");
    }

    @ParameterizedTest
    @EnumSource(Sym.class)
    public void testToString(Sym sym){
        StringToken stringToken = new StringToken(sym, "test");
        String string = "Symbol : "+sym.name()+" | Value : test";
        assertEquals(string, stringToken.toString(), "Test with equals String");
        assertNotEquals("Symbol : TEST | Value : test", stringToken.toString(), "Test with non-equals String");
    }
}
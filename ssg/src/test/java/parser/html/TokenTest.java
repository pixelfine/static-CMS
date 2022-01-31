package parser.html;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    @ParameterizedTest
    @EnumSource(Sym.class)
    public void testSymbol(Sym sym) {
        Token token = new Token(sym);
        assertEquals(sym, token.symbol(), "Test if symbols are equals");
    }

    @ParameterizedTest
    @EnumSource(Sym.class)
    public void testToString(Sym sym) {
        Token token = new Token(sym);
        assertEquals("Symbol : "+sym.name(), token.toString(), "Test if Strings are equals");
    }
}
package console;

import console.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Console
 * @author Lucian Petic
 */
public class ConsoleTest {

    public static Console console = Console.getInstance();

    @Test()
    public void testHasCommandOk() {
        assertTrue(console.hasCommand("help").isPresent(), "help command is not present");
        assertTrue(console.hasCommand("build").isPresent(), "build command is not present");
        assertTrue(console.hasCommand("serve").isPresent(), "serve command is not present");
    }

    @Test()
    public void testHasCommandKo() {
        assertFalse(console.hasCommand("incorrect").isPresent(), "incorrect command is present");
    }
}

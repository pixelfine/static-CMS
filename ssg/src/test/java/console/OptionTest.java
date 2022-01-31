package console;

import console.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Option
 * @author Lucian Petic
 */
public class OptionTest {

    @Test()
    public void testCreateOptionWithArg() {
        Option option = new Option("--input-dir", true);
        assertTrue(option.getNeedArg(), "Arg is required.");
    }

    @Test()
    public void testCreateOptionWithoutArg() {
        Option option = new Option("--rebuild-all");
        assertFalse(option.getNeedArg(), "Arg is not required.");
    }

    @Test()
    public void testSameName() throws Exception {
        Option option1 = new Option("--input-dir", true);
        Option option2 = new Option("--input-dir");
        assertTrue(option1.sameName(option2), "Not same names.");
    }

    @Test()
    public void testContainsOk() throws Exception {
        Option option = new Option("--input-dir");
        List <Option> list = List.of(
                new Option("--input-dir", true),
                new Option("--output-dir", true)
        );
        assertTrue(Option.contains(list, option), "Option not found in the list.");
    }

    @Test()
    public void testContainsKo() throws Exception {
        Option option = new Option("--incorrect");
        List <Option> list = List.of(
                new Option("--input-dir", true),
                new Option("--output-dir", true)
        );
        assertFalse(Option.contains(list, option), "Option not found in the list.");
    }
}

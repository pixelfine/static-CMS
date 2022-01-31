package console;

import console.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Commands
 * @author Lucian Petic
 */
public class CommandTest {

    @Test()
    public void testCreateCommandWithoutOptions() {
        CommandUser commandUser = new CommandUser(ConsoleTest.console, new String[]{"help"});
        assertTrue(commandUser.getName().equals("help"), "Command name is not the same.");
        assertTrue(commandUser.run(), "Launch command not working");
    }

    @Test()
    public void testCreateCommandWithOptions() {
        CommandUser commandUser = new CommandUser(ConsoleTest.console, new String[]{"build", "--output-dir", "exportFolder", "arg1", "arg2"});

        Option option = commandUser.getOptions().get(0);
        assertEquals(option.getName(), "--output-dir", "Option name not the same");
        assertEquals(option.getNeedArg(), true, "Arg is required");
        assertEquals(option.getArg().get(), "exportFolder", "Not same name for option argument");
        assertTrue(commandUser.getArgs().equals(List.of("arg1", "arg2")), "Args not the same");
    }

    @Test()
    public void testCreateCommandWithOptions2() {
        CommandUser commandUser = new CommandUser(ConsoleTest.console, new String[]{"build", "--input-dir", "importFolder", "--output-dir", "exportFolder"});

        Option option1 = commandUser.getOptions().get(0);
        assertEquals(option1.getName(), "--input-dir", "Option name not the same");
        assertEquals(option1.getNeedArg(), true, "Arg is required");
        assertEquals(option1.getArg().get(), "importFolder", "Not same name for option argument");

        Option option2 = commandUser.getOptions().get(1);
        assertEquals(option2.getName(), "--output-dir", "Option name not the same");
        assertEquals(option2.getNeedArg(), true, "Arg is required");
        assertEquals(option2.getArg().get(), "exportFolder", "Not same name for option argument");

        assertTrue(commandUser.getArgs().equals(List.of()), "Args not the same");
    }

    @Test()
    public void testCreateCommandWithOptions3() {
        CommandUser commandUser = new CommandUser(ConsoleTest.console, new String[]{"build", "--input-dir", "importFolder", "--output-dir", "exportFolder", "--rebuild-all"});

        Option option = commandUser.getOptions().get(2);
        assertEquals(option.getName(), "--rebuild-all", "Option name not the same");
        assertEquals(option.getNeedArg(), false, "Arg is required");
        assertEquals(option.getArg().isPresent(), false, "Arg should not be present");

        assertTrue(commandUser.getArgs().equals(List.of()), "Args not the same");
    }

    @Test()
    public void testRunCommandFail() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CommandUser(ConsoleTest.console, new String[]{});
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CommandUser(ConsoleTest.console, null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CommandUser(ConsoleTest.console, new String[]{"incorrect"});
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CommandUser(ConsoleTest.console, new String[]{"build", "--input-dir"});
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CommandUser(ConsoleTest.console, new String[]{"help", "--input-dir"});
        });
    }
}

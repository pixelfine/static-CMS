package console;

import java.util.*;

/**
 * This class is default class for defined commands
 * @author Lucian Petic
 */
public class Console {

    /**
     * List of commands avaible
     */
    private List<Command> commands;

    /**
     * Singleton for the console
     */
    private static Console INSTANCE = null;

    private Console(Command ... commands){
        this.commands = List.of(commands);
    }

    /**
     * Get instance method
     * @return
     */
    public static Console getInstance() {
        if (INSTANCE == null){
            synchronized(Console.class){
                if (INSTANCE == null){
                    INSTANCE = new Console(
                            new Command("build",
                                new Option("--input-dir", true),
                                new Option("--output-dir", true),
                                new Option("--jobs", true),
                                new Option("--rebuild-all"),
                                new Option("--watch")
                            ),
                            new Command("serve",
                                    new Option("--port", true),
                                    new Option("--input-dir", true),
                                    new Option("--output-dir", true),
                                    new Option("--jobs", true),
                                    new Option("--watch")
                            ),
                            new Command("help")
                    );
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Check if command exists or not
     * @param command
     * @return
     */
    public Optional<Command> hasCommand(String command){
        return this.commands
                .stream()
                .filter(cmd -> cmd.getName().equals(command))
                .findFirst();
    }
}

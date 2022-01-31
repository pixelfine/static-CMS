package console;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

/**
 * Command class
 * Base class for commands
 * @author Lucian Petic
 */
public class Command {

    /**
     * Name of the command
     */
    protected String name;

    /**
     * Avaible options
     */
    protected List<Option> options;

    /**
     * Constructors for command
     * @param name
     */
    public Command(String name){
        this(name, new Option[]{});
    }

    public Command(String name, Option ... options){
        this.name = name;
        this.options = Arrays.asList(options);
    }

    public Command(){
        // This empty constructor is important.
    }

    /**
     * Check if command has an option
     * @param option
     * @return
     */
    public Optional<Option> hasOption(String option){
        return this.options
                .stream()
                .filter(opt -> opt.getName().equals(option))
                .findFirst();
    }

    /**
     * Getter name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Getter options
     * @return
     */
    public List<Option> getOptions() {
        return options;
    }
}

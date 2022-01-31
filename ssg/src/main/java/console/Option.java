package console;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Option for a command
 * Option is liked directly with arguments if it is required.
 * @author Lucian Petic
 */
public class Option {

    /**
     * Name of the option
     */
    private final String name;

    /**
     * Is the arg required
     */
    private final boolean needArg;

    /**
     * Argument
     */
    private Optional<String> arg;

    /**
     * Constructors for option
     * @param name
     * @param needArg
     */
    public Option(String name, boolean needArg){
        this.name = name;
        this.needArg = needArg;
        this.arg = Optional.empty();
    }

    public Option(String name){
        this(name, false);
    }

    /**
     * Check if names are the same
     * @param option
     * @return
     */
    public boolean sameName(Option option){
        return this.name.equals(option.name);
    }

    /**
     * Check if the option exists
     * @param optionList
     * @param option
     * @return
     */
    public static boolean contains(List<Option> optionList, Option option){
        return 1 == optionList
                .stream()
                .filter(o -> o.sameName(option))
                .count();
    }

    /**
     * Getter need arg
     * @return
     */
    public boolean getNeedArg(){
        return this.needArg;
    }

    /**
     * Getter name
     * @return
     */
    public String getName(){
        return this.name;
    }

    /**
     * Getter arg
     * @return
     */
    public Optional<String> getArg() {
        return arg;
    }

    /**
     * Setter arg
     * @param arg
     * @return
     */
    public Option setArg(String arg) {
        this.arg = Optional.of(arg);
        return this;
    }
}

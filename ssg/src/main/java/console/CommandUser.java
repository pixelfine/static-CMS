package console;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import app.App;

/**
 * CommandUser is a command given by the user
 * When a object is create we check with avaible default commands from the console
 * This class contains magique invoke later function
 * @author Lucian Petic
 */
public class CommandUser extends Command {

    /**
     * Complementary args
     */
    private List<String> args;

    /**
     * Constructor for an user command
     * @param console
     * @param inputs
     */
    public CommandUser(Console console, String inputs[]){
        if(inputs == null || inputs.length == 0)
            throw new IllegalArgumentException("Array is empty.");

        Optional<Command> command = console.hasCommand(inputs[0]);

        if(command.isEmpty()) throw new IllegalArgumentException("Command was not found.");
        else this.name = inputs[0];

        this.options = new ArrayList<Option>();
        this.args = new ArrayList<String>();

        // Options with args are linked directly
        int indexArgs = 0;
        for (int i = 1; i < inputs.length; i++) {
            if(!inputs[i].startsWith("--")) break;
            Optional<Option> option = command.get().hasOption(inputs[i]);
            if(option.isPresent()){
                indexArgs = i;
                Option newOption = new Option(inputs[i], option.get().getNeedArg());
                try{
                    if(option.get().getNeedArg()) {
                        indexArgs = i + 1;
                        newOption.setArg(inputs[i+1]);
                        i++;
                    }
                    this.options.add(newOption);
                }catch (Exception e){
                    throw new IllegalArgumentException("Not enouth arguments.");
                }
            }else{
                throw new IllegalArgumentException("Option does't exists.");
            }
        }

        // Init other args
        for (int i = indexArgs + 1; i < inputs.length; i++) {
            this.args.add(inputs[i]);
        }
    }

    /**
     * Run the command
     * @return
     */
    public boolean run(){
        try{
            Method method = App.class.getMethod(this.name, CommandUser.class);
            method.invoke(App.class, this);
            return true;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Getter for args
     * @return
     */
    public List<String> getArgs() {
        return args;
    }
}

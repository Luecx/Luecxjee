package luecx.server.protocol.commands;

import luecx.server.protocol.exceptions.ArgumentException;
import luecx.server.protocol.exceptions.CommandException;
import luecx.server.tcp.connection.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by finne on 27.01.2018.
 */
public class Command {

    private final String name;
    private final String description;
    private Map<String, Argument> arguments = new WeakHashMap<>();
    private Executable executable;

    public Command(String name) {
        this.name = name.toLowerCase();
        this.description = "";
    }

    public Command(String name, String description) {
        this.name = name.toLowerCase();
        this.description = description;
    }

    public Command registerArgument(Argument argument) {
        arguments.put(argument.getName().toLowerCase(), argument);
        return this;
    }

    public void execute(Connection<?> con, String[] argumentValues) throws Exception{
        for(int i = 0; i < argumentValues.length; i++) {
            String[] args = argumentValues[i].trim().split(" ");
            Argument argument = getArgument(args[0]);
            String[] values = Arrays.copyOfRange(args, 1, args.length);
            if(argument != null) {
                argument.setValues(values);
            }
        }
        ArrayList<String> missingArgs = null;
        for(String arg:arguments.keySet()){
            if(arguments.get(arg).isRequired() && arguments.get(arg).getValues() == null) {

                if(missingArgs == null) {
                    missingArgs = new ArrayList<>();
                }
                missingArgs.add(arguments.get(arg).getName());
            }
        }
        if(missingArgs != null) {

            String s = "Cannot execute command. Not enough arguments are given:\n" +
                    "    missing values: ";
            for(String g:missingArgs) {
                s += String.format("%n %10s %-10s %-30s", " ","-"+arguments.get(g).getName(), "   * "+ arguments.get(g).getDescription());
            }

            throw new ArgumentException(s);


        }else{
            if(this.executable != null){
                this.executable.execute(con, this);
            }else{
                throw new CommandException("No commands.Executable registered for this command");
            }
        }
    }

    public Argument getArgument(String argument){
        for(String g:arguments.keySet()) {
            if(arguments.get(g).hasAlias(argument)){
                return arguments.get(g);
            }
        }
        return null;
    }

    protected String getValue(String argument) {
        return this.arguments.get(argument.toLowerCase()).getValue();
    }

    protected String[] getValues(String argument) {
        return this.arguments.get(argument.toLowerCase()).getValues();
    }

    protected String getCommandLayout() {
        String ret = this.getName();

        for(String c:this.arguments.keySet()){
            ret += " [-"+c+"]";
        }
        ret += "\n    *" + this.getDescription();

        for(String c:this.arguments.keySet()){
            ret += String.format("%n %3s %-10s %-30s", "", "-"+c, " :" + this.arguments.get(c).getDescription());
        }

        return ret;
    }

    public String getInfo( ){
        String ret = this.getName();

        for(String c:this.arguments.keySet()){
            ret += " [-"+c+"]";
        }

        ret += "\n    *" + this.getDescription();

        return ret;
    }

    public Command setExecutable(Executable executable) {
        this.executable = executable;
        return this;
    }

    public Map<String, Argument> getArguments() {
        return arguments;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Executable getExecutable() {
        return executable;
    }
}

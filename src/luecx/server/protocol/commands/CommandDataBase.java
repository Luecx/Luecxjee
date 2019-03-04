package luecx.server.protocol.commands;

import luecx.server.tcp.connection.Connection;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by finne on 27.01.2018.
 */
public class CommandDataBase {

    private Map<String, Command> commands = new WeakHashMap<>();

    public CommandDataBase(){

        this.registerCommand(new Command("help", "overview of all functions")
                .registerArgument(new Argument("func", false, "f"))
                .setExecutable(new Executable() {
            @Override
            public void execute(Connection<?> con, Command c) {
                if(c.getValues("func")!=null) {
                    System.out.println(commands.get(c.getValue("func")).getCommandLayout());
                }else{
                    for(String s:commands.keySet()) {
                        System.out.println(commands.get(s).getInfo());
                    }
                }
            }
        }));
    }

    public void registerCommand(Command c) {
        try{
            commands.put(c.getName().toLowerCase(), c);
        }catch (Exception e) {
        }
    }

    public void removeCommand(String key) {
        commands.remove(key.toLowerCase());
    }

    public void executeCommand(Connection<?> con, String s) {

        try {
            if (s.startsWith("-")) s = s.substring(1);
            s = StringManipulation.transformIntoReadableCommand(s);
            int index = s.indexOf("-");


            String command = index >= 0 ? s.substring(0, index).trim() : s;
            String rest = index >= 0 ? s.substring(index + 1).trim() : "";

            for (String key : commands.keySet()) {
                if (key.equals(command.trim())) {
                    commands.get(key).execute(con, rest.split("-"));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeCommand(String s) {

        try {
            if (s.startsWith("-")) s = s.substring(1);
            s = StringManipulation.transformIntoReadableCommand(s);
            int index = s.indexOf("-");


            String command = index >= 0 ? s.substring(0, index).trim() : s;
            String rest = index >= 0 ? s.substring(index + 1).trim() : "";


            for (String key : commands.keySet()) {
                if (key.equals(command.trim())) {
                    commands.get(key).execute(null, rest.split("-"));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {


    }

}

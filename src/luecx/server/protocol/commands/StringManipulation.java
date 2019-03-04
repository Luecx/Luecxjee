package luecx.server.protocol.commands;

/**
 * Created by finne on 27.01.2018.
 */
public class StringManipulation {

    public static String transformIntoReadableCommand(String s) {
        return s.trim().replaceAll("[ ]{2,}", " ").toLowerCase();
    }
}

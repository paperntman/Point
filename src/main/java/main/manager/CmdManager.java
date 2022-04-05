package main.manager;

import main.SenderInfo;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Optional;

public class CmdManager{
    public static boolean run(String input, OutputStream stream, Optional<Runnable> runnable, SenderInfo senderInfo) {
        runnable.ifPresent(Runnable::run);
        boolean showError = false;
        final StreamManager streamManager = new StreamManager(stream);
        try {
            if(input.endsWith(" -showError")) showError = true;
            input = input.replace(" -showError", "");
            String cmd = input.split(" ")[0];
            String[] args = input.replaceFirst(cmd, "").trim().split(" ");
            if (args[0].isEmpty()) args = new String[]{};
            final Object o = Class.forName("main.cli." + cmd).getDeclaredConstructor().newInstance();
            Method m = Class.forName("main.cli." + cmd).getDeclaredMethod("run", String.class, String[].class, StreamManager.class, SenderInfo.class);
            m.invoke(o, cmd, args, streamManager, senderInfo);
            streamManager.close();
        } catch (ClassNotFoundException e){
            return true;
        } catch (Exception e) {
            if(showError){
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String exceptionString = stringWriter.toString();
                streamManager.println(exceptionString);
                streamManager.close();
            }
            return false;
        }
        return true;
        // TODO: 2022-04-03 Riot Api Cmd, Point Get / Set, View Data List & Data Filter
    }
}

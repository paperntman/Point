package main;

import main.manager.CliManager;
import main.manager.DiscordManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;

public class Main{

    public static Guild mainGuild;
    public static JDA jda;

    public static void main(String[] args) throws Exception {
        long started = System.currentTimeMillis();
        JDA jda = JDABuilder.createDefault(Config.get("token")).build().awaitReady();
        setupVars(jda);
        jda.addEventListener(new DiscordManager());
        new Thread(new CliManager()).start();
        System.out.println("main ready, "+(System.currentTimeMillis() - started)+" millis elapsed");
    }



    public static void setupVars(JDA jda){
        Main.jda = jda;
        mainGuild = jda.getGuildById("901824502122577981");
    }

}

package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

public class bet extends ListenerAdapter {

    static JDA jda;
    public static void main(JDA testjda) {
        System.out.println("bet starting");
        jda = testjda;
        System.out.println("bet ready");
    }

    public static Vector<String> list = new Vector<>();
    public static Vector<betAgent> agentVector = new Vector<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().toLowerCase().startsWith("/bet ")){
            if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                if(list.contains(event.getChannel().getId())){
                    event.getChannel().sendMessage("투표가 이미 진행 중입니다!");
                    return;
                }
                list.add(event.getChannel().getId());
                betAgent agent = new betAgent(event, event.getMessage().getContentRaw().replace("/bet ", "").split("/"), jda);
                jda.addEventListener(agent);
                agentVector.add(agent);
                event.getMessage().delete().queue();
            }else{
                Message m = event.getMessage().reply("권한이 없습니다!").complete();
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        m.delete().queue();
                        event.getMessage().delete().queue();
                    }
                };
                timer.schedule(timerTask, 3000);
            }
        }
    }

    public static void request(String id, UUID uuid){
        list.remove(id);
        for(betAgent agent : agentVector){
            if(agent.getOne().equals(uuid)){
                agentVector.remove(agent);
                break;
            }
        }
    }
}

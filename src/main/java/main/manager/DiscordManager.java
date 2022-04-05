package main.manager;

import main.Main;
import main.SenderInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.*;

public class DiscordManager extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.isFromGuild()){
            final Member member = event.getMember();
            final EnumSet<Permission> permissions = Objects.requireNonNull(member).getPermissions();
            if (permissions.contains(Permission.ADMINISTRATOR) && !event.getMember().getId().equals(Main.jda.getSelfUser().getId())) {
                if (!CmdManager.run( event.getMessage().getContentRaw(), new OutputStream() {
                    final List<Byte> list = new ArrayList<>();

                    @Override
                    public void write(int b) {
                        if(b != 200) list.add((byte) b);
                        Byte[] bytelist = new Byte[list.size()];
                        bytelist = list.toArray(bytelist);
                        byte[] bytes = new byte[bytelist.length];
                        for (int i = 0; i < bytelist.length; i++) {
                            bytes[i] = bytelist[i];
                        }
                        if (b == -200) {
                            String send = new String(bytes);
                            send = send.substring(0, send.length()-1);
                            while(!send.equals("")){
                                event.getChannel().sendMessage(send.substring(0, send.length() > 2000 ? 2000 : send.length()%2000)).queue();
                                if(send.length() > 2000){
                                    send = send.substring(2000);
                                }else send = "";
                            }
                            list.clear();
                        }
                    }
                }, Optional.of(() -> event.getChannel().sendTyping().queue()), new SenderInfo("discord", event))) {
                    event.getChannel().sendMessage("Error!").queue();
                }
            }
        }
    }
}

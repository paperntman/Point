package main.cli;

import main.CmdInterface;
import main.Main;
import main.SenderInfo;
import main.manager.StreamManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Objects;

public class messageinfo implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager stream, SenderInfo info) throws IOException {
        String channelId;
        String id;
        JDA jda = Main.jda;
        if(info.getName().equalsIgnoreCase("discord")){
            final MessageReceivedEvent event = (MessageReceivedEvent) info.getInfo();
            if (event.getMessage().getReferencedMessage() == null) {
                stream.println("please make the message referenced !!");
                return;
            }
            channelId = event.getChannel().getId();
            id = event.getMessage().getReferencedMessage().getId();
        }else{
            if(args.length < 2) {
                stream.println("plz write channel Id and message Id");
                return;
            }
            channelId = args[0];
            id = args[1];
        }
        final Message message = Objects.requireNonNull(jda.getTextChannelById(channelId)).retrieveMessageById(id).complete();
        String builder = String.format("sender: %s\n", message.getAuthor().getAsTag()) +
                String.format("contents: %s\n", message.getContentRaw()) +
                String.format("id: %s\n", message.getId()) +
                String.format("time: %s\n", message.getTimeCreated().atZoneSameInstant(ZoneId.systemDefault()).toEpochSecond());
        stream.println(builder);
    }
}

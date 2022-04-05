package main.manager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static main.Main.jda;

public class SlashCommandManager extends ListenerAdapter {

    public SlashCommandManager() {
        CommandData getCommand = new CommandData("get", "유저의 포인트를 보여줍니다.")
                .addOption(OptionType.USER, "유저", "데이터를 가져올 유저입니다.", true);
        CommandData addCommand = new CommandData("add", "유저의 포인트를 추가합니다.")
                .addOption(OptionType.USER, "유저", "데이터를 추가할 유저입니다.", true)
                .addOption(OptionType.INTEGER, "포인트", "추가할 포인트의 양입니다.", true);
        for(Guild guild : jda.getGuilds()){
            try{
                for(Command command : guild.retrieveCommands().complete()){
                    if(command.getApplicationId().equals(jda.getSelfUser().getApplicationId()))
                        guild.deleteCommandById(command.getId()).complete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for (Guild guild : jda.getGuilds()) {
            try {
                guild.upsertCommand(getCommand).complete();
                guild.upsertCommand(addCommand).complete();
            }catch (Exception e){
                System.err.println("------\n"+guild.getName());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        System.out.println(Objects.requireNonNull(event.getMember()).getAsMention()+" ("+event.getMember().getNickname()+") used "+event.getName());
        switch (event.getName()){
            case "get" :  event.deferReply().complete(); getPoint(event);break;
            case "add" :  event.deferReply().complete(); addPoint(event);break;
        }
    }
    public void getPoint(SlashCommandEvent event){
        CurrencyManager manager = new CurrencyManager(event.getGuild().getId(), jda);
        Member m = event.getOption("유저").getAsMember();
        event.getHook().sendMessage(m.getEffectiveName()+"의 포인트는 "+manager.get(m)+"입니다.").queue();
    }
    public void addPoint(SlashCommandEvent event){
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)){
            event.getHook().sendMessage("권한이 없습니다!").queue();
            return;
        }
        CurrencyManager manager = new CurrencyManager(event.getGuild().getId(), jda);
        Member m = event.getOption("유저").getAsMember();
        Long a = event.getOption("포인트"). getAsLong();
        Long originalPoint = manager.get(m);
        manager.add(m, Math.toIntExact(a));
        event.getHook().sendMessage(m.getEffectiveName()+"의 포인트에 "+a+"를 추가하여 "+originalPoint+" 에서 "+manager.get(m)+" 포인트가 되었습니다.").queue();
    }
}

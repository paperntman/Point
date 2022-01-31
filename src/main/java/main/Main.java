package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Main extends ListenerAdapter {

    static Guild mainGuild;
    static JDA jda;
    static Api api;

    public static void main(String[] args) throws Exception {
        Long started = System.currentTimeMillis();
        System.out.println("main starting");
        JDA jda = JDABuilder.createDefault("OTE4MjgwNjgxOTEyNjY0MDc0.YbE9hw.lgVidgLCw_E1PGwPJQMfdc6aO94").addEventListeners(new Main(), new bet()).build().awaitReady();
        bet.main(jda);
        for(Guild guild : jda.getGuilds()){
            try{
                for(Command command : guild.retrieveCommands().complete()){
                    if(command.getApplicationId().equals(jda.getSelfUser().getApplicationId()))
                        guild.deleteCommandById(command.getId()).complete();
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(guild.getName());
            }
        }
        CommandData getCommand = new CommandData("get", "유저의 포인트를 보여줍니다.")
                .addOption(OptionType.USER, "유저", "데이터를 가져올 유저입니다.", true);
        CommandData addCommand = new CommandData("add", "유저의 포인트를 추가합니다.")
                .addOption(OptionType.USER, "유저", "데이터를 추가할 유저입니다.", true)
                .addOption(OptionType.INTEGER, "포인트", "추가할 포인트의 양입니다.", true);
        CommandData checkCommand = new CommandData("check", "최근 20전적의 게임에서 얻을 수 있는 포인트를 가져옵니다. 띄어쓰기는 -로 부탁드려요!")
                .addOption(OptionType.STRING, "닉네임", "데이터를 가져올 롤 플레이어입니다.띄어쓰기는 -로 부탁드려요!.", true);
        CommandData betendCommand = new CommandData("betend", "베팅을 끝냅니다.")
                .addOption(OptionType.INTEGER, "option", "1번과 2번, 어느 쪽이 맞았습니까?", true);
        CommandData voteCommand = new CommandData("vote", "베팅합니다.")
                .addOption(OptionType.INTEGER, "option", "1번과 2번, 어느 쪽이 더 끌립니까?", true)
                .addOption(OptionType.INTEGER, "amount", "얼마만큼의 금액을 베팅하실 겁니까?", true);
       for (Guild guild : jda.getGuilds()) {
            try {
                guild.upsertCommand(getCommand).complete();
                guild.upsertCommand(addCommand).complete();
                guild.upsertCommand(voteCommand).complete();
                guild.upsertCommand(betendCommand).complete();
                if(guild.getId().equals("765475010872606753")){
                    guild.upsertCommand(checkCommand).complete();
                }
            }catch (Exception e){
                System.err.println("------\n"+guild.getName());
                System.err.println(Arrays.toString(e.getStackTrace()));
            }
        }
       setupVars(jda);
        System.out.println("main ready, "+(System.currentTimeMillis() - started)+" millis elapsed");
    }

    public static void setupVars(JDA jda){
        Main.jda = jda;
        mainGuild = jda.getGuildById("901824502122577981");
        api = new Api();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        System.out.println(Objects.requireNonNull(event.getMember()).getAsMention()+" ("+event.getMember().getNickname()+") used "+event.getName());
        switch (event.getName()){
            case "get" :  event.deferReply().complete(); getPoint(event);break;
            case "add" :  event.deferReply().complete(); addPoint(event);break;
            case "check" :
                event.deferReply().complete();
                try {
                    checkPoint(event);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public void checkPoint(SlashCommandEvent event) throws IOException, ParseException {
        try{
            String str = String.valueOf(api.main(event.getOption("닉네임").getAsString()));
            if(str.equalsIgnoreCase("-1")){
                event.getHook().sendMessage("java.lang.Exception: Exception occured at line 110: 측정 불가").queue();
                return;
            }
            event.getHook().sendMessage(event.getOption("닉네임").getAsString().replaceAll("-"," ")+" : "+str).queue();
        }catch (Exception e){
            event.getHook().sendMessage("오류 발생!").queue();
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

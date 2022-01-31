package main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.Vector;

public class betAgent extends ListenerAdapter {

    class vMember{
        Member member;
        double amount;
        public vMember(Member m, long a){
            member = m;
            amount = a;
        }

        public double getAmount() {
            return amount;
        }

        public Member getMember() {
            return member;
        }
    }

    MessageReceivedEvent messageReceivedEvent;
    Vector<vMember> opt1 = new Vector<>();
    Vector<vMember> opt2 = new Vector<>();
    CurrencyManager currencyManager;
    long opt1Amount = 0;
    long opt2Amount = 0;
    double total = 0;
    TextChannel channel;
    Message message;
    Member author;
    String[] args;
    UUID one = UUID.randomUUID();

    public UUID getOne() {
        return one;
    }

    public betAgent(MessageReceivedEvent event, String[] args, JDA jda){
        channel = jda.getTextChannelById(event.getChannel().getId());
        this.args = args;
        messageReceivedEvent = event;
        author = event.getMember();
        message = channel.sendMessageEmbeds(embedBuilder(author)).complete();
        currencyManager = new CurrencyManager(event.getGuild().getId(), jda);
        System.out.println("agent created : "+one);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if(!event.getChannel().equals(channel)) return;
        if(event.getName().equals("vote")){
            long opt = event.getOption("option").getAsLong();
            long amount = event.getOption("amount").getAsLong();
            Member member = event.getMember();
            if(contains(opt1, member) || contains(opt2, member)){
                event.reply("당신은 이미 투표했습니다!").queue();
                return;
            }
            if(amount <= 0){
                event.reply("1 이상의 포인트를 입력해주세요!").queue();
                return;
            }
            if(opt < 1 || 2 < opt){
                event.reply("1 또는 2의 숫자를 입력해주세요!").queue();
                return;
            }
            if(currencyManager.get(event.getMember()) < amount){
                event.reply("포인트가 부족합니다!").queue();
                return;
            }
            if(opt == 1){
                opt1.add(new vMember(member, amount));
                opt1Amount += amount;
            }else {
                opt2.add(new vMember(member, amount));
                opt2Amount += amount;
            }
            currencyManager.add(member, 0-amount);
            update();
            event.reply(amount+"만큼을 "+args[Math.toIntExact(opt)]+"에 투표하였습니다!").queue();
        }else if(event.getName().equals("betend")){
            if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                event.reply("권한이 없습니다!").complete();
                return;
            }
            long opt = event.getOption("option").getAsLong();
            if(opt < 1 || 2 < opt){
                event.reply("1 또는 2의 숫자를 입력해주세요!").queue();
                return;
            }
            event.replyEmbeds(end(opt)).queue();
            bet.request(channel.getId(), one);
        }
    }

    public MessageEmbed end(long opt) {
        EmbedBuilder builder = new EmbedBuilder();
        Vector<vMember> teamWon = (opt == 1) ? opt1 : opt2;
        Double amountWon = Double.valueOf((opt == 1) ? opt1Amount : opt2Amount);
        if(((opt == 1) ? opt1Amount : opt2Amount) == 0){
            builder.setTitle(args[0])
                    .setAuthor("bet started by "+author.getEffectiveName(), null, author.getEffectiveAvatarUrl())
                    .addField(new MessageEmbed.Field(
                            args[(int) opt]+"가 정답이었습니다!", "정답을 맞춘 사람에겐 건 포인트 1당 0만큼의 포인트가 보상으로 수여됩니다.", true));

        }else{
            builder.setTitle(args[0])
                .setAuthor("bet started by "+author.getEffectiveName(), null, author.getEffectiveAvatarUrl())
                .addField(new MessageEmbed.Field(
                        args[(int) opt]+"가 정답이었습니다!", "정답을 맞춘 사람에겐 건 포인트 1당 "+ Math.round(total / amountWon)
                        +"만큼의 포인트가 보상으로 수여됩니다.", true));
            for(vMember m : teamWon){
                currencyManager.add(m.getMember(), Math.round(m.getAmount() * (total / amountWon)));
            }
        }
        bet.list.remove(channel.getId());
        System.out.println("agent removed : "+one);
        return builder.build();
    }

    public MessageEmbed embedBuilder(Member author, Vector<vMember> opt1, long opt1Amount, Vector<vMember> opt2, long opt2Amount){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(args[0]);
        builder.setAuthor("bet started by "+author.getEffectiveName(), null , author.getEffectiveAvatarUrl());
        if(opt1Amount == 0){
            builder.addField(new MessageEmbed.Field(args[1], ":coin: "+ opt1Amount+
                    "\n:trophy: 1 : 0"+
                    "\n:busts_in_silhouette: "+ opt1.size() +
                    "\n:first_place: "+max(opt1), true, true));
        }else{
            builder.addField(new MessageEmbed.Field(args[1], ":coin: "+ opt1Amount+
                    "\n:trophy: 1 : "+ Math.round(total/opt1Amount)+
                    "\n:busts_in_silhouette: "+ opt1.size() +
                    "\n:first_place: "+max(opt1), true, true));
        }
        if(opt2Amount == 0){
            builder.addField(new MessageEmbed.Field(args[2], ":coin: "+ opt2Amount+
                    "\n:trophy: 1 : 0"+
                    "\n:busts_in_silhouette: "+ opt2.size() +
                    "\n:first_place: "+max(opt2), true, true));
        }else{
            builder.addField(new MessageEmbed.Field(args[2], ":coin: "+ opt2Amount+
                    "\n:trophy: 1 : "+ Math.round(total/opt2Amount)+
                    "\n:busts_in_silhouette: "+ opt2.size() +
                    "\n:first_place: "+max(opt2), true, true));
        }
        return builder.build();
    }

    public MessageEmbed embedBuilder(Member author){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(args[0]);
        builder.setAuthor("bet started by "+author.getEffectiveName(), null , author.getEffectiveAvatarUrl());
        builder.addField(new MessageEmbed.Field(
                args[1], ":coin: "+ opt1Amount+
                "\n:trophy: 1 : 0"+
                "\n:busts_in_silhouette: 0"+
                "\n:first_place: 0", true))
                .addField(new MessageEmbed.Field(
                        args[2], ":coin: "+ opt2Amount+
                        "\n:trophy: 1 : 0"+
                        "\n:busts_in_silhouette: 0"+
                        "\n:first_place: 0", true));
        return builder.build();
    }

    public void update(){
        total = opt1Amount+opt2Amount;
        message = message.editMessageEmbeds(embedBuilder(messageReceivedEvent.getMember(), opt1, opt1Amount, opt2, opt2Amount)).complete();
    }

    public boolean contains(Vector<vMember> v, Member m){
        for(vMember vm : v){
            if(vm.getMember().equals(m)) return true;
        }
        return false;
    }

    public long max(Vector<vMember> v){
        long max = 0;
        for(vMember vm : v){
            if(vm.getAmount() > max) max = (long) vm.getAmount();
        }
        return max;
    }
}

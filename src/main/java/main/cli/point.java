package main.cli;

import main.CmdInterface;
import main.Main;
import main.SenderInfo;
import main.manager.CurrencyManager;
import main.manager.StreamManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Map;
import java.util.Objects;

public class point implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager streamManager, SenderInfo info) {
        if(args.length == 0) {
            streamManager.println("point get/set/list ");
        }
        if (info.getName().equalsIgnoreCase("discord")) {
            Discord(args, streamManager, info);
        }
        else Console(args, streamManager);
    }

    private void Console(String[] args, StreamManager streamManager){
        JDA jda = Main.jda;
        CurrencyManager manager;
        String user;
        try {
            final User userByTag = jda.getUserByTag(args[1]);
            user = userByTag != null ? userByTag.getId() : null;
            if(user == null) throw new NullPointerException();
        } catch (Exception e) {
            user = Objects.requireNonNull(jda.getUserById(args[1])).getId();
        }
        switch (args[0]) {
            case "get" -> {
                manager = new CurrencyManager(args[2], jda);
                streamManager.println(user + " : " + manager.get(user));
            }
            case "set" -> {
                manager = new CurrencyManager(args[3], jda);
                manager.set(user, Long.parseLong(args[2]));
                streamManager.println(user + "의 점수를 " + args[2] + "로 설정하였습니다.");
            }
            case "add" -> {
                manager = new CurrencyManager(args[3], jda);
                manager.add(user, Long.parseLong(args[2]));
                streamManager.println(user + "의 점수를 " + manager.get(user) + "로 설정하였습니다.");
            }
            case "list" -> {
                Map<String, Long> list = new CurrencyManager(args[1], jda).list();
                System.out.println(list.size());
                for (String s : list.keySet()) {
                    final Member member = Objects.requireNonNull(jda.getGuildById(args[1])).getMemberById(s);
                    if (member == null) {
                        streamManager.println(s + " : " + list.get(s));
                    } else {
                        streamManager.println(member.getUser().getAsTag() + " : " + list.get(s));
                    }
                }
            }
        }

    }

    private void Discord(String[] args, StreamManager streamManager, SenderInfo info){
        JDA jda = Main.jda;
        MessageReceivedEvent event = (MessageReceivedEvent) info.getInfo();
        CurrencyManager manager = new CurrencyManager(event.getGuild().getId(), jda);
        String user;
        if(!args[0].equals("list"))
        try {
            final User userByTag = jda.getUserByTag(args[1]);
            user = userByTag != null ? userByTag.getId() : null;
            if(user == null) throw new NullPointerException();
        } catch (Exception e) {
            user = Objects.requireNonNull(jda.getUserById(args[1])).getId();
        }
        switch (args[0]) {
            case "get" -> {
                manager = args.length < 3 ? manager : new CurrencyManager(args[2], jda);
                streamManager.println(user + " : " + manager.get(user));
            }
            case "set" -> {
                manager = args.length < 4 ? manager : new CurrencyManager(args[3], jda);
                manager.set(user, Long.parseLong(args[2]));
                streamManager.println(user + "의 점수를 " + args[2] + "로 설정하였습니다.");
            }
            case "add" -> {
                manager = args.length < 4 ? manager : new CurrencyManager(args[3], jda);
                manager.add(user, Long.parseLong(args[2]));
                streamManager.println(user + "의 점수를 " + manager.get(user) + "로 설정하였습니다.");
            }
            case "list" -> {
                Map<String, Long> list = args.length == 1 ? manager.list() : new CurrencyManager(args[1], jda).list();
                System.out.println(list.size());
                for (String s : list.keySet()) {
                    final Member member = event.getGuild().getMemberById(s);
                    if (member == null) {
                        streamManager.println(s + " : " + list.get(s));
                    } else {
                        streamManager.println(member.getUser().getAsTag() + " : " + list.get(s));
                    }
                }
            }
        }
    }
}

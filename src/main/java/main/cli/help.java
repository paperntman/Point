package main.cli;

import main.CmdInterface;
import main.SenderInfo;
import main.manager.StreamManager;

public class help implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager streamManager, SenderInfo info) {
        streamManager.println("no asdf yet");
    }
}
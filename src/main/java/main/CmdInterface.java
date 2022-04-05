package main;

import main.manager.StreamManager;

import java.io.IOException;

public interface CmdInterface {
    default void run(String cmd, String[] args, StreamManager stream, SenderInfo info) throws IOException {}
}

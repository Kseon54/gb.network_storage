package main.client;


import main.server.messages.AbstractMessage;

public interface OnMessageReceived {

    void onReceive(AbstractMessage msg);

}

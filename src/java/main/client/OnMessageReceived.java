package main.client;


import main.server.messages.IMessage;

public interface OnMessageReceived {

    void onReceive(IMessage msg);

}

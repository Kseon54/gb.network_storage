package main.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import main.server.messages.IMessage;

public class ClientMessageHandler extends SimpleChannelInboundHandler<IMessage> {

    private final OnMessageReceived callback;

    public ClientMessageHandler(OnMessageReceived callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                IMessage message) throws Exception {
        callback.onReceive(message);
    }
}

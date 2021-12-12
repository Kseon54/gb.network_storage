package main.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import main.server.comands.Command;
import main.server.comands.CommandType;
import main.server.messages.IMessage;
import main.server.messages.StringMessage;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<IMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                IMessage abstractMessage) throws Exception {
        log.debug("Received: {}", abstractMessage);
//        ctx.writeAndFlush(abstractMessage);
        if (abstractMessage instanceof StringMessage) actionMessage(ctx, (StringMessage) abstractMessage);

    }

    private void actionMessage(ChannelHandlerContext ctx, StringMessage message) {
        String[] tokens = message.getContent().trim().split(" +");
        CommandType type;
        try {
            type = CommandType.byCommand(tokens[0]);
            switch (type) {
                case AUTH:
                    Command.authCommand(ctx, tokens);
                    break;
            }
        } catch (RuntimeException e) {
            log.error("e=", e);
        }
    }

}

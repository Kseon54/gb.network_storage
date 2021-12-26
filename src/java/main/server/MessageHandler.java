package main.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import main.server.comands.Command;
import main.server.comands.CommandType;
import main.server.messages.ActionMessage;
import main.server.messages.FileMessage;
import main.server.messages.IMessage;
import main.server.messages.StringMessage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<IMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                IMessage abstractMessage){
        log.debug("Received: {}", abstractMessage);
        if (abstractMessage instanceof StringMessage) actionStringMessage(ctx, (StringMessage) abstractMessage);
        if (abstractMessage instanceof ActionMessage) actionAbstractMessage(ctx, (ActionMessage) abstractMessage);
        if (abstractMessage instanceof FileMessage) actionFileMessage(ctx, (FileMessage) abstractMessage);

    }

    private void actionFileMessage(ChannelHandlerContext ctx, FileMessage message) {
        Command.downland(ctx,message);
    }

    private void actionAbstractMessage(ChannelHandlerContext ctx, ActionMessage message) {
        try {
            switch (message.getType()) {
                case MKDIR:
                    Command.mkDigCommand(ctx, Paths.get(message.getPath()), message.getName());
                    break;
                case RENAME:
                    Command.renameCommand(ctx, Paths.get(message.getPath()), message.getName());
                    break;
                case DELETE:
                    Command.deleteCommand(ctx, Paths.get(message.getPath()), message.getName());
                    break;
                case GET_FILES:
                    Command.getFiles(ctx, Paths.get(message.getPath()));
                    break;
                case DOWNLAND:
                    Command.getFileToClient(ctx,message.getPath(),message.getName());
            }
        } catch (RuntimeException e) {
            log.error("e=", e);
        }
    }

    private void actionStringMessage(ChannelHandlerContext ctx, StringMessage message) {
        String[] tokens = message.getContent().trim().split(" +");
        CommandType type;
        try {
            type = CommandType.byCommand(tokens[0]);
            if (type == CommandType.AUTH) {
                Command.authCommand(ctx, tokens);
            }
        } catch (RuntimeException e) {
            log.error("e=", e);
        }
    }

}

package main.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import main.server.comands.Command;
import main.server.comands.CommandType;
import main.server.messages.AbstractMessage;
import main.server.messages.FileMessage;
import main.server.messages.StringMessage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage abstractMessage) throws Exception {
        log.debug("Received: {}", abstractMessage);
        ctx.writeAndFlush(abstractMessage);
        /**
         * GOTO
         */
        if (abstractMessage instanceof StringMessage) {
            StringMessage message = (StringMessage) abstractMessage;
            String[] tokens = message.getContent().trim().split(" +");
            CommandType type;
            try {
                type = CommandType.byCommand(tokens[0]);
                switch (type) {
                    case AUTH:
                        authCommand(ctx, tokens);
                        break;
                }
            } catch (RuntimeException e) {
                log.error("e=", e);
            }
        }
    }

    private void authCommand(ChannelHandlerContext ctx, String[] tokens) {
        if (tokens.length != 3) {
            sendMessage(ctx, new StringMessage("Command should have 3 args", LocalDateTime.now()));
            return;
        }
        if (Command.auth(ctx, tokens[1], tokens[2])) {
            Path path = Paths.get("src/java/file" + File.separator + tokens[1]);
            FileMessage fileMessage = new FileMessage(path, null, LocalDateTime.now());
            sendMessage(ctx, fileMessage);
        }else {
            sendMessage(ctx, new StringMessage("Invalid username or password", LocalDateTime.now()));
        }
    }

    private void sendMessage(ChannelHandlerContext ctx, AbstractMessage abstractMessage) {
        log.debug("Received: {}", abstractMessage);
        ctx.writeAndFlush(abstractMessage);
    }
}

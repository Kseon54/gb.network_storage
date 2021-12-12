package main.server.comands;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import main.server.db.User;
import main.server.messages.AuthMessage;
import main.server.messages.IMessage;
import main.server.messages.StringMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public final class Command {

    public static void authCommand(ChannelHandlerContext ctx, String[] tokens) {
        if (tokens.length != 3) {
            sendMessage(ctx, new StringMessage("Invalid username or password", LocalDateTime.now()));
            return;
        }
        Optional<User> user = User.findUserByLoginAndPassword(tokens[1], tokens[2]);
        if (user.isPresent()) {
            Path path = Paths.get("src/resources/file" + File.separator + user.get().getId());

            if (!Files.exists(path)) {
                try {
                    Files.createDirectory(path);
                } catch (IOException e) {
                    log.error("e = ", e);
                }
            }

            AuthMessage authMessage = new AuthMessage(user.get(), path);
            sendMessage(ctx, authMessage);
        } else {
            sendMessage(ctx, new StringMessage("Invalid username or password", LocalDateTime.now()));
        }
    }

    public static void sendMessage(ChannelHandlerContext ctx, IMessage abstractMessage) {
        log.debug("Received: {}", abstractMessage);
        ctx.writeAndFlush(abstractMessage);
    }
}

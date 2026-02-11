package org.mdxabu.Commands.Labs;

import java.util.Random;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class inMessageCommands extends ListenerAdapter {

    private static final String PREFIX = "?";
    private final Random random = new Random();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String rawMessage = event.getMessage().getContentRaw().trim();

        if (!rawMessage.startsWith(PREFIX)) return;

        String fullCommand = rawMessage.substring(PREFIX.length()).trim();
        String commandName;

        int spaceIndex = fullCommand.indexOf(' ');
        if (spaceIndex == -1) {
            commandName = fullCommand.toLowerCase();
        } else {
            commandName = fullCommand.substring(0, spaceIndex).toLowerCase();
        }

        switch (commandName) {
            // --- Original Fun Commands ---
            case "flip": {
                String coin = random.nextInt(2) == 0 ? "heads" : "tails";
                event.getMessage().reply(coin).queue();
                break;
            }
            case "roll": {
                int roll = random.nextInt(1, 7);
                event.getMessage().reply(String.valueOf(roll)).queue();
                break;
            }
            default:
                // Unknown command — silently ignore to avoid spam
                break;
        }
    }
}

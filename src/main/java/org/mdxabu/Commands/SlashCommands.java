package org.mdxabu.Commands;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mdxabu.Commands.Labs.CommandLab;

public class SlashCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(
        @NotNull SlashCommandInteractionEvent event
    ) {
        if (event.getGuild() == null) {
            return;
        }

        switch (event.getName()) {
            // --- Original Commands ---
            case "hello":
                event.reply("Hello, " + event.getUser().getAsMention()).queue();
                break;
            case "say":
                CommandLab.say(
                    event,
                    Objects.requireNonNull(
                        event.getOption("content")
                    ).getAsString()
                );
                break;
            case "write":
                CommandLab.writeName(
                    event,
                    Objects.requireNonNull(
                        event.getOption("name")
                    ).getAsString()
                );
                break;
            case "rps":
                CommandLab.rps(event, event.getOption("choice").getAsString());
                break;
            default:
                event.reply("There is no command like this...").queue();
        }
    }
}

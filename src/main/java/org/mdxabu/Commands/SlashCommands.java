package org.mdxabu.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mdxabu.Commands.Labs.CommandLab;

import java.util.Objects;

public class SlashCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getGuild()==null){
            return;
        }
        if (event.getName().equals("say")) {
            CommandLab.say(event, Objects.requireNonNull(event.getOption("content")).getAsString());
        } else {
            event.reply("I can't handle that command right now :(")
                    .setEphemeral(true)
                    .queue();
        }
    }
}

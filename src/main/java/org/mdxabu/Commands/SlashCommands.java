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
        switch (event.getName()){
            case "hello":
                event.reply("Rawr! Rawr! " + event.getUser().getAsMention()).queue();
                break;
            case "say":
                CommandLab.say(event, Objects.requireNonNull(event.getOption("content")).getAsString());
                break;

            default:
                event.reply("There is no command like this...").queue();

        }
    }
}

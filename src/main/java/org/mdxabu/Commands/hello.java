package org.mdxabu.Commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class hello extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equalsIgnoreCase("!hello")){
            User author = event.getAuthor();
            if(!author.isBot()) {
                event.getChannel().sendMessage("Rawr! Rawr! "+author.getAsMention()).queue();
            }
        }
    }

}

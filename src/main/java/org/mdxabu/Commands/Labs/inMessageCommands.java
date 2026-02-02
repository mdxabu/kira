package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class inMessageCommands extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("?flip")){
            Random random = new Random();
            String coin;

            int flip = random.nextInt(2);

            coin = flip==0?"heads":"tails";

            event.getMessage().reply(coin).queue();
        }
    }
}

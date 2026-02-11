package org.mdxabu.KiraBot;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.*;

import java.util.EnumSet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.mdxabu.Commands.Labs.inMessageCommands;
import org.mdxabu.Commands.SlashCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiraBot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(KiraBot.class);

    static JDA kiraBuilder;

    public static void run() {
        // --- Build JDA ---
        String botToken = System.getenv("BOT-TOKEN");
        if (botToken == null || botToken.isBlank()) {
            logger.error("BOT-TOKEN environment variable is not set!");
            System.exit(1);
        }

        EnumSet<GatewayIntent> intents = EnumSet.of(
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.MESSAGE_CONTENT
        );

        kiraBuilder = JDABuilder.createLight(botToken, intents)
            .addEventListeners(new KiraBot())
            .addEventListeners(new SlashCommands())
            .addEventListeners(new inMessageCommands())
            .setActivity(Activity.watching("You..."))
            .setStatus(OnlineStatus.ONLINE)
            .build();

        // --- Register Slash Commands ---
        CommandListUpdateAction commands = kiraBuilder.updateCommands();

        commands
            .addCommands(
                slash("hello", "Say hello to Kira"),
                Commands.slash(
                    "say",
                    "Makes the bot say what you tell it to"
                ).addOption(STRING, "content", "What the bot should say", true),
                Commands.slash(
                    "write",
                    "Write the name you want to be in the deathnote :)"
                ).addOption(STRING, "name", "Name of the person", true)
            )
            .queue(
                success ->
                    logger.info(
                        "Successfully registered {} slash commands!",
                        success.size()
                    ),
                failure ->
                    logger.error(
                        "Failed to register slash commands: {}",
                        failure.getMessage()
                    )
            );

        // --- Graceful Shutdown Hook ---
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                logger.info("Shutting down Kira bot...");
                if (kiraBuilder != null) {
                    kiraBuilder.shutdown();
                    logger.info("JDA shutdown complete.");
                }
            })
        );

        logger.info("Kira bot is starting up!");
    }
}

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
import org.mdxabu.Database.MongoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiraBot extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(KiraBot.class);

    static JDA kiraBuilder;

    public static void run() {
        // --- Initialize MongoDB ---
        String mongoUri = System.getenv("MONGO_URI");
        if (mongoUri == null || mongoUri.isBlank()) {
            logger.error(
                "MONGO_URI environment variable is not set! Please set it before running the bot."
            );
            logger.info(
                "Example: export MONGO_URI=\"mongodb://localhost:27017\" or use a MongoDB Atlas connection string."
            );
            System.exit(1);
        }

        try {
            MongoManager.initialize(mongoUri);
            logger.info("MongoDB initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize MongoDB: {}", e.getMessage());
            System.exit(1);
        }

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
            .setActivity(Activity.watching("You... \uD83C\uDFA3"))
            .setStatus(OnlineStatus.ONLINE)
            .build();

        // --- Register Slash Commands ---
        CommandListUpdateAction commands = kiraBuilder.updateCommands();

        commands
            .addCommands(
                // ========== Original Commands ==========
                slash("hello", "Say hello to Kira"),
                Commands.slash(
                    "say",
                    "Makes the bot say what you tell it to"
                ).addOption(STRING, "content", "What the bot should say", true),
                Commands.slash(
                    "write",
                    "Write the name you want to be in the deathnote :)"
                ).addOption(STRING, "name", "Name of the person", true),
                // ========== Economy Commands ==========
                Commands.slash("balance", "Check your coin balance and wallet"),
                Commands.slash(
                    "daily",
                    "Claim your daily 200 coins (24h cooldown)"
                ),
                Commands.slash(
                    "shop",
                    "Browse the item shop — fishing rods, traps, and more"
                ),
                Commands.slash(
                    "buy",
                    "Purchase an item from the shop"
                ).addOption(
                    STRING,
                    "item",
                    "The item ID or name to buy (e.g. fishing_rod)",
                    true
                ),
                Commands.slash(
                    "sell",
                    "Sell an item from your inventory for 50% of its price"
                ).addOption(
                    STRING,
                    "item",
                    "The item ID or name to sell (e.g. fishing_rod)",
                    true
                ),
                Commands.slash("inventory", "View all items in your inventory"),
                // ========== Fishing & Trapping ==========
                Commands.slash(
                    "fish",
                    "Cast your fishing rod and catch fish! (Requires a Fishing Rod)"
                ),
                Commands.slash(
                    "trap",
                    "Check your food trap for caught animals! (Requires a Food Trap)"
                ),
                // ========== Profile & Stats ==========
                Commands.slash(
                    "profile",
                    "View your full player profile with stats and gear"
                ),
                Commands.slash(
                    "leaderboard",
                    "View the top 10 richest players"
                ),
                Commands.slash(
                    "ecohelp",
                    "Show all economy, fishing, and trapping commands"
                )
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
                try {
                    MongoManager.getInstance().close();
                    logger.info("MongoDB connection closed.");
                } catch (Exception e) {
                    logger.warn("Error closing MongoDB: {}", e.getMessage());
                }
                if (kiraBuilder != null) {
                    kiraBuilder.shutdown();
                    logger.info("JDA shutdown complete.");
                }
            })
        );

        logger.info("Kira bot is starting up with economy & fishing system!");
        logger.info(
            "Entry bonus: 500 coins | Daily: 200 coins | Shop items: 8"
        );
        logger.info(
            "Commands: /fish, /trap, /shop, /buy, /sell, /inventory, /balance, /daily, /profile, /leaderboard"
        );
    }
}

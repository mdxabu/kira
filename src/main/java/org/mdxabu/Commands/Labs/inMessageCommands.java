package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mdxabu.Economy.EconomyManager;
import org.mdxabu.Economy.FishingManager;
import org.mdxabu.Models.PlayerData;

import java.util.Random;

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
        String args = "";

        int spaceIndex = fullCommand.indexOf(' ');
        if (spaceIndex == -1) {
            commandName = fullCommand.toLowerCase();
        } else {
            commandName = fullCommand.substring(0, spaceIndex).toLowerCase();
            args = fullCommand.substring(spaceIndex + 1).trim();
        }

        String userId = event.getAuthor().getId();
        String username = event.getAuthor().getEffectiveName();
        String avatarUrl = event.getAuthor().getEffectiveAvatarUrl();

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

            // --- Economy Commands ---
            case "balance":
            case "bal":
            case "wallet": {
                MessageEmbed embed = EconomyManager.getBalanceEmbed(userId, username, avatarUrl);
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "daily": {
                MessageEmbed embed = EconomyManager.claimDailyEmbed(userId, username);
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "shop":
            case "store": {
                MessageEmbed embed = EconomyManager.getShopEmbed();
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "buy":
            case "purchase": {
                if (args.isEmpty()) {
                    event.getMessage().reply("Please specify an item to buy! Usage: `?buy <item_id>`\nExample: `?buy fishing_rod`").queue();
                    return;
                }
                MessageEmbed embed = EconomyManager.buyItemEmbed(userId, username, args);
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "sell": {
                if (args.isEmpty()) {
                    event.getMessage().reply("Please specify an item to sell! Usage: `?sell <item_id>`\nExample: `?sell fishing_rod`").queue();
                    return;
                }
                MessageEmbed embed = EconomyManager.sellItemEmbed(userId, username, args);
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "inventory":
            case "inv":
            case "bag": {
                MessageEmbed embed = EconomyManager.getInventoryEmbed(userId, username, avatarUrl);
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            // --- Fishing & Trapping ---
            case "fish":
            case "fishing": {
                PlayerData player = PlayerData.getOrCreatePlayer(userId);
                FishingManager.FishingResult result = FishingManager.doFish(player);
                event.getMessage().replyEmbeds(result.getEmbed()).queue();
                break;
            }

            case "trap":
            case "trapping": {
                PlayerData player = PlayerData.getOrCreatePlayer(userId);
                FishingManager.TrappingResult result = FishingManager.doTrap(player);
                event.getMessage().replyEmbeds(result.getEmbed()).queue();
                break;
            }

            // --- Profile & Stats ---
            case "profile":
            case "stats":
            case "me": {
                MessageEmbed embed = EconomyManager.getProfileEmbed(userId, username, avatarUrl);
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "leaderboard":
            case "lb":
            case "top": {
                MessageEmbed embed = EconomyManager.getLeaderboardEmbed();
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            case "ecohelp":
            case "economy":
            case "fishhelp": {
                MessageEmbed embed = EconomyManager.getHelpEmbed();
                event.getMessage().replyEmbeds(embed).queue();
                break;
            }

            default:
                // Unknown command — silently ignore to avoid spam
                break;
        }
    }
}

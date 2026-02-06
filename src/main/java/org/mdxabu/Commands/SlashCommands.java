package org.mdxabu.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mdxabu.Commands.Labs.CommandLab;
import org.mdxabu.Economy.EconomyManager;
import org.mdxabu.Economy.FishingManager;
import org.mdxabu.Models.PlayerData;

import java.util.Objects;

public class SlashCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }

        String userId = event.getUser().getId();
        String username = event.getUser().getEffectiveName();
        String avatarUrl = event.getUser().getEffectiveAvatarUrl();

        switch (event.getName()) {
            // --- Original Commands ---
            case "hello":
                event.reply("Hello, " + event.getUser().getAsMention()).queue();
                break;

            case "say":
                CommandLab.say(event, Objects.requireNonNull(event.getOption("content")).getAsString());
                break;

            case "write":
                CommandLab.writeName(event, Objects.requireNonNull(event.getOption("name")).getAsString());
                break;

            // --- Economy Commands ---
            case "balance":
                event.replyEmbeds(EconomyManager.getBalanceEmbed(userId, username, avatarUrl)).queue();
                break;

            case "daily":
                event.replyEmbeds(EconomyManager.claimDailyEmbed(userId, username)).queue();
                break;

            case "shop":
                event.replyEmbeds(EconomyManager.getShopEmbed()).queue();
                break;

            case "buy":
                String buyItem = Objects.requireNonNull(event.getOption("item")).getAsString();
                event.replyEmbeds(EconomyManager.buyItemEmbed(userId, username, buyItem)).queue();
                break;

            case "sell":
                String sellItem = Objects.requireNonNull(event.getOption("item")).getAsString();
                event.replyEmbeds(EconomyManager.sellItemEmbed(userId, username, sellItem)).queue();
                break;

            case "inventory":
                event.replyEmbeds(EconomyManager.getInventoryEmbed(userId, username, avatarUrl)).queue();
                break;

            // --- Fishing & Trapping ---
            case "fish":
                PlayerData fishPlayer = PlayerData.getOrCreatePlayer(userId);
                FishingManager.FishingResult fishResult = FishingManager.doFish(fishPlayer);
                event.replyEmbeds(fishResult.getEmbed()).queue();
                break;

            case "trap":
                PlayerData trapPlayer = PlayerData.getOrCreatePlayer(userId);
                FishingManager.TrappingResult trapResult = FishingManager.doTrap(trapPlayer);
                event.replyEmbeds(trapResult.getEmbed()).queue();
                break;

            // --- Profile & Stats ---
            case "profile":
                event.replyEmbeds(EconomyManager.getProfileEmbed(userId, username, avatarUrl)).queue();
                break;

            case "leaderboard":
                event.replyEmbeds(EconomyManager.getLeaderboardEmbed()).queue();
                break;

            case "ecohelp":
                event.replyEmbeds(EconomyManager.getHelpEmbed()).queue();
                break;

            default:
                event.reply("There is no command like this...").queue();
        }
    }
}

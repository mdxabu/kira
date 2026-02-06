package org.mdxabu.Economy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mdxabu.Models.PlayerData;
import org.mdxabu.Models.ShopItem;

import java.awt.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EconomyManager {

    // =====================================================
    //  BALANCE
    // =====================================================

    /**
     * Builds an embed showing the player's current balance.
     */
    public static MessageEmbed getBalanceEmbed(String userId, String username, String avatarUrl) {
        PlayerData player = PlayerData.getOrCreatePlayer(userId);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDCB0 Wallet — " + username);
        embed.setThumbnail(avatarUrl);
        embed.setColor(new Color(255, 215, 0));
        embed.addField("\uD83D\uDCB3 Balance", "**" + player.getBalance() + "** coins", true);
        embed.addField("\uD83D\uDCCA Total Earned", "**" + player.getTotalEarned() + "** coins", true);
        embed.setFooter("Use /daily to claim free coins • /shop to spend them");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  DAILY
    // =====================================================

    /**
     * Attempts to claim daily coins and returns an embed with the result.
     */
    public static MessageEmbed claimDailyEmbed(String userId, String username) {
        PlayerData player = PlayerData.getOrCreatePlayer(userId);
        long claimed = player.claimDaily();

        EmbedBuilder embed = new EmbedBuilder();

        if (claimed > 0) {
            embed.setTitle("\u2705 Daily Coins Claimed!");
            embed.setColor(new Color(87, 242, 135));
            embed.setDescription(
                    "**" + username + "** collected their daily reward!\n\n" +
                    "\uD83D\uDCB0 **+" + claimed + " coins** added to your wallet.\n" +
                    "\uD83D\uDCB3 New Balance: **" + player.getBalance() + " coins**"
            );
            embed.setFooter("Come back in 24 hours for more! • /shop to spend coins");
        } else {
            long remainingMs = player.getRemainingDailyCooldown();
            String timeLeft = formatCooldown(remainingMs);

            embed.setTitle("\u23F3 Daily Cooldown");
            embed.setColor(new Color(237, 66, 69));
            embed.setDescription(
                    "You've already claimed your daily coins!\n\n" +
                    "\u23F0 Time remaining: **" + timeLeft + "**\n" +
                    "\uD83D\uDCB3 Current Balance: **" + player.getBalance() + " coins**"
            );
            embed.setFooter("Try /fish or /trap to earn more coins in the meantime!");
        }

        embed.setTimestamp(Instant.now());
        return embed.build();
    }

    // =====================================================
    //  SHOP
    // =====================================================

    /**
     * Builds the full shop listing embed, organized by category.
     */
    public static MessageEmbed getShopEmbed() {
        Map<String, ShopItem> allItems = ShopItem.getAllItems();

        // Group items by category
        Map<String, StringBuilder> categories = new LinkedHashMap<>();
        for (ShopItem item : allItems.values()) {
            categories.computeIfAbsent(item.getCategory(), k -> new StringBuilder())
                    .append(item.toShopDisplay())
                    .append("\n  \u2514 ID: `")
                    .append(item.getId())
                    .append("`\n\n");
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDED2 Kira's Shop");
        embed.setColor(new Color(88, 101, 242));
        embed.setDescription("Welcome to the shop! Use `/buy <item_id>` or `?buy <item_id>` to purchase.\n\u200B");

        for (Map.Entry<String, StringBuilder> entry : categories.entrySet()) {
            String categoryEmoji = getCategoryEmoji(entry.getKey());
            embed.addField(categoryEmoji + " " + entry.getKey(), entry.getValue().toString(), false);
        }

        embed.setFooter("Use /balance to check your coins • /daily for free coins");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  BUY
    // =====================================================

    /**
     * Attempts to purchase an item for the player and returns a result embed.
     */
    public static MessageEmbed buyItemEmbed(String userId, String username, String itemQuery) {
        if (itemQuery == null || itemQuery.isBlank()) {
            return buildErrorEmbed("Missing Item",
                    "Please specify an item to buy!\n\n**Usage:** `/buy <item_id>`\n**Example:** `/buy fishing_rod`\n\nUse `/shop` to see available items.");
        }

        ShopItem item = ShopItem.getByName(itemQuery);
        if (item == null) {
            return buildErrorEmbed("Item Not Found",
                    "Could not find an item matching **\"" + itemQuery + "\"**.\n\nUse `/shop` to see all available items and their IDs.");
        }

        PlayerData player = PlayerData.getOrCreatePlayer(userId);

        // Check if player can afford it
        if (player.getBalance() < item.getPrice()) {
            long deficit = item.getPrice() - player.getBalance();
            return buildErrorEmbed("Not Enough Coins",
                    "You don't have enough coins to buy **" + item.getName() + "**!\n\n" +
                    "\uD83D\uDCB3 Your Balance: **" + player.getBalance() + " coins**\n" +
                    "\uD83C\uDFF7\uFE0F Item Price: **" + item.getPrice() + " coins**\n" +
                    "\u274C Short by: **" + deficit + " coins**\n\n" +
                    "Use `/daily` or `/fish` to earn more coins!");
        }

        // Deduct and add item
        player.deductBalance(item.getPrice());
        player.addItem(item.getId());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\u2705 Purchase Successful!");
        embed.setColor(new Color(87, 242, 135));
        embed.setDescription(
                "**" + username + "** bought " + item.getEmoji() + " **" + item.getName() + "**!\n\n" +
                "\uD83D\uDCB0 Spent: **" + item.getPrice() + " coins**\n" +
                "\uD83D\uDCB3 Remaining Balance: **" + player.getBalance() + " coins**\n\n" +
                "\u2139\uFE0F " + item.getDescription()
        );
        embed.setFooter("Item added to your inventory • Use /inventory to check");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  SELL
    // =====================================================

    /**
     * Sells an item from the player's inventory for 50% of its shop price.
     */
    public static MessageEmbed sellItemEmbed(String userId, String username, String itemQuery) {
        if (itemQuery == null || itemQuery.isBlank()) {
            return buildErrorEmbed("Missing Item",
                    "Please specify an item to sell!\n\n**Usage:** `/sell <item_id>`\n**Example:** `/sell fishing_rod`\n\nUse `/inventory` to see your items.");
        }

        ShopItem item = ShopItem.getByName(itemQuery);
        if (item == null) {
            return buildErrorEmbed("Item Not Found",
                    "Could not find an item matching **\"" + itemQuery + "\"**.\n\nUse `/inventory` to see your items.");
        }

        PlayerData player = PlayerData.getOrCreatePlayer(userId);

        if (!player.hasItem(item.getId())) {
            return buildErrorEmbed("Item Not In Inventory",
                    "You don't own **" + item.getName() + "**!\n\nUse `/inventory` to see your items.");
        }

        long sellPrice = item.getPrice() / 2;
        player.removeItem(item.getId());
        player.addBalance(sellPrice);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDCE4 Item Sold!");
        embed.setColor(new Color(250, 166, 26));
        embed.setDescription(
                "**" + username + "** sold " + item.getEmoji() + " **" + item.getName() + "**!\n\n" +
                "\uD83D\uDCB0 Earned: **" + sellPrice + " coins** (50% of shop price)\n" +
                "\uD83D\uDCB3 New Balance: **" + player.getBalance() + " coins**"
        );
        embed.setFooter("Use /shop to buy new items • /inventory to check remaining items");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  INVENTORY
    // =====================================================

    /**
     * Builds an embed showing the player's full inventory.
     */
    public static MessageEmbed getInventoryEmbed(String userId, String username, String avatarUrl) {
        PlayerData player = PlayerData.getOrCreatePlayer(userId);
        List<String> inventory = player.getInventory();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83C\uDF92 Inventory — " + username);
        embed.setThumbnail(avatarUrl);
        embed.setColor(new Color(114, 137, 218));
        embed.setTimestamp(Instant.now());

        if (inventory.isEmpty()) {
            embed.setDescription(
                    "\uD83D\uDCED Your inventory is empty!\n\n" +
                    "Visit the `/shop` to buy your first items.\n" +
                    "You need a **Fishing Rod** to fish and a **Food Trap** to trap animals!"
            );
        } else {
            // Count duplicates
            Map<String, Integer> itemCounts = new LinkedHashMap<>();
            for (String itemId : inventory) {
                itemCounts.merge(itemId, 1, Integer::sum);
            }

            StringBuilder desc = new StringBuilder();
            int totalItems = 0;

            for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                ShopItem item = ShopItem.getById(entry.getKey());
                int count = entry.getValue();
                totalItems += count;

                if (item != null) {
                    desc.append(item.getEmoji())
                        .append(" **")
                        .append(item.getName())
                        .append("**");
                    if (count > 1) {
                        desc.append(" x").append(count);
                    }
                    desc.append(" — *").append(item.getDescription()).append("*\n");
                } else {
                    desc.append("\u2753 **").append(entry.getKey()).append("**");
                    if (count > 1) {
                        desc.append(" x").append(count);
                    }
                    desc.append("\n");
                }
            }

            embed.setDescription(desc.toString());
            embed.setFooter("Total Items: " + totalItems + " • Use /sell <item> to sell items");
        }

        return embed.build();
    }

    // =====================================================
    //  PROFILE
    // =====================================================

    /**
     * Builds a comprehensive profile embed showing all player stats.
     */
    public static MessageEmbed getProfileEmbed(String userId, String username, String avatarUrl) {
        PlayerData player = PlayerData.getOrCreatePlayer(userId);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDC64 Profile — " + username);
        embed.setThumbnail(avatarUrl);
        embed.setColor(new Color(88, 101, 242));

        // Economy stats
        embed.addField("\uD83D\uDCB0 Economy", String.join("\n",
                "\uD83D\uDCB3 Balance: **" + player.getBalance() + "** coins",
                "\uD83D\uDCCA Total Earned: **" + player.getTotalEarned() + "** coins"
        ), true);

        // Hunting stats
        embed.addField("\uD83C\uDFA3 Stats", String.join("\n",
                "\uD83D\uDC1F Fish Caught: **" + player.getFishCaught() + "**",
                "\uD83D\uDC3E Animals Trapped: **" + player.getTrapsCaught() + "**"
        ), true);

        // Inventory summary
        List<String> inventory = player.getInventory();
        int uniqueItems = (int) inventory.stream().distinct().count();
        embed.addField("\uD83C\uDF92 Inventory", String.join("\n",
                "\uD83D\uDCE6 Total Items: **" + inventory.size() + "**",
                "\uD83C\uDFAD Unique Items: **" + uniqueItems + "**"
        ), true);

        // Gear status
        StringBuilder gearStatus = new StringBuilder();
        gearStatus.append(player.hasItem("fishing_rod") || player.hasItem("advanced_rod") ? "\u2705" : "\u274C")
                .append(" Fishing Rod\n");
        gearStatus.append(player.hasItem("food_trap") || player.hasItem("advanced_trap") ? "\u2705" : "\u274C")
                .append(" Food Trap\n");
        gearStatus.append(player.hasItem("fish_net") ? "\u2705" : "\u274C")
                .append(" Fish Net\n");
        gearStatus.append(player.hasItem("premium_bait") ? "\u2705" : "\u274C")
                .append(" Premium Bait\n");
        gearStatus.append(player.hasItem("lucky_charm") ? "\u2705" : "\u274C")
                .append(" Lucky Charm\n");

        embed.addField("\u2699\uFE0F Gear Status", gearStatus.toString(), false);

        embed.setFooter("Keep fishing and trapping to climb the leaderboard!");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  HELP
    // =====================================================

    /**
     * Builds a help embed listing all economy and fishing commands.
     */
    public static MessageEmbed getHelpEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDCD6 Kira Economy & Fishing — Help");
        embed.setColor(new Color(88, 101, 242));

        embed.addField("\uD83D\uDCB0 Economy Commands", String.join("\n",
                "`/balance` or `?balance` — Check your coin balance",
                "`/daily` or `?daily` — Claim 200 free coins (24h cooldown)",
                "`/profile` or `?profile` — View your full profile & stats"
        ), false);

        embed.addField("\uD83D\uDED2 Shop Commands", String.join("\n",
                "`/shop` or `?shop` — Browse the item shop",
                "`/buy <item>` or `?buy <item>` — Purchase an item",
                "`/sell <item>` or `?sell <item>` — Sell an item (50% price)",
                "`/inventory` or `?inventory` — View your inventory"
        ), false);

        embed.addField("\uD83C\uDFA3 Fishing & Trapping", String.join("\n",
                "`/fish` or `?fish` — Go fishing (requires Fishing Rod)",
                "`/trap` or `?trap` — Check your food trap (requires Food Trap)",
                "",
                "**Tip:** Buy a **Fishing Rod** and **Food Trap** from the shop first!",
                "Use **Premium Bait** for rarer fish, **Lucky Charm** to double earnings!"
        ), false);

        embed.addField("\uD83C\uDFAF Getting Started", String.join("\n",
                "1\uFE0F\u20E3 You start with **500 coins** as an entry bonus!",
                "2\uFE0F\u20E3 Use `/shop` to view items and `/buy fishing_rod` to start fishing.",
                "3\uFE0F\u20E3 Use `/fish` to catch fish and earn coins!",
                "4\uFE0F\u20E3 Claim `/daily` every 24 hours for free coins.",
                "5\uFE0F\u20E3 Upgrade your gear for better catches!"
        ), false);

        embed.setFooter("Kira Bot • Economy & Fishing System");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  LEADERBOARD
    // =====================================================

    /**
     * Builds a leaderboard embed. (Fetches top 10 players by balance.)
     */
    public static MessageEmbed getLeaderboardEmbed() {
        // Fetch top 10 from MongoDB sorted by balance descending
        var collection = org.mdxabu.Database.MongoManager.getInstance().getPlayersCollection();
        var topPlayers = collection.find()
                .sort(new org.bson.Document("balance", -1))
                .limit(10)
                .into(new java.util.ArrayList<>());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83C\uDFC6 Leaderboard — Top 10 Richest Players");
        embed.setColor(new Color(255, 215, 0));

        if (topPlayers.isEmpty()) {
            embed.setDescription("No players found yet! Be the first to start earning.");
        } else {
            StringBuilder desc = new StringBuilder();
            int rank = 1;

            for (var doc : topPlayers) {
                String odUserId = doc.getString("userId");
                long balance = doc.getLong("balance");
                int fishCaught = doc.getInteger("fishCaught", 0);

                String medal = switch (rank) {
                    case 1 -> "\uD83E\uDD47";
                    case 2 -> "\uD83E\uDD48";
                    case 3 -> "\uD83E\uDD49";
                    default -> "**#" + rank + "**";
                };

                desc.append(medal)
                    .append(" <@").append(odUserId).append("> — **")
                    .append(balance).append("** coins")
                    .append(" | \uD83D\uDC1F ").append(fishCaught)
                    .append("\n");
                rank++;
            }

            embed.setDescription(desc.toString());
        }

        embed.setFooter("Use /fish and /trap to climb the ranks!");
        embed.setTimestamp(Instant.now());

        return embed.build();
    }

    // =====================================================
    //  HELPERS
    // =====================================================

    /**
     * Formats a cooldown in milliseconds into a human-readable string.
     */
    private static String formatCooldown(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    /**
     * Returns an emoji for a shop category.
     */
    private static String getCategoryEmoji(String category) {
        return switch (category.toLowerCase()) {
            case "fishing" -> "\uD83C\uDFA3";
            case "trapping" -> "\uD83E\uDE64";
            case "consumables" -> "\uD83E\uDDEA";
            default -> "\uD83D\uDCE6";
        };
    }

    /**
     * Builds a standard error embed.
     */
    private static MessageEmbed buildErrorEmbed(String title, String description) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\u274C " + title);
        embed.setDescription(description);
        embed.setColor(new Color(237, 66, 69));
        embed.setFooter("Need help? Use /ecohelp for all commands");
        embed.setTimestamp(Instant.now());
        return embed.build();
    }
}

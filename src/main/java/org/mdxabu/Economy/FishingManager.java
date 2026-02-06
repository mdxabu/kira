package org.mdxabu.Economy;

import java.awt.*;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mdxabu.Models.PlayerData;

public class FishingManager {

    private static final Random random = new Random();

    // --- Fish Types ---
    public enum FishType {
        TRASH("Old Boot", "\uD83E\uDD7E", 1, 5, 35.0),
        SEAWEED("Seaweed", "\uD83C\uDF3F", 2, 8, 15.0),
        COMMON_FISH("Common Fish", "\uD83D\uDC1F", 10, 30, 25.0),
        RARE_FISH("Rare Fish", "\uD83D\uDC20", 50, 100, 13.0),
        TROPICAL_FISH("Tropical Fish", "\uD83D\uDC21", 80, 150, 5.0),
        EPIC_FISH("Epic Fish", "\uD83E\uDDA5", 150, 300, 4.0),
        GOLDEN_FISH("Golden Fish", "\u2B50", 300, 500, 2.0),
        LEGENDARY_FISH("Legendary Fish", "\uD83D\uDC09", 500, 1000, 0.8),
        MYTHICAL_FISH(
            "Mythical Kraken Tentacle",
            "\uD83D\uDC19",
            1000,
            2000,
            0.2
        );

        private final String name;
        private final String emoji;
        private final int minValue;
        private final int maxValue;
        private final double baseChance; // percentage

        FishType(
            String name,
            String emoji,
            int minValue,
            int maxValue,
            double baseChance
        ) {
            this.name = name;
            this.emoji = emoji;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.baseChance = baseChance;
        }

        public String getName() {
            return name;
        }

        public String getEmoji() {
            return emoji;
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public double getBaseChance() {
            return baseChance;
        }

        public int rollValue() {
            return random.nextInt(maxValue - minValue + 1) + minValue;
        }
    }

    // --- Animal/Trap Types ---
    public enum TrapCatch {
        NOTHING("Nothing", "\uD83D\uDCA8", 0, 0, 20.0),
        MOUSE("Mouse", "\uD83D\uDC01", 10, 25, 25.0),
        RABBIT("Rabbit", "\uD83D\uDC07", 20, 50, 22.0),
        SQUIRREL("Squirrel", "\uD83D\uDC3F\uFE0F", 30, 60, 13.0),
        FOX("Fox", "\uD83E\uDD8A", 60, 120, 8.0),
        WILD_BOAR("Wild Boar", "\uD83D\uDC17", 100, 200, 5.0),
        DEER("Deer", "\uD83E\uDD8C", 150, 250, 4.0),
        BEAR("Bear", "\uD83D\uDC3B", 200, 400, 2.0),
        LEGENDARY_PHOENIX("Phoenix Feather", "\uD83E\uDD85", 500, 1000, 0.8),
        MYTHICAL_UNICORN("Unicorn Horn", "\uD83E\uDD84", 800, 1500, 0.2);

        private final String name;
        private final String emoji;
        private final int minValue;
        private final int maxValue;
        private final double baseChance;

        TrapCatch(
            String name,
            String emoji,
            int minValue,
            int maxValue,
            double baseChance
        ) {
            this.name = name;
            this.emoji = emoji;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.baseChance = baseChance;
        }

        public String getName() {
            return name;
        }

        public String getEmoji() {
            return emoji;
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public double getBaseChance() {
            return baseChance;
        }

        public int rollValue() {
            if (minValue == 0 && maxValue == 0) return 0;
            return random.nextInt(maxValue - minValue + 1) + minValue;
        }
    }

    // --- Rarity Tiers (for embed color) ---
    public enum Rarity {
        JUNK(new Color(128, 128, 128)),
        COMMON(new Color(170, 210, 170)),
        RARE(new Color(70, 130, 230)),
        EPIC(new Color(163, 53, 238)),
        LEGENDARY(new Color(255, 165, 0)),
        MYTHICAL(new Color(255, 0, 80));

        private final Color color;

        Rarity(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    // =====================================================
    //  FISHING
    // =====================================================

    /**
     * Attempts to fish. Returns an embed result.
     * Requires the player to own a fishing_rod or advanced_rod.
     * Premium bait and lucky charm are consumed if present.
     */
    public static FishingResult doFish(PlayerData player) {
        // Check for rod
        boolean hasAdvancedRod = player.hasItem("advanced_rod");
        boolean hasBasicRod = player.hasItem("fishing_rod");

        if (!hasAdvancedRod && !hasBasicRod) {
            return new FishingResult(
                null,
                0,
                false,
                buildErrorEmbed(
                    "No Fishing Rod!",
                    "You need a **Fishing Rod** to fish!\nBuy one from the shop using `/shop` and `/buy fishing_rod`."
                )
            );
        }

        // Check consumables
        boolean hasBait = player.hasItem("premium_bait");
        boolean hasLucky = player.hasItem("lucky_charm");
        boolean hasNet = player.hasItem("fish_net");

        // Consume single-use items
        if (hasBait) player.removeItem("premium_bait");
        if (hasLucky) player.removeItem("lucky_charm");

        // Roll the catch
        FishType caught = rollFish(hasAdvancedRod, hasBait);
        int value = caught.rollValue();

        // Lucky charm doubles earnings
        if (hasLucky && value > 0) {
            value *= 2;
        }

        // Net bonus: chance to catch a second fish
        int bonusValue = 0;
        FishType bonusFish = null;
        if (hasNet && random.nextInt(100) < 40) {
            bonusFish = rollFish(hasAdvancedRod, false);
            bonusValue = bonusFish.rollValue();
            if (hasLucky) bonusValue *= 2;
        }

        int totalValue = value + bonusValue;

        // Update player data
        if (totalValue > 0) {
            player.addBalance(totalValue);
        }
        player.incrementFishCaught();

        // Build result embed
        Rarity rarity = getFishRarity(caught);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83C\uDFA3 Fishing Result");
        embed.setColor(rarity.getColor());

        StringBuilder desc = new StringBuilder();
        desc
            .append("You cast your ")
            .append(
                hasAdvancedRod ? "**Advanced Fishing Rod**" : "**Fishing Rod**"
            )
            .append(" into the water...\n\n");
        desc
            .append(caught.getEmoji())
            .append(" You caught a **")
            .append(caught.getName())
            .append("**!\n");
        desc
            .append("\uD83D\uDCB0 Value: **")
            .append(value)
            .append(" coins**\n");

        if (bonusFish != null) {
            desc.append("\n\uD83E\uDD4D **Fish Net Bonus!**\n");
            desc
                .append(bonusFish.getEmoji())
                .append(" You also netted a **")
                .append(bonusFish.getName())
                .append("**!\n");
            desc
                .append("\uD83D\uDCB0 Bonus: **")
                .append(bonusValue)
                .append(" coins**\n");
        }

        if (hasLucky) {
            desc.append(
                "\n\uD83C\uDF40 **Lucky Charm** doubled your earnings!"
            );
        }
        if (hasBait) {
            desc.append(
                "\n\uD83E\uDEB1 **Premium Bait** was used to attract rarer fish!"
            );
        }

        desc
            .append("\n\n\uD83D\uDCB0 **Total Earned: ")
            .append(totalValue)
            .append(" coins**");
        desc
            .append("\n\uD83D\uDCB3 New Balance: **")
            .append(player.getBalance())
            .append(" coins**");
        desc
            .append("\n\uD83D\uDC1F Total Fish Caught: **")
            .append(player.getFishCaught())
            .append("**");

        embed.setDescription(desc.toString());
        embed.setFooter(
            "Use /shop to buy better gear! • /daily for free coins"
        );

        return new FishingResult(caught, totalValue, true, embed.build());
    }

    /**
     * Rolls a random fish based on weighted probabilities.
     * Advanced rod and bait shift probabilities toward rarer catches.
     */
    private static FishType rollFish(boolean advancedRod, boolean premiumBait) {
        double[] weights = new double[FishType.values().length];
        double totalWeight = 0;

        for (int i = 0; i < FishType.values().length; i++) {
            FishType fish = FishType.values()[i];
            double weight = fish.getBaseChance();

            if (advancedRod) {
                // Advanced rod: reduce junk/common, boost rare+
                if (fish == FishType.TRASH || fish == FishType.SEAWEED) {
                    weight *= 0.5;
                } else if (fish.getMinValue() >= 50) {
                    weight *= 1.8;
                }
            }

            if (premiumBait) {
                // Bait: further reduce junk, boost rare+
                if (fish == FishType.TRASH || fish == FishType.SEAWEED) {
                    weight *= 0.4;
                } else if (fish.getMinValue() >= 80) {
                    weight *= 2.0;
                }
            }

            weights[i] = weight;
            totalWeight += weight;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0;

        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll <= cumulative) {
                return FishType.values()[i];
            }
        }

        return FishType.COMMON_FISH; // fallback
    }

    private static Rarity getFishRarity(FishType fish) {
        return switch (fish) {
            case TRASH, SEAWEED -> Rarity.JUNK;
            case COMMON_FISH -> Rarity.COMMON;
            case RARE_FISH, TROPICAL_FISH -> Rarity.RARE;
            case EPIC_FISH, GOLDEN_FISH -> Rarity.EPIC;
            case LEGENDARY_FISH -> Rarity.LEGENDARY;
            case MYTHICAL_FISH -> Rarity.MYTHICAL;
        };
    }

    // =====================================================
    //  TRAPPING
    // =====================================================

    /**
     * Attempts to use a food trap. Returns an embed result.
     * Requires the player to own a food_trap or advanced_trap.
     */
    public static TrappingResult doTrap(PlayerData player) {
        boolean hasAdvancedTrap = player.hasItem("advanced_trap");
        boolean hasBasicTrap = player.hasItem("food_trap");

        if (!hasAdvancedTrap && !hasBasicTrap) {
            return new TrappingResult(
                null,
                0,
                false,
                buildErrorEmbed(
                    "No Food Trap!",
                    "You need a **Food Trap** to catch animals!\nBuy one from the shop using `/shop` and `/buy food_trap`."
                )
            );
        }

        boolean hasLucky = player.hasItem("lucky_charm");
        if (hasLucky) player.removeItem("lucky_charm");

        // Roll the catch
        TrapCatch caught = rollTrap(hasAdvancedTrap);
        int value = caught.rollValue();

        if (hasLucky && value > 0) {
            value *= 2;
        }

        // Update player
        if (value > 0) {
            player.addBalance(value);
        }
        if (caught != TrapCatch.NOTHING) {
            player.incrementTrapsCaught();
        }

        Rarity rarity = getTrapRarity(caught);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83E\uDE64 Trapping Result");
        embed.setColor(rarity.getColor());

        StringBuilder desc = new StringBuilder();
        desc
            .append("You check your ")
            .append(
                hasAdvancedTrap ? "**Advanced Food Trap**" : "**Food Trap**"
            )
            .append("...\n\n");

        if (caught == TrapCatch.NOTHING) {
            desc.append(
                "\uD83D\uDCA8 The trap was empty! Nothing was caught.\n"
            );
            desc.append("Better luck next time!\n");
        } else {
            desc
                .append(caught.getEmoji())
                .append(" You caught a **")
                .append(caught.getName())
                .append("**!\n");
            desc
                .append("\uD83D\uDCB0 Value: **")
                .append(value)
                .append(" coins**\n");
        }

        if (hasLucky && value > 0) {
            desc.append(
                "\n\uD83C\uDF40 **Lucky Charm** doubled your earnings!"
            );
        }

        desc
            .append("\n\n\uD83D\uDCB0 **Total Earned: ")
            .append(value)
            .append(" coins**");
        desc
            .append("\n\uD83D\uDCB3 New Balance: **")
            .append(player.getBalance())
            .append(" coins**");
        desc
            .append("\n\uD83D\uDC3E Total Animals Caught: **")
            .append(player.getTrapsCaught())
            .append("**");

        embed.setDescription(desc.toString());
        embed.setFooter(
            "Use /shop to buy better gear! • /daily for free coins"
        );

        return new TrappingResult(caught, value, true, embed.build());
    }

    private static TrapCatch rollTrap(boolean advancedTrap) {
        double[] weights = new double[TrapCatch.values().length];
        double totalWeight = 0;

        for (int i = 0; i < TrapCatch.values().length; i++) {
            TrapCatch animal = TrapCatch.values()[i];
            double weight = animal.getBaseChance();

            if (advancedTrap) {
                if (animal == TrapCatch.NOTHING) {
                    weight *= 0.4;
                } else if (animal.getMinValue() >= 60) {
                    weight *= 2.0;
                }
            }

            weights[i] = weight;
            totalWeight += weight;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0;

        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll <= cumulative) {
                return TrapCatch.values()[i];
            }
        }

        return TrapCatch.MOUSE; // fallback
    }

    private static Rarity getTrapRarity(TrapCatch animal) {
        return switch (animal) {
            case NOTHING -> Rarity.JUNK;
            case MOUSE, RABBIT -> Rarity.COMMON;
            case SQUIRREL, FOX -> Rarity.RARE;
            case WILD_BOAR, DEER -> Rarity.EPIC;
            case BEAR -> Rarity.LEGENDARY;
            case LEGENDARY_PHOENIX, MYTHICAL_UNICORN -> Rarity.MYTHICAL;
        };
    }

    // =====================================================
    //  RESULT RECORDS
    // =====================================================

    public static class FishingResult {

        private final FishType fishType;
        private final int coinsEarned;
        private final boolean success;
        private final MessageEmbed embed;

        public FishingResult(
            FishType fishType,
            int coinsEarned,
            boolean success,
            MessageEmbed embed
        ) {
            this.fishType = fishType;
            this.coinsEarned = coinsEarned;
            this.success = success;
            this.embed = embed;
        }

        public FishType getFishType() {
            return fishType;
        }

        public int getCoinsEarned() {
            return coinsEarned;
        }

        public boolean isSuccess() {
            return success;
        }

        public MessageEmbed getEmbed() {
            return embed;
        }
    }

    public static class TrappingResult {

        private final TrapCatch trapCatch;
        private final int coinsEarned;
        private final boolean success;
        private final MessageEmbed embed;

        public TrappingResult(
            TrapCatch trapCatch,
            int coinsEarned,
            boolean success,
            MessageEmbed embed
        ) {
            this.trapCatch = trapCatch;
            this.coinsEarned = coinsEarned;
            this.success = success;
            this.embed = embed;
        }

        public TrapCatch getTrapCatch() {
            return trapCatch;
        }

        public int getCoinsEarned() {
            return coinsEarned;
        }

        public boolean isSuccess() {
            return success;
        }

        public MessageEmbed getEmbed() {
            return embed;
        }
    }

    // =====================================================
    //  HELPERS
    // =====================================================

    private static MessageEmbed buildErrorEmbed(
        String title,
        String description
    ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\u274C " + title);
        embed.setDescription(description);
        embed.setColor(new Color(237, 66, 69));
        embed.setFooter("Use /shop to browse items • /buy <item> to purchase");
        return embed.build();
    }
}

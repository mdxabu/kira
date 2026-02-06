package org.mdxabu.Models;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShopItem {

    private final String id;
    private final String name;
    private final String description;
    private final long price;
    private final String emoji;
    private final String category;

    // Master registry of all shop items
    private static final Map<String, ShopItem> SHOP_ITEMS = new LinkedHashMap<>();

    static {
        // --- Fishing Equipment ---
        register(new ShopItem("fishing_rod", "Fishing Rod", "A basic fishing rod to catch fish. Essential for fishing!", 200, "\uD83C\uDFA3", "Fishing"));
        register(new ShopItem("advanced_rod", "Advanced Fishing Rod", "An upgraded rod with better catch rates and rarer fish!", 500, "\uD83E\uDDEF", "Fishing"));
        register(new ShopItem("premium_bait", "Premium Bait", "Increases your chance of catching rare fish. Single use.", 100, "\uD83E\uDEB1", "Fishing"));
        register(new ShopItem("fish_net", "Fish Net", "Cast a wide net to catch multiple fish at once!", 350, "\uD83E\uDD4D", "Fishing"));

        // --- Trapping Equipment ---
        register(new ShopItem("food_trap", "Food Trap", "A trap baited with food to catch small animals. Essential for trapping!", 150, "\uD83E\uDE64", "Trapping"));
        register(new ShopItem("advanced_trap", "Advanced Food Trap", "A reinforced trap that catches bigger and rarer animals!", 400, "\u2699\uFE0F", "Trapping"));

        // --- Consumables ---
        register(new ShopItem("lucky_charm", "Lucky Charm", "Doubles your earnings from the next catch. Single use.", 250, "\uD83C\uDF40", "Consumables"));
        register(new ShopItem("energy_drink", "Energy Drink", "Removes the cooldown on your next fishing or trapping attempt. Single use.", 175, "\u26A1", "Consumables"));
    }

    public ShopItem(String id, String name, String description, long price, String emoji, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.emoji = emoji;
        this.category = category;
    }

    private static void register(ShopItem item) {
        SHOP_ITEMS.put(item.getId(), item);
    }

    // --- Static Lookup Methods ---

    /**
     * Returns all shop items in insertion order.
     */
    public static Map<String, ShopItem> getAllItems() {
        return new LinkedHashMap<>(SHOP_ITEMS);
    }

    /**
     * Gets a shop item by its unique id.
     * Returns null if not found.
     */
    public static ShopItem getById(String id) {
        return SHOP_ITEMS.get(id.toLowerCase());
    }

    /**
     * Searches for a shop item by name (case-insensitive, partial match).
     * Returns the first match, or null if not found.
     */
    public static ShopItem getByName(String name) {
        String lowerName = name.toLowerCase().trim();

        // Try exact id match first
        ShopItem exact = SHOP_ITEMS.get(lowerName);
        if (exact != null) return exact;

        // Try exact name match (case-insensitive)
        for (ShopItem item : SHOP_ITEMS.values()) {
            if (item.getName().equalsIgnoreCase(lowerName)) {
                return item;
            }
        }

        // Try partial name match
        for (ShopItem item : SHOP_ITEMS.values()) {
            if (item.getName().toLowerCase().contains(lowerName) || item.getId().contains(lowerName)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Returns all items that belong to a specific category.
     */
    public static Map<String, ShopItem> getByCategory(String category) {
        Map<String, ShopItem> result = new LinkedHashMap<>();
        for (Map.Entry<String, ShopItem> entry : SHOP_ITEMS.entrySet()) {
            if (entry.getValue().getCategory().equalsIgnoreCase(category)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    // --- Getters ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getCategory() {
        return category;
    }

    /**
     * Formatted display string for shop listing.
     */
    public String toShopDisplay() {
        return emoji + " **" + name + "** — `" + price + " coins`\n" +
                "  ↳ " + description;
    }

    /**
     * Compact display string for inventory listing.
     */
    public String toInventoryDisplay() {
        return emoji + " " + name;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }
}

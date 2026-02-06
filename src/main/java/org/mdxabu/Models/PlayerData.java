package org.mdxabu.Models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.mdxabu.Database.MongoManager;

public class PlayerData {

    private static final long ENTRY_BONUS = 500L;
    private static final long DAILY_COINS = 200L;
    private static final long DAILY_COOLDOWN_MS = 24 * 60 * 60 * 1000L; // 24 hours

    private final String odUserId;
    private long balance;
    private List<String> inventory;
    private long lastDaily;
    private int fishCaught;
    private int trapsCaught;
    private long totalEarned;

    public PlayerData(
        String userId,
        long balance,
        List<String> inventory,
        long lastDaily,
        int fishCaught,
        int trapsCaught,
        long totalEarned
    ) {
        this.odUserId = userId;
        this.balance = balance;
        this.inventory = inventory != null ? inventory : new ArrayList<>();
        this.lastDaily = lastDaily;
        this.fishCaught = fishCaught;
        this.trapsCaught = trapsCaught;
        this.totalEarned = totalEarned;
    }

    // --- Static Factory / DB Methods ---

    /**
     * Fetches a player from MongoDB. If the player doesn't exist, creates a new
     * record with the entry bonus of 500 coins.
     */
    public static PlayerData getOrCreatePlayer(String userId) {
        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        Document doc = collection.find(Filters.eq("userId", userId)).first();

        if (doc != null) {
            return fromDocument(doc);
        }

        // New player — grant entry bonus
        Document newPlayer = new Document("userId", userId)
            .append("balance", ENTRY_BONUS)
            .append("inventory", new ArrayList<String>())
            .append("lastDaily", 0L)
            .append("fishCaught", 0)
            .append("trapsCaught", 0)
            .append("totalEarned", ENTRY_BONUS);

        collection.insertOne(newPlayer);

        return new PlayerData(
            userId,
            ENTRY_BONUS,
            new ArrayList<>(),
            0L,
            0,
            0,
            ENTRY_BONUS
        );
    }

    /**
     * Converts a MongoDB Document into a PlayerData instance.
     */
    public static PlayerData fromDocument(Document doc) {
        String odUserId = doc.getString("userId");
        long balance = doc.getLong("balance");
        List<String> inventory = doc.getList(
            "inventory",
            String.class,
            new ArrayList<>()
        );
        long lastDaily = doc.getLong("lastDaily");
        int fishCaught = doc.getInteger("fishCaught", 0);
        int trapsCaught = doc.getInteger("trapsCaught", 0);
        long totalEarned = doc.getLong("totalEarned");

        return new PlayerData(
            odUserId,
            balance,
            inventory,
            lastDaily,
            fishCaught,
            trapsCaught,
            totalEarned
        );
    }

    // --- Balance Operations ---

    public long getBalance() {
        return balance;
    }

    public void addBalance(long amount) {
        this.balance += amount;
        this.totalEarned += amount;
        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        collection.updateOne(
            Filters.eq("userId", odUserId),
            Updates.combine(
                Updates.inc("balance", amount),
                Updates.inc("totalEarned", amount)
            )
        );
    }

    public boolean deductBalance(long amount) {
        if (this.balance < amount) {
            return false;
        }
        this.balance -= amount;
        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        collection.updateOne(
            Filters.eq("userId", odUserId),
            Updates.inc("balance", -amount)
        );
        return true;
    }

    // --- Daily Claim ---

    /**
     * Attempts to claim daily coins.
     * Returns the amount claimed, or -1 if still on cooldown.
     * If on cooldown, call getRemainingDailyCooldown() for the time left.
     */
    public long claimDaily() {
        long now = System.currentTimeMillis();
        long elapsed = now - this.lastDaily;

        if (elapsed < DAILY_COOLDOWN_MS) {
            return -1;
        }

        this.lastDaily = now;
        this.balance += DAILY_COINS;
        this.totalEarned += DAILY_COINS;

        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        collection.updateOne(
            Filters.eq("userId", odUserId),
            Updates.combine(
                Updates.set("lastDaily", now),
                Updates.inc("balance", DAILY_COINS),
                Updates.inc("totalEarned", DAILY_COINS)
            )
        );

        return DAILY_COINS;
    }

    /**
     * Returns the remaining daily cooldown in milliseconds.
     */
    public long getRemainingDailyCooldown() {
        long now = System.currentTimeMillis();
        long elapsed = now - this.lastDaily;
        long remaining = DAILY_COOLDOWN_MS - elapsed;
        return Math.max(remaining, 0);
    }

    // --- Inventory Operations ---

    public List<String> getInventory() {
        return new ArrayList<>(inventory);
    }

    public boolean hasItem(String itemId) {
        return inventory.contains(itemId);
    }

    public int getItemCount(String itemId) {
        int count = 0;
        for (String item : inventory) {
            if (item.equals(itemId)) {
                count++;
            }
        }
        return count;
    }

    public void addItem(String itemId) {
        this.inventory.add(itemId);
        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        collection.updateOne(
            Filters.eq("userId", odUserId),
            Updates.push("inventory", itemId)
        );
    }

    public boolean removeItem(String itemId) {
        boolean removed = this.inventory.remove(itemId);
        if (removed) {
            // Pull one instance of the item, then re-set the full inventory
            // (MongoDB $pull removes ALL matching, so we re-set to keep correct count)
            MongoCollection<Document> collection =
                MongoManager.getInstance().getPlayersCollection();
            collection.updateOne(
                Filters.eq("userId", odUserId),
                Updates.set("inventory", this.inventory)
            );
        }
        return removed;
    }

    // --- Fish / Trap Stats ---

    public int getFishCaught() {
        return fishCaught;
    }

    public void incrementFishCaught() {
        this.fishCaught++;
        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        collection.updateOne(
            Filters.eq("userId", odUserId),
            Updates.inc("fishCaught", 1)
        );
    }

    public int getTrapsCaught() {
        return trapsCaught;
    }

    public void incrementTrapsCaught() {
        this.trapsCaught++;
        MongoCollection<Document> collection =
            MongoManager.getInstance().getPlayersCollection();
        collection.updateOne(
            Filters.eq("userId", odUserId),
            Updates.inc("trapsCaught", 1)
        );
    }

    // --- Getters ---

    public String getUserId() {
        return odUserId;
    }

    public long getLastDaily() {
        return lastDaily;
    }

    public long getTotalEarned() {
        return totalEarned;
    }

    public static long getEntryBonus() {
        return ENTRY_BONUS;
    }

    public static long getDailyCoins() {
        return DAILY_COINS;
    }

    @Override
    public String toString() {
        return (
            "PlayerData{" +
            "userId='" +
            odUserId +
            '\'' +
            ", balance=" +
            balance +
            ", inventory=" +
            inventory +
            ", fishCaught=" +
            fishCaught +
            ", trapsCaught=" +
            trapsCaught +
            ", totalEarned=" +
            totalEarned +
            '}'
        );
    }
}

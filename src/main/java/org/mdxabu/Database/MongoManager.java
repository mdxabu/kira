package org.mdxabu.Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoManager {

    private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

    private static MongoManager instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private static final String DATABASE_NAME = "kira_bot";
    private static final String PLAYERS_COLLECTION = "players";

    private MongoManager(String connectionUri) {
        logger.info("Connecting to MongoDB...");
        this.mongoClient = MongoClients.create(connectionUri);
        this.database = mongoClient.getDatabase(DATABASE_NAME);

        // Ping the database to verify the connection
        try {
            database.runCommand(new Document("ping", 1));
            logger.info("Successfully connected to MongoDB database: {}", DATABASE_NAME);
        } catch (Exception e) {
            logger.error("Failed to connect to MongoDB: {}", e.getMessage());
            throw new RuntimeException("MongoDB connection failed", e);
        }
    }

    public static synchronized void initialize(String connectionUri) {
        if (instance == null) {
            instance = new MongoManager(connectionUri);
        }
    }

    public static synchronized MongoManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MongoManager has not been initialized. Call initialize() first.");
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getPlayersCollection() {
        return database.getCollection(PLAYERS_COLLECTION);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("MongoDB connection closed.");
        }
    }
}

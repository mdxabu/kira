package org.mdxabu.Commands.Labs;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandLab {

    public static void say(SlashCommandInteractionEvent event, String content) {
        event.reply(content).queue();
    }

    public static void writeName(
        SlashCommandInteractionEvent event,
        String name
    ) {
        ArrayList<String> titles = new ArrayList<>();

        titles.add("Do you hear that?");
        titles.add("Your name is enough.");
        titles.add("Justice doesn’t blink.");
        titles.add("No courtroom. No appeal.");
        titles.add("Every letter is a verdict.");
        titles.add("History will thank me.");
        titles.add("One breath too late.");
        titles.add("This is judgment.");
        titles.add("Another name.");
        titles.add("The world grows quieter.");

        Random random = new Random();
        String randomTitle = titles.get(random.nextInt(titles.size()));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(randomTitle);
        embed.setImage(
            "https://media.tenor.com/0AEB38Hz1PMAAAAM/death-note-light.gif"
        );
        embed.setColor(Color.RED);

        event.replyEmbeds(embed.build()).queue();
    }

    public static void rps(SlashCommandInteractionEvent event, String choice) {
        String[] choices = { "rock", "paper", "scissors" };
        Random rand = new Random();
        String botChoice = choices[rand.nextInt(3)];
        String result;
        String gifUrl;
        if (choice.equals(botChoice)) {
            result = "It's a tie! Both chose " + choice + ".";
            gifUrl =
                "https://media.tenor.com/9Q2zXVtJZ6AAAAAM/rock-paper-scissors-tie.gif";
        } else if (
            (choice.equals("rock") && botChoice.equals("scissors")) ||
            (choice.equals("paper") && botChoice.equals("rock")) ||
            (choice.equals("scissors") && botChoice.equals("paper"))
        ) {
            result =
                "You win! I chose " + botChoice + ", you chose " + choice + ".";
            gifUrl =
                "https://media.tenor.com/0AEB38Hz1PMAAAAM/death-note-light.gif";
        } else {
            result =
                "I win! I chose " + botChoice + ", you chose " + choice + ".";
            gifUrl =
                "https://media.tenor.com/0AEB38Hz1PMAAAAM/death-note-ryuk.gif";
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(result);
        embed.setImage(gifUrl);
        embed.setColor(Color.BLUE); // Or any color
        event.replyEmbeds(embed.build()).queue();
    }

    public static void randomChuckNorrisJokes(SlashCommandInteractionEvent event) throws IOException, InterruptedException {
        String BaseURL = "https://api.chucknorris.io/jokes/random";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BaseURL))
                .header("Accept","application/json")
                .build();

        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());


        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Chuck Norrise Random Jokes :)");
        embed.setColor(Color.CYAN);
        embed.setDescription(jsonObj.get("value").getAsString());
        embed.setThumbnail("https://images02.military.com/sites/default/files/2021-04/chucknorris.jpeg");

        event.replyEmbeds(embed.build()).queue();


    }
}

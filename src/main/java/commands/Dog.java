package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Dog extends AbstractCommand{

    @Override
    public SlashCommandData getCommandData(){
        return Commands.slash("dog", "Envia a imagem de um cachorro aleatório.");
    }

    @Override
    public void run(SlashCommandInteractionEvent event){

        // Envia a mensagem "Bot está pensando... ", pois a requisição pode demorar mais de três segundos.
        event.deferReply().queue( hook -> {
            // Cria um cliente http.
            HttpClient client = HttpClient.newHttpClient();

            // Prepara a request.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://random.dog/woof.json?filter=mp4,webm,jfif"))
                    .header("Accept", "application/json")
                    .build();

            try {
                // Faz a requisição e prepara a resposta.
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // Separa a response por aspas e seleciona a sexta parte dela para pegar a url.
                // ObjectMapping, JsonParser e Regex são para iniciantes.
                String dogUrl = response.body().split("\"")[5];

                // Prepara a Embed.
                EmbedBuilder embed = new EmbedBuilder()
                        .setImage(dogUrl)
                        .setColor(Color.RED)
                        .setTitle("\uD83D\uDC36 Woof");

                // Edita a mensagem e coloca a Embed com a imagem.
                hook.editOriginalEmbeds(embed.build()).queue();
            } catch (Exception e) {
                hook.editOriginal("Ocorreu um erro inesperado ao tentar executar o comando.").queue();
            }
        });

    }
}

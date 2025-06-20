package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.Color;
import java.util.Objects;

public class Avatar extends AbstractCommand{

    @Override
    public SlashCommandData getCommandData(){
        return Commands.slash("avatar", "Mostra o avatar do usuário")
                .addOption(OptionType.USER, "usuário", "Usuário para mostrar o avatar");
    }

    @Override
    public void run(SlashCommandInteractionEvent event){
        // Cria uma variável para o usuário.
        User user;

        if(!event.getOptions().isEmpty()){
            // Se um usuário foi escolhi, utilize este.
            user = Objects.requireNonNull(event.getOption("usuário")).getAsUser();
        } else {
            // caso contrário, escolha o autor do comando.
            user = event.getUser();
        }

        // Pega a url do avatar.
        String avatarUrl = user.getEffectiveAvatarUrl()+"?size=2048";
        EmbedBuilder embed = new EmbedBuilder()
                .setImage(avatarUrl)
                .setColor(Color.RED)
                .setTitle("\uD83D\uDD17 Avatar de "+user.getName(), avatarUrl);

        // Envia a resposta.
        event.replyEmbeds(embed.build()).queue();
    }
}

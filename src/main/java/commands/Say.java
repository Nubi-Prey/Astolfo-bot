package commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class Say extends AbstractCommand{

    @Override
    public SlashCommandData getCommandData(){
        return Commands.slash("say", "Envia uma mensagem")
                .addOption(OptionType.STRING, "mensagem", "Mensagem a ser enviada", true)
                .addOption(OptionType.CHANNEL, "canal", "Enviar a mensagem em:")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
    }

    @Override
    public void run(SlashCommandInteractionEvent event){

        // Pega a mensagem a ser enviada.
        String message = Objects.requireNonNull(event.getOption("mensagem")).getAsString();
        Member member = Objects.requireNonNull(event.getMember());
        User user = event.getUser();

        // Prepara a Embed a ser enviada.
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl())
                .setDescription(message);

        // Define onde a mensagem será enviada.
        TextChannel channel;

        // O try-catch foi utilizado para evitar que o usuário escolha um canal de voz.
        try {
            // Se o usuário escolheu um canal, envie no canal escolhido.
             channel = Objects.requireNonNull(event.getOption("canal")).getAsChannel().asTextChannel();
        } catch (Exception e) {
            // Caso contrário, envie no canal onde o evento foi chamado.
            channel = event.getChannel().asTextChannel();
        }

        // O usuário precisa ter permissão de "GERENCIAR MENSAGENS" para utilizar o comando.
        if(!member.hasPermission(channel, Permission.MESSAGE_MANAGE)){
            event.reply("Você não tem permissão para enviar mensagem neste canal.").setEphemeral(true).queue();
            return;
        }

        // Envia a mensagem.
        channel.sendMessageEmbeds(embed.build()).queue((success) -> event.reply("Mensagem Enviada!").setEphemeral(true).queue());

    }
}

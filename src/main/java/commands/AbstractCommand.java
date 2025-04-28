package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Classe abstrata de Comandos, serve como modelo para novos comandos.
 */
public abstract class AbstractCommand {

    /**
     * Responsável por descrever as informações necessárias para o comando.
     * @return SlashCommandData
     */
    public abstract SlashCommandData getCommandData();

    /**
     * Função a ser executada pelo comando.
     */
    public abstract void run(SlashCommandInteractionEvent event);
}

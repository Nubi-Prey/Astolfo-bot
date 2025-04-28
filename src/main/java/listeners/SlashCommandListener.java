package listeners;

import commands.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ConcurrentMap;

/**
 * Listener de Slash Commands, responsável por receber as requisições e executar os comandos.
 */
public class SlashCommandListener extends ListenerAdapter {
    private final ConcurrentMap<String, AbstractCommand> commandRegistry;

    public SlashCommandListener(ConcurrentMap<String, AbstractCommand> commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String CommandName = event.getName();

        try {
            AbstractCommand command = commandRegistry.get(CommandName);

            command.run(event);

        } catch (Exception e) {
            // Captura outras exceções inesperadas
            System.err.println("Ocorreu um erro inesperado ao tentar executar " + CommandName + ": " + e.getMessage());
        }
    }
}

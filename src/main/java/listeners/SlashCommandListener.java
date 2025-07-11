package listeners;

import commands.AbstractCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Listener de Slash Commands, responsável por receber as requisições e executar os comandos.
 */
public class SlashCommandListener extends ListenerAdapter {

    static final Logger logger = LoggerFactory.getLogger(SlashCommandListener.class);
    private final ConcurrentMap<String, AbstractCommand> commandRegistry;
    private final Boolean TEST_MODE;
    private final String TEST_GUILD_ID;

    public SlashCommandListener(ConcurrentMap<String, AbstractCommand> commandRegistry, Boolean TEST_MODE, String TEST_GUILD_ID) {
        this.commandRegistry = commandRegistry;
        this.TEST_MODE = TEST_MODE;
        this.TEST_GUILD_ID = TEST_GUILD_ID;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String CommandName = event.getName();
        String GuildId = Objects.requireNonNull(event.getGuild()).getId();
        User user = event.getUser();

        logger.debug("{}({}) usou o comando {}", user.getName(), user.getId(), CommandName);

        if(TEST_MODE && !TEST_GUILD_ID.equals(GuildId)){
            event.reply("Bot habilitado apenas em servidor de testes.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        try {
            AbstractCommand command = commandRegistry.get(CommandName);

            command.run(event);

        } catch (Exception e) {
            // Captura outras exceções inesperadas
            event.reply("Ocorreu um erro inesperado ao tentar executar o comando.").setEphemeral(true).queue();
            logger.error("{}: {}", CommandName, e.getMessage());
        }
    }
}

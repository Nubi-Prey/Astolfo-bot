package handlers;

import commands.AbstractCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Gerenciador de comandos, responsável por atualizar o cache de comandos e slash commands do bot.
 */
public class CommandHandler {
    // Cria uma Lista para serem adicionados os comandos
    public ConcurrentMap<String, AbstractCommand> commandRegistry;
    private final boolean TEST_MODE;
    private final String TEST_GUILD_ID;
    public List<SlashCommandData> slashCommandDatas = new ArrayList<>();

    static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    public CommandHandler(ConcurrentMap<String, AbstractCommand> commandRegistry, boolean TEST_MODE, String TEST_GUILD_ID){
        this.commandRegistry = commandRegistry;
        this.TEST_MODE = TEST_MODE;
        this.TEST_GUILD_ID = TEST_GUILD_ID;
    }

    /**
     * Atualiza o registro de comandos no cache.
     */
    public void updateCommandRegistry(){

        // Limpa a Lista para reiniciá-la
        commandRegistry.clear();
        slashCommandDatas.clear();

        Reflections reflections = new Reflections("commands");
        Set<Class<? extends AbstractCommand>> commandClasses = reflections.getSubTypesOf(AbstractCommand.class);

        // Itera pela lista de classes para adicionar os comandos e seus dados.
        for (Class<? extends AbstractCommand> cmdClass : commandClasses) {

            if (cmdClass.isInterface() || java.lang.reflect.Modifier.isAbstract(cmdClass.getModifiers())) {
                continue;
            }

            try {
                AbstractCommand instance = cmdClass.getDeclaredConstructor().newInstance();
                String commandName = cmdClass.getSimpleName().toLowerCase();

                if(!instance.isEnabled()) continue;

                commandRegistry.put(commandName, instance);
                slashCommandDatas.add(instance.getCommandData());

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Atualiza os Slash Commands no discord.
     * <p>
     * Variáveis de ambiente: <br>
     * {@code TEST_MODE}: para definir se os comandos iram apenas para o servidor de teste.<p>
     * {@code TEST_GUILD_ID}: ID do servidor de teste
     */
    public void updateDiscordSlashCommands(JDA jda){

        if(slashCommandDatas.isEmpty()){
            logger.error("Não foi possível atualizar os Slash Commands, certifique-se de atualizar o registro de comandos antes de atualizar os Slash Commands.");
            return;
        }

        if(TEST_MODE){
            if(TEST_GUILD_ID == null){
                logger.error("TEST_MODE habilitado, porém, TEST_GUILD_ID não foi definido. Não foi possível registrar comandos de guilda.");
                return;
            }
            Guild testGuild = jda.getGuildById(TEST_GUILD_ID);

            if (testGuild == null) {
                logger.error("Servidor de teste com ID '{}' não encontrado. Não foi possível registrar comandos de guilda.", TEST_GUILD_ID);
                return;
            }

            testGuild.updateCommands()
            .addCommands(slashCommandDatas)
            .queue(
                    success -> logger.info("{} comandos registrados/atualizados para o servidor de teste.", success.size()),
                    error -> logger.error("Erro ao registrar comandos para o servidor de teste: {}", error.getMessage())
            );

            return;
        }

        jda.updateCommands()
        .addCommands(slashCommandDatas)
        .queue(
                success -> logger.info("{} comandos registrados/atualizados globalmente.", success.size()),
                error -> logger.error("Erro ao registrar comandos globais: {}", error.getMessage())

        );


    }
}

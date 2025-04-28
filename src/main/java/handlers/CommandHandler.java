package handlers;

import commands.AbstractCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.reflections.Reflections;

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
    public List<SlashCommandData> slashCommandDatas = new ArrayList<>();
    private static final String TEST_GUILD_ID = System.getenv().getOrDefault("TEST_GUILD_ID", "720086666890706996");
    private static final Boolean TEST_MODE = Boolean.valueOf(System.getenv().getOrDefault("TEST_MODE",  "true"));

    public CommandHandler(ConcurrentMap<String, AbstractCommand> commandRegistry){
        this.commandRegistry = commandRegistry;
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

                commandRegistry.put(commandName, instance);
                slashCommandDatas.add(instance.getCommandData());

            } catch (Exception e) {
                throw new RuntimeException(e);
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
            System.err.println("CommandHandler -> Não foi possível atualizar os Slash Commands, certifique-se de atualizar o registro de comandos antes de atualizar os Slash Commands.");
            return;
        }

        if(TEST_MODE){
            Guild testGuild = jda.getGuildById(TEST_GUILD_ID);

            if (testGuild == null) {
                System.err.println("CommandHandler -> Servidor de teste com ID '" + TEST_GUILD_ID + "' não encontrado. Não foi possível registrar comandos de guilda.");
                return;
            }

            testGuild.updateCommands()
            .addCommands(slashCommandDatas)
            .queue(
                    success -> System.out.println("CommandHandler -> Sucesso! " + success.size() + " comandos registrados/atualizados para o servidor de teste."),
                    error -> System.err.println("CommandHandler -> Erro ao registrar comandos para o servidor de teste: " + error)
            );

            return;
        }

        jda.updateCommands()
        .addCommands(slashCommandDatas)
        .queue(
                success -> System.out.println("CommandHandler -> Sucesso! " + success.size() + " comandos registrados/atualizados globalmente."),
                error -> System.err.println("CommandHandler -> Erro ao registrar comandos globais: " + error)

        );


    }
}

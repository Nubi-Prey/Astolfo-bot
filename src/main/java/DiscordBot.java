import commands.AbstractCommand;
import handlers.CommandHandler;
import listeners.ReadyListener;
import listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Classe principal para o bot, responsável por iniciar o bot e seus listeners.
 */
public class DiscordBot
{
    public static void main(String[] args)
            throws InterruptedException
    {
        // Procura o Token nas variáveis de ambiente
        String token = System.getenv("DISCORD_BOT_TOKEN");

        if (token == null || token.isEmpty()) {
            System.err.println("Erro: Variável de ambiente DISCORD_BOT_TOKEN não foi encontrado.");
            return;
        }

        // Define configurações de inicialização.
        Properties props = new Properties();
        try(InputStream input = DiscordBot.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null){
                System.err.println("Não foi possível encontrar o arquivo de configuração config.properties. Iniciando com configurações padrão.");
                return;
            }

            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String TEST_GUILD_ID = props.getProperty("test.guild.id");
        boolean TEST_MODE = Boolean.parseBoolean(props.getProperty("test.mode", "false"));
        boolean UPDATE_COMMANDS_ON_READY = Boolean.parseBoolean(props.getProperty("update.commands.on.ready", "true"));

        // Inicializa um cache para os comandos
        ConcurrentMap<String, AbstractCommand> commandRegistry = new ConcurrentHashMap<>();

        CommandHandler commandHandler = new CommandHandler(commandRegistry, TEST_MODE, TEST_GUILD_ID);

        // Instancia os Listeners com o cache de comandos
        ReadyListener readyListener = new ReadyListener(commandHandler, UPDATE_COMMANDS_ON_READY);
        SlashCommandListener slashCommandListener = new SlashCommandListener(commandRegistry, TEST_MODE, TEST_GUILD_ID);

        JDA jda = JDABuilder.createLight(token)
                .addEventListeners(
                        readyListener,
                        slashCommandListener
                )
                .build();

        jda.awaitReady();
    }

}
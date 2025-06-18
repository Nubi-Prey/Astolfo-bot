import commands.AbstractCommand;
import handlers.CommandHandler;
import listeners.ReadyListener;
import listeners.SlashCommandListener;
import listeners.GuildMemberJoinListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principal para o bot, responsável por iniciar o bot e seus listeners.
 */
public class DiscordBot
{
    static final Logger logger = LoggerFactory.getLogger(DiscordBot.class);

    public static void main(String[] args)
            throws InterruptedException {

        // Procura o Token nas variáveis de ambiente
        String token = System.getenv("DISCORD_BOT_TOKEN");

        if (token == null || token.isEmpty()) {
            logger.error("Erro: Variável de ambiente DISCORD_BOT_TOKEN não foi encontrado.");
            return;
        }

        // Define configurações de inicialização.
        Properties props = new Properties();
        try(InputStream input = DiscordBot.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null){
                logger.error("Não foi possível encontrar o arquivo de configuração config.properties. Iniciando com configurações padrão.");
            } else {
                props.load(input);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        String TEST_GUILD_ID = props.getProperty("test.guild.id");
        boolean TEST_MODE = Boolean.parseBoolean(props.getProperty("test.mode", "false"));
        boolean UPDATE_DATABASE_ON_READY = Boolean.parseBoolean(props.getProperty("update.database.on.ready", "true"));
        boolean UPDATE_COMMANDS_ON_READY = Boolean.parseBoolean(props.getProperty("update.commands.on.ready", "true"));

        // Inicializa um cache para os comandos
        ConcurrentMap<String, AbstractCommand> commandRegistry = new ConcurrentHashMap<>();

        CommandHandler commandHandler = new CommandHandler(commandRegistry, TEST_MODE, TEST_GUILD_ID);

        // Instancia os Listeners com o cache de comandos
        ReadyListener readyListener = new ReadyListener(commandHandler, UPDATE_COMMANDS_ON_READY, UPDATE_DATABASE_ON_READY);
        SlashCommandListener slashCommandListener = new SlashCommandListener(commandRegistry, TEST_MODE, TEST_GUILD_ID);


        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(
                        readyListener,
                        slashCommandListener,
                        new GuildMemberJoinListener()
                )
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        jda.awaitReady();
    }

}
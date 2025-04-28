import commands.AbstractCommand;
import handlers.CommandHandler;
import listeners.ReadyListener;
import listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

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

        // Inicializa um cache para os comandos
        ConcurrentMap<String, AbstractCommand> commandRegistry = new ConcurrentHashMap<>();

        CommandHandler commandHandler = new CommandHandler(commandRegistry);

        // Instancia os Listeners com o cache de comandos
        ReadyListener readyListener = new ReadyListener(commandHandler);
        SlashCommandListener slashCommandListener = new SlashCommandListener(commandRegistry);

        JDA jda = JDABuilder.createLight(token)
                .addEventListeners(
                        readyListener,
                        slashCommandListener
                )
                .build();

        jda.awaitReady();
    }

}
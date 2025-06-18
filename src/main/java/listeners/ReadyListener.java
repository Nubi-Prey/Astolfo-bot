package listeners;

import net.dv8tion.jda.api.JDA;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import handlers.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Listener de onReady, executado quando o bot iniciar com sucesso.
 * <p>Responsável por iniciar o registro de comandos.
 * */
public class ReadyListener extends ListenerAdapter {

    static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);
    private final CommandHandler commandHandler;
    private final Boolean UPDATE_COMMANDS_ON_READY;
    private final Boolean UPDATE_DATABASE_ON_READY;
    private final String url = System.getenv("DATABASE_URL");

    public ReadyListener(CommandHandler commandHandler, Boolean UPDATE_COMMANDS_ON_READY, Boolean UPDATE_DATABASE_ON_READY) {
        this.commandHandler = commandHandler;
        this.UPDATE_COMMANDS_ON_READY = UPDATE_COMMANDS_ON_READY;
        this.UPDATE_DATABASE_ON_READY = UPDATE_DATABASE_ON_READY;
    }

    @Override
    public void onReady(ReadyEvent event) {

        JDA jda = event.getJDA();
        SelfUser selfUser = jda.getSelfUser();
        String bot_name = selfUser.getName();

        commandHandler.updateCommandRegistry();

        if(UPDATE_COMMANDS_ON_READY){
            commandHandler.updateDiscordSlashCommands(jda);
        }

        if(UPDATE_DATABASE_ON_READY){
            try(Connection db = DriverManager.getConnection(url)){
                db.setAutoCommit(false);

                List<Guild> guilds = jda.getGuilds();
                PreparedStatement insert_guild = db.prepareStatement("INSERT INTO server_config (id, name) VALUES (?, ?) " +
                        "ON CONFLICT (id) DO UPDATE " +
                        "SET name=EXCLUDED.name;");

                for(Guild guild : guilds){
                    String guild_id = guild.getId();
                    String guild_name = guild.getName();

                    insert_guild.setString(1, guild_id);
                    insert_guild.setString(2, guild_name);

                    insert_guild.addBatch();
                }

                insert_guild.executeBatch();

                db.commit();
            }catch (SQLException exception){
                logger.error(exception.getMessage());
            }
        }

        logger.info("{} está online e pronto!", bot_name);
    }
}

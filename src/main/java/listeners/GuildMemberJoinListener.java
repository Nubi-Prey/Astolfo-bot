package listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class GuildMemberJoinListener extends ListenerAdapter {

    private final String url = System.getenv("DATABASE_URL");
    static final Logger logger = LoggerFactory.getLogger(GuildMemberJoinListener.class);

    @Override
    public void onGuildMemberJoin( GuildMemberJoinEvent event){
        User user = event.getUser();
        Guild guild = event.getGuild();

        String channel_id = "";

        try(Connection db = DriverManager.getConnection(url)){
            PreparedStatement get_welcome_channel = db.prepareStatement("SELECT welcome_channel FROM server_config WHERE id=?;");

            get_welcome_channel.setString(1, guild.getId());

            ResultSet result = get_welcome_channel.executeQuery();
            result.next();

            channel_id = result.getString("welcome_channel");

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        if(channel_id == null) return;

        TextChannel channel = guild.getTextChannelById(channel_id);

        if(channel == null){
            logger.error("TextChannel de boas-vindas não encontrado!");
            return;
        }

        channel.sendMessage(
                "### " + user.getAsMention() + ", seja Bem-Vindo(a)!"
        ).queue();

    }
}

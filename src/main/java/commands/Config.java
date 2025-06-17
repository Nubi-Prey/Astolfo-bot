package commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Comando para configurar servidores com o database do bot.
 */
public class Config extends AbstractCommand {

    private final String url = System.getenv("DATABASE_URL");

    @Override
    public SlashCommandData getCommandData(){
        return Commands.slash("config", "Configura este servidor para o bot.")
                .addOption(OptionType.CHANNEL, "welcome_channel", "Canal de boas-vindas")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {

        // Cria listas para as opções selecionadas/possíveis
        List<OptionMapping> eventOptions = event.getOptions();
        List<String> possibleOptions = new ArrayList<>();

        for(OptionData option : this.getCommandData().getOptions()){
            possibleOptions.add(option.getName());
        }

        Guild guild = Objects.requireNonNull(event.getGuild());

        StringBuilder updates = new StringBuilder();

        // Se conecta com o database
        try (Connection db = DriverManager.getConnection(url)) {
            db.setAutoCommit(false);

            if(eventOptions.isEmpty()){
                PreparedStatement select_guild_data = db.prepareStatement("SELECT * FROM server_config WHERE id=?");
                select_guild_data.setString(1, guild.getId());

                ResultSet result = select_guild_data.executeQuery();

                result.next();

                for(String item:possibleOptions){
                    try{
                        if(result.getString(item) == null) continue;

                        // Adiciona os dados na mensagem
                        updates.append("> `").append(item).append("`: `").append(result.getString(item)).append("`\n");
                    } catch (SQLException e) {
                        continue;
                    }

                }

                if(updates.isEmpty()){
                    event.reply("Nenhuma função está configurada neste servidor.").setEphemeral(true).queue();
                } else {
                    event.reply("Dados configurados:\n" + updates).setEphemeral(true).queue();
                }

                db.commit();
                return;
            }

            // Se houver opções, altere elas no database
            for (OptionMapping option : eventOptions) {

                String option_name = option.getName();
                String option_value = option.getAsString();

                if(!possibleOptions.contains(option_name)){
                    continue;
                }

                PreparedStatement insert_guild_data = db.prepareStatement(
                        "INSERT INTO server_config (id, " + option_name + ") VALUES (?,?) " +
                             "ON CONFLICT (id) DO UPDATE SET " + option_name + " = EXCLUDED." + option_name + ";");

                insert_guild_data.setString(1, guild.getId());
                insert_guild_data.setString(2, option_value);

                insert_guild_data.executeUpdate();

                updates.append("> `").append(option_name).append("` definido como ").append(option.getAsMentionable().getAsMention()).append("\n");
            }

            db.commit();
            event.reply("Configurações atualizadas:\n"+updates).setEphemeral(true).queue();
        } catch (SQLException exception){
            throw new RuntimeException(exception);
        }

    }
}

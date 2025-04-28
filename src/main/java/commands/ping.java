package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Mostra a latência do bot
 */
public class ping extends AbstractCommand{

    @Override
    public SlashCommandData getCommandData(){
        return Commands.slash("ping", "Mostra a latência do bot");
    }

    @Override
    public void run(SlashCommandInteractionEvent event){
        long time = System.currentTimeMillis();
        event.reply("Pong!")
            .flatMap(v -> event.getHook().editOriginalFormat("Pong! Latência: %d ms", System.currentTimeMillis() - time))
            .queue();
    }
}

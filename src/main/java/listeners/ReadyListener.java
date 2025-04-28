package listeners;

import net.dv8tion.jda.api.JDA;

import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import handlers.CommandHandler;

/**
 * Listener de onReady, executado quando o bot iniciar com sucesso.
 * <p>Responsável por iniciar o registro de comandos.
 * */
public class ReadyListener extends ListenerAdapter {

    private final CommandHandler commandHandler;
    private final Boolean UPDATE_COMMANDS_ON_READY;

    public ReadyListener(CommandHandler commandHandler, boolean UPDATE_COMMANDS_ON_READY) {
        this.commandHandler = commandHandler;
        this.UPDATE_COMMANDS_ON_READY = UPDATE_COMMANDS_ON_READY;
    }

    @Override
    public void onReady(ReadyEvent event) {

        JDA jda = event.getJDA();
        SelfUser selfUser = jda.getSelfUser();
        String name = selfUser.getName();

        System.out.println(name + " está online e pronto!");

        commandHandler.updateCommandRegistry();

        if(UPDATE_COMMANDS_ON_READY){
            commandHandler.updateDiscordSlashCommands(jda);
        }
    }
}

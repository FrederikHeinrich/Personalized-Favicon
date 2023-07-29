package io.freddi.personalizedfavicon.waterfall;

import io.freddi.personalizedfavicon.utils.PersonalizedFaviconCommand;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class WaterfallCommand extends Command {

    public WaterfallCommand() {
        super("pf", "personalizedfavicon.command");
    }



    @Override
    public void execute(CommandSender sender, String[] args) {
        Audience executor = WaterfallPlugin.getAdventure().sender(sender);
        if(args.length == 1){
            if (args[0].equalsIgnoreCase("reload")) {
                if(sender.hasPermission("personalizedfavicon.command.reload"))
                    new PersonalizedFaviconCommand(executor).reload();
                else
                    new PersonalizedFaviconCommand(executor).noPermissions();
                return;
            }
            if(args[0].equalsIgnoreCase("clear")){
                if(sender.hasPermission("personalizedfavicon.command.clear"))
                    new PersonalizedFaviconCommand(executor).clear();
                else
                    new PersonalizedFaviconCommand(executor).noPermissions();
                return;
            }
            if(args[0].equalsIgnoreCase("edit")){
                if(sender.hasPermission("personalizedfavicon.command.edit"))
                    new PersonalizedFaviconCommand(executor).edit();
                else
                    new PersonalizedFaviconCommand(executor).noPermissions();
                return;
            }
        }
        else if(args.length == 2){
            if(args[0].equalsIgnoreCase("load")){
                if(sender.hasPermission("personalizedfavicon.command.edit"))
                    new PersonalizedFaviconCommand(executor).load(args[1]);
                else
                    new PersonalizedFaviconCommand(executor).noPermissions();
                return;
            }
        }
        new PersonalizedFaviconCommand(executor).help();
    }
}

package io.freddi.personalizedfavicon.utils;

import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

public class PersonalizedFaviconCommand {

    private static MiniMessage mm = MiniMessage.miniMessage();

    private Audience audience;

    public PersonalizedFaviconCommand(Audience audience){
        this.audience = audience;
    }

    public void noPermissions(){
        audience.sendMessage(Component.text("You don't have permissions to do that!", NamedTextColor.RED));
    }
    public void help(){
        audience.sendMessage(mm.deserialize("<green>PersonalizedFavicon"));
        audience.sendMessage(mm.deserialize("<green>/pf reload - Reloads the config"));
        audience.sendMessage(mm.deserialize("<green>/pf clear - Clears the Database"));
        audience.sendMessage(mm.deserialize("<green>/pf edit - Opens the editor"));
        audience.sendMessage(mm.deserialize("<green>/pf load <key> - Loads the config"));
    }

    public void reload(){
        audience.sendMessage(mm.deserialize("<green>Reloading config..."));
        audience.sendMessage(Config.instance().reload());
        audience.sendMessage(mm.deserialize("<green>Config reloaded!"));
    }

    public void edit(){
        audience.sendMessage(mm.deserialize("<green>Opening editor..."));
        audience.sendMessage(Config.instance().editor());
    }

    public void load(String key){
        if(key == null){
            audience.sendMessage(Component.text("You must provide a key!", NamedTextColor.RED));
        }
        audience.sendMessage(mm.deserialize("<green>Loading config..."));
        audience.sendMessage(Config.instance().download(key));
        audience.sendMessage(mm.deserialize("<green>Config loaded!"));
    }

    public void clear(){
        audience.sendMessage(mm.deserialize("<green>Clearing config..."));
        audience.sendMessage(PersonalizedFavicon.clear());
        audience.sendMessage(mm.deserialize("<green>Config cleared!"));
    }


}

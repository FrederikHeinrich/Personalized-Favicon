package io.freddi.personalizedfavicon.waterfall;

import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import io.freddi.personalizedfavicon.utils.Config;
import io.freddi.personalizedfavicon.utils.Messages;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class WaterfallPlugin extends Plugin implements Listener {

    @Getter
    private static BungeeAudiences adventure;
    @Override
    public void onEnable() {

        adventure = BungeeAudiences.create(this);
        getLogger().info("Starting PersonalizedFavicon for Waterfall");
        Metrics metrics = new Metrics(this, 5495);
        metrics.addCustomChart(new Metrics.SingleLineChart("stored_favicons", PersonalizedFavicon::count));
        //
        Config.instance().reload();
        Messages.instance().reload();

        getProxy().getPluginManager().registerCommand(this, new WaterfallCommand());
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        String ip = event.getConnection().getVirtualHost().getHostString();
        PersonalizedFavicon favicon = PersonalizedFavicon.find(ip);
        if (favicon == null) return;
        if (favicon.toImage() == null) return;
        ServerPing response = event.getResponse();
        response.setFavicon(Favicon.create(favicon.toImage()));
        event.setResponse(response);
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        String uuid = event.getConnection().getUniqueId().toString();
        String ip = event.getConnection().getVirtualHost().getHostString();
        String username = event.getConnection().getName();
        PersonalizedFavicon favicon = PersonalizedFavicon.find(uuid, username, ip);
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
    public @NonNull BungeeAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        }
        return this.adventure;
    }

}

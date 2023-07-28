package io.freddi.personalizedfavicon.waterfall;

import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import io.freddi.personalizedfavicon.utils.Config;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class WaterfallPlugin extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Starting PersonalizedFavicon for Waterfall");
        Metrics metrics = new Metrics(this, 5495);
        metrics.addCustomChart(new Metrics.SingleLineChart("stored_favicons", PersonalizedFavicon::count));
        //
        assert Config.instance() != null;
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
    public void onLogin(ServerConnectedEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String ip = event.getPlayer().getPendingConnection().getVirtualHost().getHostString();
        String username = event.getPlayer().getName();
        PersonalizedFavicon favicon = PersonalizedFavicon.find(uuid, username, ip);
    }
}

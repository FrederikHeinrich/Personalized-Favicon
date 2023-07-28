package io.freddi.personalizedfavicon.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import io.freddi.personalizedfavicon.utils.Config;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Base64;

@Plugin(id = "personalizedfavicon", name = "PersonalizedFavicon", version = "@version@", description = "PersonalizedFavicon plugin for Velocity", authors = {"Freddi"})
public class VelocityPlugin {

    private final Metrics.Factory metricsFactory;
    @Inject
    public VelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirector, Metrics.Factory metricsFactory) {
        logger.info("Starting PersonalizedFavicon for Velocity"); this.metricsFactory = metricsFactory;
        assert Config.instance() != null;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        System.out.println("Starting PersonalizedFavicon for Velocity");
        Metrics metrics = metricsFactory.make(this, 19279);
        metrics.addCustomChart(new Metrics.SingleLineChart("stored_favicons", PersonalizedFavicon::count));
    }

    @Subscribe
    public void onServerList(ProxyPingEvent event) {
        String ip = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
        PersonalizedFavicon favicon = PersonalizedFavicon.find(ip);
        System.out.println(favicon);
        if (favicon == null) return;
        if (favicon.toImage() == null) return;
        event.setPing(event.getPing().asBuilder().favicon(Favicon.create(favicon.toImage())).build());
    }

    @Subscribe
    public void onPlayerJoin(ServerPreConnectEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String ip = event.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        String username = event.getPlayer().getUsername();
        PersonalizedFavicon favicon = PersonalizedFavicon.find(uuid, username, ip);

    }

}

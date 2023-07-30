package io.freddi.personalizedfavicon.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.Favicon;
import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import io.freddi.personalizedfavicon.utils.Config;
import io.freddi.personalizedfavicon.utils.Messages;
import io.freddi.personalizedfavicon.utils.UpdateChecker;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(id = "personalizedfavicon", name = "PersonalizedFavicon", version = "@version@", description = "PersonalizedFavicon plugin for Velocity", authors = {"Freddi"}, url = "https://discord.gg/freddi")
public class VelocityPlugin {

    private final Metrics.Factory metricsFactory;
    private final ProxyServer proxyServer;

    @Inject
    public VelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory
    Path dataDirector, Metrics.Factory metricsFactory) {
        this.proxyServer = proxyServer;
        logger.info("Starting PersonalizedFavicon for Velocity");
        this.metricsFactory = metricsFactory;
        Config.instance().reload();
        Messages.instance().reload();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        System.out.println("Starting PersonalizedFavicon for Velocity");
        Metrics metrics = metricsFactory.make(this, 19279);
        metrics.addCustomChart(new Metrics.SingleLineChart("stored_favicons", PersonalizedFavicon::count));
        proxyServer.getCommandManager().register(VelocityCommand.command(proxyServer));
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


    @Subscribe
    public void onConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        if (Config.instance().updateChecker().notification().user())
            if (player.hasPermission("personalizedfavicon.updates")) {
                if (UpdateChecker.instance.available()) {
                    player.sendMessage(UpdateChecker.instance.updateAvailable());
                }
            }
    }

}

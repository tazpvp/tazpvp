package net.tazpvp.tazpvp.listeners;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.tazpvp.tazpvp.utils.data.PunishmentService;
import net.tazpvp.tazpvp.utils.data.PunishmentServiceImpl;
import net.tazpvp.tazpvp.utils.enums.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandSend implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        final PunishmentService punishmentService = new PunishmentServiceImpl();
        TextComponent component = new TextComponent(CC.GRAY + "Join the discord to appeal [Click here]");
        component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/56rdkbSqa8"));


        if (punishmentService.getPunishment(p.getUniqueId()) == PunishmentService.PunishmentType.MUTED) {
            if (e.getMessage().contains("me")) {
                e.setCancelled(true);
            }
        }

        if (punishmentService.getPunishment(p.getUniqueId()) == PunishmentService.PunishmentType.BANNED) {
            if (e.getMessage().contains("me")) {
                e.setCancelled(true);
            }
            if (punishmentService.getTimeRemaining(p.getUniqueId()) > 0) {
                p.sendMessage(CC.RED + "You cannot use commands while banned.");
                p.spigot().sendMessage(component);
                e.setCancelled(true);
            }
        }
    }
}

package net.tazpvp.tazpvp.player.talents.talent;

import net.tazpvp.tazpvp.data.entity.TalentEntity;
import net.tazpvp.tazpvp.game.guilds.Guild;
import net.tazpvp.tazpvp.game.guilds.GuildUtils;
import net.tazpvp.tazpvp.enums.CC;
import net.tazpvp.tazpvp.utils.functions.PlayerFunctions;
import net.tazpvp.tazpvp.utils.observer.Observable;
import net.tazpvp.tazpvp.utils.player.PlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Medic extends Observable {
    @Override
    public void death(Player victim, Player killer) {
        final PlayerWrapper killerWrapper = PlayerWrapper.getPlayer(killer);
        final TalentEntity killerTalentEntity = killerWrapper.getTalentEntity();

        if (killerTalentEntity.isMedic()) {
            if (GuildUtils.isInGuild(killer)) {
                Guild killerGuild = GuildUtils.getGuildPlayerIn(killer);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (GuildUtils.isInGuild(p)) {
                        Guild pGuild = GuildUtils.getGuildPlayerIn(p);
                        if (pGuild == killerGuild) {
                            if (p.getLocation().distance(victim.getLocation()) <= 5) {
                                if (p != killer) {
                                    PlayerFunctions.addHealth(p, 2);
                                    p.sendMessage(CC.DARK_GREEN + "Your guild mate healed you.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

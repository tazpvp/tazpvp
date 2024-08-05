/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, n-tdi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tazpvp.tazpvp.listeners;

import net.tazpvp.tazpvp.Tazpvp;
import net.tazpvp.tazpvp.data.LooseData;
import net.tazpvp.tazpvp.data.entity.PlayerStatEntity;
import net.tazpvp.tazpvp.data.implementations.UserRankServiceImpl;
import net.tazpvp.tazpvp.data.services.PlayerStatService;
import net.tazpvp.tazpvp.data.services.UserRankService;
import net.tazpvp.tazpvp.enums.StatEnum;
import net.tazpvp.tazpvp.helpers.PlaytimeHelper;
import net.tazpvp.tazpvp.enums.CC;
import net.tazpvp.tazpvp.enums.Theme;
import net.tazpvp.tazpvp.helpers.*;
import net.tazpvp.tazpvp.objects.CombatObject;
import net.tazpvp.tazpvp.wrappers.PlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.ntdi.nrcore.NRCore;

import java.util.UUID;

public class JoinListener implements Listener {


    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        PlayerWrapper.addPlayer(p);
        ScoreboardHelper.initScoreboard(p);
        PlaytimeHelper.playerJoined(p);
        PlayerHelper.resetHealth(p);
        PlayerHelper.feedPlr(p);
        Tazpvp.getInstance().getPlayerNameTagService().initializePlayer(p);
        BanHelper.checkBan(p);

        if (!p.getWorld().getName().equalsIgnoreCase("arena")) {
            PlayerHelper.teleport(p, NRCore.config.spawn);
        }

        CombatObject.tags.put(id, new CombatObject(id));

        int playerLevel = StatEnum.LEVEL.getInt(id);
        int playerXp = StatEnum.XP.getInt(id);

        p.setLevel(playerLevel);
        if (playerXp >= LooseData.getExpLeft(id)) {
            PlayerHelper.levelUp(id);
        } else if (StatEnum.XP.getInt(id) < 0) {
            StatEnum.XP.set(id, 0);
        } else {
            p.setExp((float) playerXp / LooseData.getExpLeft(p.getUniqueId()));
        }

        for (Player vp : Bukkit.getOnlinePlayers()) {
            PlayerWrapper vpw = PlayerWrapper.getPlayer(vp);
            if (vpw.isVanished()) {
                for (Player op : Bukkit.getOnlinePlayers()) {
                    if (!op.hasPermission("tazpvp.vanish")) {
                        op.hidePlayer(Tazpvp.getInstance(), vp);
                    } else {
                        op.showPlayer(Tazpvp.getInstance(), vp);
                    }
                }
            }
        }

        PlayerWrapper playerWrapper = PlayerWrapper.getPlayer(p);

        final UserRankService userRankService = new UserRankServiceImpl();
        userRankService.removeAllExpiredRanks(playerWrapper.getUserRankEntity());

        playerWrapper.refreshRankEntity();

        p.setPlayerListHeaderFooter(
                CC.DARK_AQUA + "                                      " + "\n                 " +
                        Theme.SERVER.gradient("TAZPVP.NET", true) + "               " + "\n",

                "\n" +
                        Theme.DISCORD.gradient("✉ ᴊᴏɪɴ ᴜꜱ /ᴅɪꜱᴄᴏʀᴅ", false) + "\n" +
                        Theme.STORE.gradient("✘ ꜱᴜʙꜱᴄʀɪʙᴇ /ꜱᴛᴏʀᴇ", false) + "\n");


        final String name = p.getName();
        final String plus;

        if (!p.hasPlayedBefore()) {
            PlayerHelper.kitPlayer(p);
            plus = CC.YELLOW + "[" + CC.GOLD + "+" + CC.YELLOW + "]";
        } else {
            plus = CC.GREEN + "[" + CC.GOLD + "+" + CC.GREEN + "]";
        }

        final String message = plus + " " + name;
        e.setJoinMessage(message);

        AfkHelper.setAfk(p);
        Tazpvp.getBotThread().connectionChat(p.getName(), true);
    }
}

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

package net.tazpvp.tazpvp.game.duels;

import lombok.Getter;
import lombok.Setter;
import net.tazpvp.tazpvp.Tazpvp;
import net.tazpvp.tazpvp.enums.CC;
import net.tazpvp.tazpvp.enums.StatEnum;
import net.tazpvp.tazpvp.helpers.ChatHelper;
import net.tazpvp.tazpvp.helpers.PlayerHelper;
import net.tazpvp.tazpvp.wrappers.PlayerWrapper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.ntdi.nrcore.NRCore;
import world.ntdi.nrcore.utils.ArmorManager;
import world.ntdi.nrcore.utils.world.WorldUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Duel {

    public static final String prefix = CC.AQUA + "Duel | " + CC.DARK_AQUA;

    @Getter
    private final UUID P1;
    @Getter
    private final UUID P2;
    @Getter
    private final String NAME;
    @Getter
    private final List<UUID> DUELERS;
    @Getter
    private final List<UUID> SPECTATORS;
    @Getter @Setter
    private UUID winner;
    @Getter @Setter
    private UUID loser;
    @Getter @Setter
    private String worldName;
    @Getter @Setter
    private boolean starting;

    public Duel(@Nonnull final UUID P1, @Nonnull final UUID P2, @Nonnull final String NAME) {
        this.P1 = P1;
        this.P2 = P2;
        this.NAME = NAME;

        this.DUELERS = new ArrayList<>();
        this.DUELERS.add(P1);
        this.DUELERS.add(P2);

        this.SPECTATORS = new ArrayList<>();
    }

    public void initialize() {
        getDUELERS().forEach(uuid -> {
            PlayerWrapper pw = PlayerWrapper.getPlayer(uuid);
            if (pw.getDuel() != null) return;
            pw.setDuel(this);
        });
        new WorldUtil().cloneWorld("duelMap1", worldName);

        new BukkitRunnable() {
            public void run() {
                begin();
            }
        }.runTaskLater(Tazpvp.getInstance(), 20*2L);
    }

    public void begin() {
        World world = Bukkit.getWorld(worldName);

        List<UUID> duelers = DUELERS;

        Player p1 = Bukkit.getPlayer(duelers.get(0));
        Player p2 = Bukkit.getPlayer(duelers.get(1));

        if (p1 == null || p2 == null) return;

        initPlayer(p1);
        initPlayer(p2);

        PlayerHelper.teleport(p1, new Location(world, 0.5, 10, 14.5, 180, 0));
        PlayerHelper.teleport(p2, new Location(world, 0.5, 10, -13.5, 0, 0));

        duelers.forEach(this::addItems);

        setStarting(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                duelers.forEach(id -> {
                    Player p = Bukkit.getPlayer(id);
                    if (p != null) {
                        p.sendTitle(CC.GOLD + "" + CC.BOLD + "BEGIN", "", 5, 10, 5);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1,1);
                    }
                });
                setStarting(false);
            }
        }.runTaskLater(Tazpvp.getInstance(), 20*5);
    }

    public void end(final UUID loserID) {
        setLoser(loserID);
        setWinner(getOtherDueler(loserID));

        final Player pWinner = Bukkit.getPlayer(winner);
        final Player pLoser = Bukkit.getPlayer(loser);

        ChatHelper.announce(
                CC.AQUA + Bukkit.getOfflinePlayer(getWinner()).getName() +
                        CC.DARK_AQUA + " won a duel against " +
                        CC.AQUA + Bukkit.getOfflinePlayer(loserID).getName(),
                Sound.BLOCK_BELL_RESONATE
        );

        if (pWinner != null) {
            StatEnum.DUEL_MMR.add(winner, 15);
            pWinner.sendTitle(CC.GOLD + "" + CC.BOLD + "YOU WIN", "", 20, 20, 20);
        }

        if (pLoser != null) {
            StatEnum.DUEL_MMR.remove(loser, 9);
            pLoser.setGameMode(GameMode.SPECTATOR);
        }

        final Duel duel = this;

        new BukkitRunnable() {
            public void run() {

                DUELERS.forEach(id -> {
                    Player p = Bukkit.getPlayer(id);
                    if (p == null) return;
                    ArmorManager.setPlayerContents(p, true);
                    p.setGameMode(GameMode.SURVIVAL);
                    PlayerHelper.teleport(p, NRCore.config.spawn);
                    PlayerWrapper.getPlayer(p).setDuel(null);
                    PlayerHelper.resetHealth(p);
                });

                new WorldUtil().deleteWorld(getWorldName());
                duelsList.remove(duel);
            }
        }.runTaskLater(Tazpvp.getInstance(), 20*5);

        Tazpvp.getObservers().forEach(observer -> observer.duel_end(pWinner, pLoser));
    }

    public void abort() {
        DUELERS.forEach(p -> {
            Player plr = Bukkit.getPlayer(p);
            if (plr != null) {
                PlayerHelper.teleport(plr, NRCore.config.spawn);
                ArmorManager.setPlayerContents(plr, true);
            }
        });

        new WorldUtil().deleteWorld(getWorldName());
        duelsList.remove(this);
    }

    public abstract void addItems(UUID id);

    public UUID getOtherDueler(final UUID id) {
        return (P1.equals(id) ? P2 : P1);
    }

    public final static ConcurrentHashMap<UUID, Duel> duels = new ConcurrentHashMap<>();
    public final static List<Duel> duelsList = new ArrayList<>();

    private void initPlayer(final Player p) {
        ArmorManager.storeAndClearInventory(p);
        PlayerHelper.resetHealth(p);
        PlayerHelper.feedPlr(p);
        p.sendMessage(CC.BOLD + "" + CC.GOLD + "The duel will begin in 5 seconds.");
        p.setGameMode(GameMode.SURVIVAL);
        Tazpvp.getObservers().forEach(observer -> observer.duel(p));
    }
}
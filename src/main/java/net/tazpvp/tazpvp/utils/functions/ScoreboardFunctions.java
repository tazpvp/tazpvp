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

package net.tazpvp.tazpvp.utils.functions;

import net.tazpvp.tazpvp.utils.data.DataTypes;
import net.tazpvp.tazpvp.utils.data.LooseData;
import net.tazpvp.tazpvp.utils.data.PersistentData;
import net.tazpvp.tazpvp.utils.enums.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardFunctions {

    private static Scoreboard board = null;
    private static Objective objective = null;

    /**
     * Initialize all the scoreboard values for a player.
     * @param p The player in question.
     */

    @SuppressWarnings("all")
    public static void initScoreboard(Player p) {

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = board.registerNewObjective("sb", "dummy", CC.translateAlternateColorCodes('&', "&3&lTAZPVP.NET"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("                         ").setScore(8);
        newLine(p, DataTypes.LEVEL.getColumnName(), "〡 Level:", CC.AQUA,
                PersistentData.getInt(p, DataTypes.LEVEL) + "").setScore(7);
        newLine(p, DataTypes.COINS.getColumnName(), "〡 Coins:", CC.GOLD,
                PersistentData.getInt(p, DataTypes.COINS) + "").setScore(6);
        newLine(p, DataTypes.XP.getColumnName(), "〡 EXP:", CC.BLACK,
                PersistentData.getInt(p, DataTypes.XP) + " / " + LooseData.getExpLeft(p.getUniqueId())).setScore(5);
        objective.getScore(" ").setScore(4);
        newLine(p, DataTypes.KILLS.getColumnName(), "〡 Kills:", CC.YELLOW,
                PersistentData.getInt(p, DataTypes.KILLS) + "").setScore(3);
        newLine(p, DataTypes.DEATHS.getColumnName(), "〡 Deaths:", CC.DARK_PURPLE,
                PersistentData.getInt(p, DataTypes.DEATHS) + "").setScore(2);
        newLine(p, "kdr", "〡 KDR:", CC.GRAY, PersistentData.kdrFormula(
                PersistentData.getFloat(p, DataTypes.KILLS), PersistentData.getFloat(p, DataTypes.DEATHS)) + "").setScore(1);
        objective.getScore("   ").setScore(0);

        p.setScoreboard(board);
    }

    private static Score newLine(Player p, String name, String prefix, CC chatColor, String suffix) {
        String ID = chatColor.toString();
        Team team = board.registerNewTeam(name);

        team.addEntry(ID);
        team.setPrefix(CC.AQUA + prefix + CC.GRAY + " ");
        team.setSuffix(suffix);

        return objective.getScore(ID);
    }
}

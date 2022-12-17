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

package net.tazpvp.tazpvp.gui.guis;

import net.tazpvp.tazpvp.utils.data.PersistentData;
import net.tazpvp.tazpvp.utils.enums.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.ntdi.nrcore.utils.gui.Button;
import world.ntdi.nrcore.utils.gui.GUI;
import world.ntdi.nrcore.utils.item.builders.ItemBuilder;

public class Achievements extends GUI {

    public Achievements(Player p) {
        super("Achievements", 4);

        net.tazpvp.tazpvp.achievements.Achievements ACH = PersistentData.getAchievements(p.getUniqueId());

        fill(1, 4*9, ItemBuilder.of(Material.BLACK_STAINED_GLASS, 1).name(" ").build());

        setButton(p,  10, "Adept", "kys", ACH.isAdept());
        setButton(p,  11, "Bowling", "kys", ACH.isBowling());
        setButton(p,  12, "Charm", "kys", ACH.isCharm());
        setButton(p,  13, "Craftsman", "kys", ACH.isCraftsman());
        setButton(p,  14, "Gamble", "kys", ACH.isGamble());
        setButton(p,  15, "Gladiator", "kys", ACH.isGladiator());
        setButton(p,  16, "Legend", "kys", ACH.isLegend());

        setButton(p,  19, "Merchant", "kys", ACH.isMerchant());
        setButton(p,  20, "Superior", "kys", ACH.isSuperior());

        open(p);
    }

    private void setButton(Player p, int slot, String name, String lore, boolean completed) {

        String complete = completed ? CC.GREEN + "Complete" : CC.RED + "Incomplete";
        Material mat = completed ? Material.ENCHANTED_BOOK : Material.WRITTEN_BOOK;

        addButton(Button.create(ItemBuilder.of(mat, 1).name(CC.AQUA + name).lore(CC.GRAY + lore, " ", complete).build(), (e) -> {
            p.getInventory().addItem(new ItemStack(mat, 1));
        }), slot);
    }
}

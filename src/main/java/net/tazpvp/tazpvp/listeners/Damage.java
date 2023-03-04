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
import net.tazpvp.tazpvp.utils.functions.CombatTagFunctions;
import net.tazpvp.tazpvp.utils.functions.DeathFunctions;
import net.tazpvp.tazpvp.utils.objects.CombatTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player victim) {
            Bukkit.broadcastMessage("player victim");
            if (Tazpvp.spawnRegion.contains(victim.getLocation())) {
                e.setCancelled(true);
                return;
            }

            if (compare(e, e.getCause() == EntityDamageEvent.DamageCause.FALL)) return;

            double fd = e.getFinalDamage();

            if (e instanceof EntityDamageByEntityEvent ee) {
                Bukkit.broadcastMessage("test");
                if ((victim.getHealth() - fd) <= 0) {
                    e.setCancelled(true);
                    DeathFunctions.death(victim, ee.getDamager());
                    return;
                }
                if (ee.getDamager() instanceof Player killer) {
                    CombatTagFunctions.putInCombat(victim.getUniqueId(), killer.getUniqueId());
                }
            } else {
                Bukkit.broadcastMessage("test2");
                if ((victim.getHealth() - fd) <= 0) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                        Tazpvp.getObservers().forEach(observer -> observer.burn(victim));
                    }
                    DeathFunctions.death(victim, null);
                } else {
                    CombatTagFunctions.putInCombat(victim.getUniqueId(), null);
                }
            }
        } else {
            Bukkit.broadcastMessage("no");
        }
    }

    private boolean compare(EntityDamageEvent e, boolean condition) {
        if (condition) e.setCancelled(true);
        return condition;
    }
}

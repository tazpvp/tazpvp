/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, n-tdi
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
 *
 */

package net.tazpvp.tazpvp.utils.kit;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableInventory implements Serializable {
    @Getter
    private final ItemStack[] hotbar;

    public SerializableInventory(ItemStack[] hotbar) {
        this.hotbar = hotbar;
    }

    public void addItems(PlayerInventory inventory, ItemStack... itemStacks) {
        List<ItemStack> toAdd = new ArrayList<>();
        for (int i = 0; i < hotbar.length; i++) {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack.getType() == hotbar[i].getType()) {
                    inventory.setItem(i, itemStack);
                    continue;
                }
                toAdd.add(itemStack);
            }
        }

        inventory.addItem(toAdd.toArray(ItemStack[]::new));
    }

    public static SerializableInventory readHotbar(PlayerInventory inventory) {
        return new SerializableInventory(new ItemStack[]{
                inventory.getItem(0), inventory.getItem(0), inventory.getItem(0), inventory.getItem(0), inventory.getItem(0), inventory.getItem(0), inventory.getItem(0), inventory.getItem(0), inventory.getItem(0)
        });
    }
}
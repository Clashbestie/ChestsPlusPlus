package com.jamesdpeters.minecraft.chests.listeners;

import com.jamesdpeters.minecraft.chests.ChestsPlusPlus;
import com.jamesdpeters.minecraft.chests.interfaces.VirtualCraftingHolder;
import com.jamesdpeters.minecraft.chests.interfaces.VirtualInventoryHolder;
import com.jamesdpeters.minecraft.chests.misc.Utils;
import com.jamesdpeters.minecraft.chests.sort.InventorySorter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryPlayerUpdate(InventoryClickEvent event) {
        inventoryUpdate(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        try {
            InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof VirtualInventoryHolder) {
                VirtualInventoryHolder vHolder = (VirtualInventoryHolder) holder;
                vHolder.openPreviousInventory();
                if (vHolder.didPlayerRemoteOpen(event.getPlayer().getUniqueId())) {
                    Utils.closeInventorySound((Player) event.getPlayer(), event.getInventory());
                }
                event.getViewers().remove(event.getPlayer());
                Utils.closeStorageInventory(vHolder.getStorage());
                vHolder.getStorage().onItemDisplayUpdate(InventorySorter.getMostCommonItem(event.getInventory()));
            }
            if (holder instanceof VirtualCraftingHolder) {
                ((VirtualCraftingHolder) holder).stopAnimation();
            }
        } catch (NullPointerException ignore) {
        } //Essentials does something weird with enderchests - shit fix but works :)
    }

    public void inventoryUpdate(InventoryInteractEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof VirtualInventoryHolder) {
            VirtualInventoryHolder vHolder = (VirtualInventoryHolder) holder;
            Bukkit.getScheduler().scheduleSyncDelayedTask(ChestsPlusPlus.PLUGIN, () -> {
                vHolder.getStorage().sort();
                vHolder.getStorage().onItemDisplayUpdate(InventorySorter.getMostCommonItem(event.getInventory()));
            }, 1);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryPlayerUpdate(InventoryDragEvent event) {
        inventoryUpdate(event);
        craftingUpdate(event);
    }

    //CRAFTING
    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryDragEvent(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof VirtualCraftingHolder) {
            Player p = (Player) event.getWhoClicked();
            for (int slot : event.getRawSlots()) {
                if (slot >= p.getOpenInventory().getTopInventory().getSize())
                    continue;

                setCraftingItem(event.getInventory(), slot, event.getOldCursor());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftingPlayerUpdate(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTopInventory().getHolder() instanceof VirtualCraftingHolder) {
            if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                    event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                    event.getAction() == InventoryAction.NOTHING) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }

            if (event.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
                if (event.getSlot() == 0) event.setCancelled(true);
                if (event.getSlot() >= 1 && event.getSlot() <= 9) {
                    setCraftingItem(event.getInventory(), event.getSlot(), event.getCursor());
                    event.setCancelled(true);
                    craftingUpdate(event);
                }
            }
        }
    }

    private void setCraftingItem(Inventory inventory, int slot, ItemStack cursor) {
        ItemStack clone = null;
        if (cursor != null) {
            clone = cursor.clone();
            clone.setAmount(1);
        }
        inventory.setItem(slot, clone);
    }

    private void craftingUpdate(InventoryInteractEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof VirtualCraftingHolder) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(ChestsPlusPlus.PLUGIN, (((VirtualCraftingHolder) holder).setUpdatingRecipe(true))::updateCrafting, 1);
            Bukkit.getScheduler().scheduleSyncDelayedTask(ChestsPlusPlus.PLUGIN, (((VirtualCraftingHolder) holder))::forceUpdateInventory, 1);
        }
    }

    @EventHandler
    public void craftEvent(PrepareItemCraftEvent event){
        if (event.getRecipe() == null) return;
        if (event.getRecipe().getResult().getType() == Material.DIAMOND_BLOCK){
            event.getInventory().setResult(null);
        }
    }


}

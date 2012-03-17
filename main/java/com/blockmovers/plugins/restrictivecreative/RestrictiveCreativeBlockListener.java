/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativeBlockListener implements Listener {

    private final RestrictiveCreative plugin;

    public RestrictiveCreativeBlockListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        //Block block = event.getBlock();
        Integer item = event.getBlockPlaced().getTypeId();
        //String playername = player.getName();

        if (plugin.creative.contains(player)) {
            if (!plugin.checkPerm(player, "creative", "place", item)) {
                event.setCancelled(true);
            }
        } else {
            if (!plugin.checkPerm(player, "general", "place", item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        //Block block = event.getBlock();
        Integer item = event.getBlock().getTypeId();
        //String playername = player.getName();

        if (!plugin.creative.contains(player)) {
            return;
        }

        //player.getServer().broadcastMessage(playername + " broke " + item + ". looking for " + Material.BEDROCK.getId());

        if (plugin.creative.contains(player)) {
            if (!plugin.checkPerm(player, "creative", "break", item)) {
                event.setCancelled(true);
            } else {
                Block blockAbove = event.getBlock().getRelative(BlockFace.UP);
                if (plugin.droppableItems.contains(blockAbove.getType())) {
                    blockAbove.setType(Material.AIR);
                }
            }
        } else {
            if (!plugin.checkPerm(player, "general", "break", item)) {
                event.setCancelled(true);
            }
        }
    }
}

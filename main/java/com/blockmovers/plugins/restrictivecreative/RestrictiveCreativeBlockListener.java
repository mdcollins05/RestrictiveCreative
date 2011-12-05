/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativeBlockListener extends BlockListener {
    private final RestrictiveCreative plugin;
    
    public RestrictiveCreativeBlockListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        player.getServer().broadcastMessage(player + " placed a " + block);
    }
    
}

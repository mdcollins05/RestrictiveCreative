/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativeEntityListener implements Listener {

    private final RestrictiveCreative plugin;

    public RestrictiveCreativeEntityListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            Entity attacker = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
            if (attacker instanceof Player) {
                Player player = (Player) attacker;
                if (plugin.creative.contains(player.getName())) {
                    event.setCancelled(true);
                }
            }
            if (attacker instanceof Projectile) {
                Projectile a = (Projectile) attacker;
                Entity e = a.getShooter();
                if (e instanceof Player) {
                    Player player = (Player) e;
                    if (plugin.creative.contains(player.getName())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity target = event.getTarget();

        if (target instanceof Player) {
            Player player = (Player) event.getTarget();
            //plugin.getServer().broadcastMessage(target + " targeted!");
            if (!plugin.creative.contains(player.getName())) {
                return;
            } else {
                event.setCancelled(true);
            }
        }

    }
}

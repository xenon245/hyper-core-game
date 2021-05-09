/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.hypercore.listener

import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.Hyper.ENTITY
import com.github.patrick.hypercore.block.HyperTree
import com.github.patrick.hypercore.entity.HyperSkeleton
import com.github.patrick.hypercore.plugin.HyperCorePlugin.Companion.INSTANCE
import com.github.patrick.hypercore.task.TreeCancelTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.log
import kotlin.random.Random

class HyperListener : Listener {
    @EventHandler
    fun onCreeperTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        Hyper.HYPER_CREEPERS[event.entity.entityId]?.let {
            event.isCancelled = true
            if (it.explosionStart == -1) it.explosionStart = it.entity.ticksLived
        }
    }
    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if(event.spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL) {
        }
    }

    @EventHandler
    fun onHyperPlayerRespawn(event: PlayerDeathEvent) {
        Hyper.HYPER_TREE_BUKKIT_TASKS[event.entity]?.forEach { task ->
            task.cancel()
        }
    }

    @EventHandler
    fun onTreeBreak(event: BlockBreakEvent) {
        event.block?.run {
            if (Hyper.TREE_MATERIAL.contains(type)) {
                event.isCancelled = true
                HyperTree(this, event.player)
            }
        }
    }
}
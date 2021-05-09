package com.github.patrick.hypercore.task

import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.plugin.HyperCorePlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.log
import kotlin.random.Random

class TreeCancelTask(val event: PlayerRespawnEvent) : Runnable {
    override fun run() {
        with(Hyper) {
            when (val player = event.player) {
                HYPER_BORDER_PLAYER -> {
                    HYPER_BORDER_PLAYER = null
                    HYPER_BORDER_TASK = null
                    player.world.worldBorder.run {
                        setCenter(0.0, 0.0)
                        size = 60000000.0
                    }
                }
            }
            HYPER_TREE_TASKS[event.player]?.forEach {
                val chance = log(it.tapArmorStands.count().toDouble(), 64.0) * 64 / it.tapArmorStands.count()
                it.tapArmorStands.forEach { entry ->
                    val stand = entry.key
                    stand.run {
                        if (Random.nextDouble() < chance) {
                            bukkitWorld.dropItemNaturally(
                                Location(bukkitWorld, posX, posY, posZ), stand.getEquipment(
                                    EquipmentSlot.HEAD).toItemStack())
                        }
                    }
                    Packet.ENTITY.destroy(stand.id).sendAll()
                }
            }
            val task = HYPER_TREE_BUKKIT_TASKS[event.player]?.run {
                forEach {
                    Bukkit.getScheduler().runTask(HyperCorePlugin.INSTANCE, it::cancel)
                }
                clear()
            }
        }
    }
}
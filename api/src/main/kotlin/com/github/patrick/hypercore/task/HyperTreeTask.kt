package com.github.patrick.hypercore.task

import com.github.noonmaru.tap.entity.TapArmorStand
import com.github.noonmaru.tap.packet.Packet
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.log
import kotlin.random.Random

class HyperTreeTask(private val player: Player, private val baseStand: TapArmorStand, val tapArmorStands: Map<TapArmorStand, Vector>) : Runnable {
    private var tick = 0
    private var ticks = 0
    private var attack = true
    private var dropTicks = 0

    override fun run() {
        with(Packet.ENTITY) {
            if(attack) {
                ticks++
                if(ticks == 4) {
                    player.location.run {
                        teleport(baseStand.bukkitEntity, x, y + if (++tick % 2 in 1..1) 8 else -8, z, 0F, 0F, false).sendAll()
                        if (tick % 2 == 0) player.damage(1.2)
                    }
                    tapArmorStands.forEach {
                        val stand = it.key
                        val vec = it.value
                        teleport(stand.bukkitEntity, baseStand.posX + vec.x, baseStand.posY + vec.y, baseStand.posZ + vec.z, 0F, 0F, false).sendAll()
                    }
                    if(player.health < 1) {
                        player.damage(10.0)
                        attack = false
                    }
                    ticks = 0
                }
            } else {
                if(dropTicks == 0) {
                    tapArmorStands.forEach { entry ->
                        val stand = entry.key
                        Bukkit.getWorlds().first().let {
                            it.dropItemNaturally(Location(it, stand.posX, stand.posY, stand.posZ), ItemStack(stand.getEquipment(EquipmentSlot.HEAD).toItemStack().type, 2))
                        }
                        Packet.ENTITY.destroy(stand.id).sendAll()
                    }
                    dropTicks = -1
                }
            }
        }
    }
}
package com.github.patrick.hypercore.block

import com.github.noonmaru.tap.Tap
import com.github.noonmaru.tap.Tap.ITEM
import com.github.noonmaru.tap.entity.TapArmorStand
import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.plugin.HyperCorePlugin
import com.github.patrick.hypercore.plugin.HyperCorePlugin.Companion.INSTANCE
import com.github.patrick.hypercore.task.HyperTreeTask
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.LinkedList

class HyperTree(init: Block, player: Player) {
    private val instance = HyperCorePlugin.INSTANCE
    private val blockMap = HashMap<Block, Vector>()
    private val blockQueue = LinkedList<Block>()
    private val tapArmorStands = HashMap<TapArmorStand, Vector>()
    private lateinit var baseStand: TapArmorStand

    init {
        with(blockQueue) {
            val entity = Tap.ENTITY.createEntity<TapArmorStand>(ArmorStand::class.java).apply {
                setPositionAndRotation(init.x + 0.5, init.y + 0.5, init.z + 0.5, 0F, 0F)
                isInvisible = true
                isMarker = true
            }
            with(Packet.ENTITY) {
                entity.bukkitEntity.apply {
                    spawnMob(this).sendAll()
                    metadata(this).sendAll()
                    equipment(entityId, EquipmentSlot.HEAD, Tap.ITEM.fromItemStack(ItemStack(init.type))).sendAll()
                    teleport(this, entity.posX, entity.posY, entity.posZ, entity.yaw, entity.pitch, false).sendAll()
                }
            }
            init.type = Material.AIR
            offer(init)
            blockMap[init] = Vector(0, 0, 0)
            tapArmorStands[entity] = Vector(0, 0, 0)
            baseStand = entity
            while(isNotEmpty()) {
                poll().run {
                    listOf(
                        getRelative(BlockFace.WEST),
                        getRelative(BlockFace.NORTH),
                        getRelative(BlockFace.EAST),
                        getRelative(BlockFace.SOUTH),
                        getRelative(BlockFace.UP),
                        getRelative(BlockFace.DOWN)
                    ).forEach {
                        if(Hyper.TREE_MATERIAL.contains(it.type) && !blockMap.contains(it)) {
                            val entity = Tap.ENTITY.createEntity<TapArmorStand>(ArmorStand::class.java).apply {
                                setPositionAndRotation(it.x + 0.5, it.y + 0.5, it.z + 0.5, 0F, 0F)
                                isInvisible = true
                                isMarker = true
                            }
                            Packet.ENTITY.spawnMob(entity.bukkitEntity).sendAll()
                            Packet.ENTITY.equipment(entity.id, EquipmentSlot.HEAD, Tap.ITEM.fromItemStack(ItemStack(it.type))).sendAll()
                            Packet.ENTITY.metadata(entity.bukkitEntity).sendAll()
                            Packet.ENTITY.teleport(entity.bukkitEntity, entity.posX, entity.posY, entity.posZ, entity.yaw, entity.pitch, false).sendAll()
                            offer(it)
                            blockMap[it] = it.location.subtract(init.location).toVector()
                            tapArmorStands[entity] = it.location.subtract(init.location).toVector()
                            it.type = Material.AIR
                        }
                    }
                }
            }
            tapArmorStands.forEach {
                val start = Bukkit.getScheduler().runTaskTimer(INSTANCE, {
                    it.key.setPosition(it.key.posX, it.key.posY + 0.1, it.key.posZ)
                    Packet.ENTITY.teleport(it.key.bukkitEntity, it.key.posX, it.key.posY, it.key.posZ, it.key.yaw, it.key.pitch, false).sendAll()
                }, 0L, 1L)
                Bukkit.getScheduler().runTaskLater(INSTANCE, start::cancel, 5 * 20)
                val task = HyperTreeTask(player, baseStand, tapArmorStands)
                val tasks = Bukkit.getScheduler().runTaskTimer(INSTANCE, task, 101L, 1L)
                Hyper.HYPER_TREE_TASKS[player]?.add(task)
                Hyper.HYPER_TREE_BUKKIT_TASKS[player]?.add(tasks)
            }
        }
    }
}
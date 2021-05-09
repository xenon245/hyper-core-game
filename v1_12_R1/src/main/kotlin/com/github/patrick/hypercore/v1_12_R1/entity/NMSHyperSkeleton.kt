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

package com.github.patrick.hypercore.v1_12_R1.entity

import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.entity.HyperSkeleton
import net.minecraft.server.v1_12_R1.*
import net.minecraft.server.v1_12_R1.EnumItemSlot.OFFHAND
import net.minecraft.server.v1_12_R1.Items.SPECTRAL_ARROW
import net.minecraft.server.v1_12_R1.Items.TIPPED_ARROW
import net.minecraft.server.v1_12_R1.SoundEffects.gW
import org.bukkit.Bukkit.getLogger
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory.callEntityShootBowEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.NATURAL
import org.bukkit.inventory.ItemStack
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextDouble

@Suppress("LeakingThis")
class NMSHyperSkeleton(world: World) : EntitySkeleton(world), HyperSkeleton {
    init {
        getWorld().addEntity(this, CUSTOM)
        getLogger().info("HyperSKELETON")
        Hyper.HYPER_SKELETONS.add(this)
        (bukkitEntity as LivingEntity).equipment.itemInMainHand = ItemStack(Material.BOW)
    }

    override val entity = bukkitEntity as LivingEntity

    override fun update(): Unit? = goalTarget?.run {
        a(this, ItemBow.b(cL()))
    }

    override fun a(f: Float): EntityArrow? {
        val itemstack = getEquipment(OFFHAND)
        return if (itemstack.item === SPECTRAL_ARROW) {
            val entityspectralarrow = EntitySpectralArrow(world, this)
            entityspectralarrow.a(this, f)
            entityspectralarrow
        } else {
            val entityarrow = super.a(f)
            if (itemstack.item === TIPPED_ARROW && entityarrow is EntityTippedArrow) {
                entityarrow.a(itemstack)
            }
            entityarrow
        }
    }

    override fun r() {
        goalSelector.a(1, PathfinderGoalLookAtPlayer(this, EntityHuman::class.java, 20F))
        goalSelector.a(2, PathfinderGoalFloat(this))
        goalSelector.a(2, PathfinderGoalRandomStrollLand(this, 1.0))
        goalSelector.a(2, PathfinderGoalRandomLookaround(this))
        targetSelector.a(1, PathfinderGoalHurtByTarget(this, false, *arrayOfNulls(0)))
        targetSelector.a(1, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }
}
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

import com.github.noonmaru.customentity.CustomEntityPacket
import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.entity.HyperCreeper
import com.github.patrick.hypercore.plugin.HyperCorePlugin.Companion.INSTANCE
import net.minecraft.server.v1_12_R1.AxisAlignedBB
import net.minecraft.server.v1_12_R1.EntityCreeper
import net.minecraft.server.v1_12_R1.GenericAttributes
import net.minecraft.server.v1_12_R1.World
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Creeper
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.math.cos
import kotlin.math.sin

class NMSHyperCreeper(world: World) : EntityCreeper(world), HyperCreeper {
    override var explosionStart = -1

    init {
        getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM)
        Hyper.HYPER_CREEPERS[id] = this
        CustomEntityPacket.register(id).sendAll()
        forceExplosionKnockback = true
        (bukkitEntity as Creeper).apply {
            isPowered = true
            explosionRadius = 3
            maxFuseTicks = 20
        }
    }

    override val entity = (bukkitEntity as LivingEntity)

    override fun update() {
        if (explosionStart == -1) return

        val ticks = entity.ticksLived - explosionStart
        if(ticks == 1) {
            getAttributeInstance(GenericAttributes.maxHealth).value = 256.0
            health = 256F
        }
        if (ticks < 200) {
            val scale = ticks / 4F
            val color = 255 - (ticks * 255 / 200)
            CustomEntityPacket.colorAndScale(entity.entityId, 255, color, color, scale, scale, scale, 1).sendAll()
            a(AxisAlignedBB(locX - 0.3 * scale, locY, locZ - 0.3 * scale, locX + 0.3 * scale, locY + 1.7 * scale, locZ + 0.3 * scale))
        }
        if (ticks == 200){
            val task = Bukkit.getScheduler().runTaskTimer(INSTANCE, Explosion(entity.location), 0L, 1L)
            Bukkit.getScheduler().runTaskLater(INSTANCE, task::cancel, 100L)
            do_()
        }
    }

    override fun initAttributes() {
        super.initAttributes()
    }
    inner class Explosion(val location: Location): Runnable {
        private var explosionTicks = 0
        override fun run () {
            location?.let { center ->
                val ticks = ++explosionTicks
                val radiusPerTick = 0.5
                val pointPerCircum = 6.0
                val radius = radiusPerTick * ticks
                val circum = 2.0 * Math.PI * radius
                val pointsCount = (circum / pointPerCircum).toInt()
                val angle = 360.0 / pointsCount
                val world = center.world
                val y = center.y
                for(i in 0 until pointsCount) {
                    val currentAngle = Math.toRadians(i * angle)
                    val x = -sin(currentAngle)
                    val z = cos(currentAngle)
                    world.createExplosion(center.x + x * radius, y, center.z + z * radius, 4.0F, false, true)
                }
                if(ticks > 100) {
                    explosionTicks = 0
                }
            }
        }
    }
}
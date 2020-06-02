package com.github.patrick.hypercore.v1_12_R1.entity

import com.github.noonmaru.customentity.CustomEntityPacket.colorAndScale
import com.github.noonmaru.customentity.CustomEntityPacket.register
import com.github.patrick.hypercore.Hyper.hyperCreepers
import com.github.patrick.hypercore.entity.HyperCreeper
import net.minecraft.server.v1_12_R1.AxisAlignedBB
import net.minecraft.server.v1_12_R1.EntityCreeper
import net.minecraft.server.v1_12_R1.GenericAttributes
import net.minecraft.server.v1_12_R1.World
import org.bukkit.entity.Creeper
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM

class NMSHyperCreeper(world: World) : EntityCreeper(world), HyperCreeper {
    override var explosionStart = -1

    init {
        getWorld().addEntity(this, CUSTOM)
        hyperCreepers[id] = this
        register(id).sendAll()
        forceExplosionKnockback = true
        (bukkitEntity as Creeper).let {
            it.isPowered = true
            it.explosionRadius = 512
            it.maxFuseTicks = 20
        }
    }

    override val entity = (bukkitEntity as LivingEntity)

    override fun update() {
        if (explosionStart == -1) return

        val ticks = entity.ticksLived - explosionStart
        if (ticks < 200) {
            val scale = ticks / 4F
            val color = 255 - (ticks * 255 / 200)
            colorAndScale(entity.entityId, 255, color, color, scale, scale, scale, 1).sendAll()
            a(AxisAlignedBB(locX - 0.3 * scale, locY, locZ - 0.3 * scale, locX + 0.3 * scale, locY + 1.7 * scale, locZ + 0.3 * scale))
        }
        if (ticks == 200) do_()
    }

    override fun initAttributes() {
        super.initAttributes()
        getAttributeInstance(GenericAttributes.maxHealth).value = 256.0
        health = 256F
    }
}
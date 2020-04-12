package com.github.patrick.hypercore.entity

import org.bukkit.entity.LivingEntity

interface HyperEntity {
    val entity: LivingEntity

    fun update(): Unit?
}
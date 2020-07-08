package com.github.patrick.hypercore.block

import com.github.noonmaru.tap.Tap
import com.github.noonmaru.tap.entity.TapArmorStand
import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.plugin.HyperCorePlugin
import com.github.patrick.hypercore.task.HyperBlockTask
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.LinkedList

class HyperBlock(init: Block) {
    private val instance = HyperCorePlugin.INSTANCE
    private val blockMap = HashMap<Block, Vector>()
    private val blockQueue = LinkedList<Block>()
    private val tapArmorStands = HashMap<TapArmorStand, Vector>()
    private lateinit var baseStand: TapArmorStand

    init {
        with(blockQueue) {
            offer(init)
            blockMap[init] = Vector(0, 0, 0)

            while (isNotEmpty()) {
                poll().run {
                    listOf(
                            getRelative(BlockFace.NORTH),
                            getRelative(BlockFace.EAST),
                            getRelative(BlockFace.SOUTH),
                            getRelative(BlockFace.WEST),
                            getRelative(BlockFace.UP),
                            getRelative(BlockFace.DOWN)
                    )
                }.forEach {
                    if (Hyper.WOOD_MATERIAL.contains(it.type) && !blockMap.contains(it)) {
                        offer(it)
                        blockMap[it] = it.location.subtract(init.location).toVector()
                    }
                }
            }
        }

        with(Packet.ENTITY) {
            blockMap.forEach { entry ->
                val block = entry.key
                @Suppress("DEPRECATION")
                tapArmorStands[Tap.ENTITY.createEntity<TapArmorStand>(ArmorStand::class.java).apply {
                    isInvisible = true
                    isMarker = true
                    setGravity(false)
                    setBasePlate(false)
                    bukkitWorld = block.world
                    setPosition(block.x + 0.5, block.y + 0.5, block.z + 0.5)
                    spawnMob(bukkitEntity).sendAll()
                    metadata(bukkitEntity).sendAll()
                    equipment(id, EquipmentSlot.HEAD, Tap.ITEM.fromItemStack(ItemStack(block.type, 1, block.data.toShort()))).sendAll()
                    if (block == init) {
                        baseStand = this
                    }
                }] = entry.value
                block.type = Material.AIR
            }


            tapArmorStands.run {
                Bukkit.getScheduler().run {
                    val start = runTaskTimer(instance, {
                        forEach {
                            val stand = it.key
                            teleport(stand.bukkitEntity, stand.posX, stand.posY + 0.1, stand.posZ, 0F, 0F, false).sendAll()
                        }
                    }, 0, 1)

                    with(Hyper) {
                        HYPER_BLOCK_PLAYER?.let { player ->
                            runTaskLater(instance, {
                                start.cancel()
                                forEach {
                                    val stand = it.key
                                    val vec = it.value
                                    teleport(stand.bukkitEntity, stand.posX + vec.x, stand.posY + vec.y, stand.posZ + vec.z, 0F, 0F, false).sendAll()
                                }
                            }, 100)
                            val task = HyperBlockTask(player, baseStand, tapArmorStands)
                            HYPER_BLOCK_TASKS.add(task)
                            HYPER_BLOCK_BUKKIT_TASKS.add(runTaskTimer(instance, task, 101, 1))
                        }?: run {
                            runTaskLater(instance, {
                                start.cancel()
                                forEach {
                                    destroy(it.key.id).sendAll()
                                }
                                clear()
                            }, 100)
                        }
                    }

                    runTaskTimer(instance, {
                        forEach {
                            val stand = it.key
                            metadata(stand.bukkitEntity).sendAll()
                        }
                    }, 0, 1)
                }
            }
        }
    }
}
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

package com.github.patrick.hypercore

import com.github.noonmaru.tap.LibraryLoader
import com.github.patrick.hypercore.entity.HyperCreeper
import com.github.patrick.hypercore.entity.HyperEntityManager
import com.github.patrick.hypercore.entity.HyperSkeleton
import com.github.patrick.hypercore.entity.HyperZombie
import com.github.patrick.hypercore.task.HyperTreeTask
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object Hyper {
    val ENTITY = requireNotNull(LibraryLoader.load(HyperEntityManager::class.java)) { "Unable to load NMS class." }

    var HYPER_BORDER_PLAYER: Player? = null
        internal set

    var HYPER_BLOCK_PLAYER: Player? = null
        internal set

    internal var HYPER_BORDER_TASK: Runnable? = null

    internal val HYPER_TREE_BUKKIT_TASKS = HashSet<BukkitTask>()

    internal val HYPER_TREE_TASKS = HashSet<HyperTreeTask>()

    val HYPER_SKELETONS = HashSet<HyperSkeleton>()

    val HYPER_CREEPERS = HashMap<Int, HyperCreeper>()

    val HYPER_ZOMBIES = HashMap<Int, HyperZombie>()

    internal val TREE_MATERIAL = setOf(Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2)
}
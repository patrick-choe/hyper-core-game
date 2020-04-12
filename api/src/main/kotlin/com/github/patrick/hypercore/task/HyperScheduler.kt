/*
 * Copyright (C) 2020 PatrickKR
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

package com.github.patrick.hypercore.task

import com.github.patrick.hypercore.Hyper.hyperCreepers
import com.github.patrick.hypercore.Hyper.hyperPlayer
import com.github.patrick.hypercore.Hyper.hyperSkeletons
import com.github.patrick.hypercore.Hyper.hyperTask
import com.github.patrick.hypercore.Hyper.hyperZombies

class HyperScheduler : Runnable {
    override fun run() {
        hyperPlayer?.let {
            if (hyperTask == null) {
                val border = it.world.worldBorder
                border.center = it.location
                border.size = 16.0
                border.warningDistance = 1
                hyperTask = HyperWorldBorderTask(it)
            } else {
                hyperTask?.run()
            }
        }

        hyperSkeletons.removeIf { !it.entity.isValid || it.entity.isDead }
        hyperSkeletons.forEach { it.update() }

        hyperCreepers.forEach {
            val entity = it.value.entity
            if (!entity.isValid || entity.isDead) hyperCreepers.remove(it.key)
        }
        hyperCreepers.values.forEach { it.update() }

        hyperZombies.removeIf { !it.entity.isValid || it.entity.isDead }
        hyperZombies.forEach { it.update() }
    }
}
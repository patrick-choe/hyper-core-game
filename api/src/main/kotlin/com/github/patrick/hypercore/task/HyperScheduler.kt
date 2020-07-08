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

package com.github.patrick.hypercore.task

import com.github.noonmaru.customentity.CustomEntityPacket.unregister
import com.github.patrick.hypercore.Hyper

class HyperScheduler : Runnable {
    override fun run() {
        with(Hyper) {
            HYPER_BORDER_PLAYER?.let {
                if (HYPER_BORDER_TASK == null) {
                    val border = it.world.worldBorder
                    border.center = it.location
                    border.size = 16.0
                    border.damageBuffer = 2.0
                    border.warningDistance = 1
                    HYPER_BORDER_TASK = HyperBorderTask(it)
                } else {
                    HYPER_BORDER_TASK?.run()
                }
            }

            HYPER_SKELETONS.removeIf { !it.entity.isValid || it.entity.isDead }
            HYPER_SKELETONS.forEach { it.update() }

            setOf(HYPER_CREEPERS, HYPER_ZOMBIES).forEach {
                it.forEach entry@{ entry ->
                    val entity = entry.value.entity
                    if (!entity.isValid || entity.isDead) {
                        HYPER_CREEPERS.remove(entry.key)
                        unregister(entry.key).sendAll()
                        return@entry
                    }
                    entry.value.update()
                }
            }
        }
    }
}
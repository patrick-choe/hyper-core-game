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

import org.bukkit.entity.Player
import org.bukkit.util.Vector

class HyperWorldBorderTask(private val player: Player) : Runnable {
    override fun run() {
        val border = player.world.worldBorder
        val direction = player.location.direction.normalize()
        if (direction.x == 0.0) direction.x = 0.01
        if (direction.z == 0.0) direction.z = 0.01
        val vector = Vector(direction.x, 0.0, direction.z).normalize()
        border.center = border.center.add(vector.multiply(0.1))
    }
}
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

package com.github.patrick.hypercore.command

import com.github.patrick.hypercore.Hyper
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Bukkit.getPlayerExact
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class HyperCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            when (args.count()) {
                2 -> {
                    when (args[1].toLowerCase()) {
                        "worldborder", "border" -> getPlayerExact(args[0])?.let {
                            Hyper.HYPER_BORDER_PLAYER = it
                            return true
                        }
                        "tree" -> getPlayerExact(args[0])?.let {
                            Hyper.HYPER_BLOCK_PLAYER = it
                            return true
                        }
                    }
                }
            }
            if (args[0] == "help") {
                sender.sendMessage("/$label <Player> <Type> to start hyper")
                return true
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.count()) {
            1 -> getOnlinePlayers().map { it.name }.filter(args[0])
            2 -> listOf("border", "worldborder", "tree").filter(args[1])
            else -> emptyList()
        }
    }
}

private fun List<String>.filter(keyword: String) = filter { it.toLowerCase().startsWith(keyword.toLowerCase()) }
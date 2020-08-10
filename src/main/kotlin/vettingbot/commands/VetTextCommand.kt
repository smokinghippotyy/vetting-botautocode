/*
 * Copyright (C) 2020  Rosetta Roberts <rosettafroberts@gmail.com>
 *
 * This file is part of VettingBot.
 *
 * VettingBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VettingBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VettingBot.  If not, see <https://www.gnu.org/licenses/>.
 */

package vettingbot.commands

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.rest.util.Permission
import org.springframework.stereotype.Component
import vettingbot.command.AbstractCommand
import vettingbot.guild.GuildConfigService
import vettingbot.util.embedDsl
import vettingbot.util.nullable
import vettingbot.util.respondEmbed

@Component
class VetTextCommand(
    private val guildConfigService: GuildConfigService
) : AbstractCommand(
    "vettext",
    "Manage the vetting message sent to new members.",
    Permission.ADMINISTRATOR
) {
    override suspend fun displayHelp(guildId: Snowflake) = embedDsl {
        field("Parameters", "`{member}` is replaced with a mention of the vetting member.")
    }

    override suspend fun run(message: MessageCreateEvent, args: String) {
        val guildId = message.guildId.nullable ?: return
        val vettingText = guildConfigService.getVettingText(guildId)
        if (args.isBlank()) {
            message.respondEmbed {
                title("Vetting Text")
                description(vettingText)
            }
        } else {
            guildConfigService.setVettingText(guildId, args)
            message.respondEmbed {
                title("Vetting Text")
                description("Changed vetting text.")
                field("Before", vettingText)
                field("After", args)
            }
        }
    }
}
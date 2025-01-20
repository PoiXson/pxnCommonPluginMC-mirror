package com.poixson.vitalcore.commands;

import static com.poixson.tools.commands.PluginCommand.ConsoleCannotUse;
import static com.poixson.tools.commands.PluginCommand.GetArg_Players;
import static com.poixson.tools.commands.PluginCommand.HasPermissionUseCMD;
import static com.poixson.tools.commands.PluginCommand.HasPermissionUseOthersCMD;
import static com.poixson.utils.BukkitUtils.RestPlayer;
import static com.poixson.utils.Utils.IsEmpty;
import static com.poixson.vitalcore.VitalCoreDefines.CMD_LABELS_REST;
import static com.poixson.vitalcore.VitalCoreDefines.PERM_CMD_REST;
import static com.poixson.vitalcore.VitalCoreDefines.PERM_CMD_REST_OTHERS;
import static com.poixson.vitalcore.VitalCorePlugin.CHAT_PREFIX;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.poixson.tools.commands.PluginCommand;
import com.poixson.vitalcore.VitalCorePlugin;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


// /rest
public interface CMD_Rest extends PluginCommand {



	default ArgumentBuilder<CommandSourceStack, ?> register_Rest(final VitalCorePlugin plugin) {
		return Commands.literal(CMD_LABELS_REST.NODE)
			// /rest
			.executes(context -> this.onCommand_Rest(context, plugin))
			// /rest <players>
			.then(Commands.argument("players", ArgumentTypes.players())
				.executes(context -> this.onCommand_Rest(context, plugin))
			);
	}



	default int onCommand_Rest(final CommandContext<CommandSourceStack> context, final VitalCorePlugin plugin) {
		final CommandSourceStack source = context.getSource();
		final CommandSender sender = source.getSender();
		final Player[] others = GetArg_Players(context);
		// self
		if (IsEmpty(others)) {
			// no console
			if (ConsoleCannotUse(sender))
				return FAILURE;
			// permission self
			if (!HasPermissionUseCMD(sender, PERM_CMD_REST.NODE))
				return FAILURE;
			final Player self = (Player) sender;
			RestPlayer(self);
			sender.sendMessage(Component.text("You are rested").color(NamedTextColor.GREEN));
		// other players
		} else {
			if (!HasPermissionUseOthersCMD(sender, PERM_CMD_REST_OTHERS.NODE))
				return FAILURE;
			int count = 0;
			for (final Player p : others) {
				RestPlayer(p);
				p.sendMessage(Component.text("You are rested").color(NamedTextColor.GREEN));
				count++;
			}
			if (count > 0) {
				sender.sendMessage(CHAT_PREFIX.append(Component.text(String.format(
					"Rested %d player%s",
					Integer.valueOf(count),
					(count == 1 ? "" : "s")
				)).color(NamedTextColor.AQUA)));
			}
		}
		return SUCCESS;
	}



}

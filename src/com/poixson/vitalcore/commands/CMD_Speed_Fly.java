package com.poixson.vitalcore.commands;

import static com.poixson.vitalcore.VitalCoreDefines.CMD_LABELS_SPEED_FLY;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.poixson.tools.commands.PluginCommand;
import com.poixson.vitalcore.VitalCorePlugin;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;


// /flyspeed
public interface CMD_Speed_Fly extends PluginCommand {



	default ArgumentBuilder<CommandSourceStack, ?> register_Speed_Fly(final VitalCorePlugin plugin) {
		return Commands.literal(CMD_LABELS_SPEED_FLY.NODE)
			// /flyspeed
			.executes(context -> this.onCommand_SpeedFly(context, plugin));
	}



	default int onCommand_SpeedFly(final CommandContext<CommandSourceStack> context, final VitalCorePlugin plugin) {
//TODO
context.getSource().getSender().sendPlainMessage("FLY-SPEED!!!!!!!!!!!!!!!!!!!!!");
		return SUCCESS;
	}



}

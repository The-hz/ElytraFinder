/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class CommandExample
extends Command {
    public CommandExample() {
        super("example", "\u53d1\u9001\u4e00\u6761\u6d88\u606f\u3002", new String[0]);
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.info("hi", new Object[0]);
            return 1;
        });
        builder.then(CommandExample.literal((String)"name").then(CommandExample.argument((String)"nameArgument", (ArgumentType)StringArgumentType.word()).executes(context -> {
            String argument = StringArgumentType.getString((CommandContext)context, (String)"nameArgument");
            this.info("hi, " + argument, new Object[0]);
            return 1;
        })));
    }
}


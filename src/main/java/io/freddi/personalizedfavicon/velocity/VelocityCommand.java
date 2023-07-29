package io.freddi.personalizedfavicon.velocity;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import io.freddi.personalizedfavicon.utils.PersonalizedFaviconCommand;

public class VelocityCommand{

    public static BrigadierCommand command(ProxyServer proxy){
        LiteralCommandNode<CommandSource> root = LiteralArgumentBuilder
                .<CommandSource>literal("pf")
                .requires(source -> source.hasPermission("personalizedfavicon.command"))
                .executes(context -> {
                    new PersonalizedFaviconCommand(context.getSource()).help();
                    return Command.SINGLE_SUCCESS;
                })
                .build();

        root.addChild(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .requires(source -> source.hasPermission("personalizedfavicon.command.reload"))
                .executes(context -> {
                    new PersonalizedFaviconCommand(context.getSource()).reload();
                    return Command.SINGLE_SUCCESS;
                })
                .build());

        root.addChild(LiteralArgumentBuilder.<CommandSource>literal("clear")
                .requires(source -> source.hasPermission("personalizedfavicon.command.clear"))
                .executes(context -> {
                    new PersonalizedFaviconCommand(context.getSource()).clear();
                    return Command.SINGLE_SUCCESS;
                })
                        .build());

        root.addChild(LiteralArgumentBuilder.<CommandSource>literal("edit")
                        .requires(source -> source.hasPermission("personalizedfavicon.command.edit"))
                        .executes(context -> {
                            new PersonalizedFaviconCommand(context.getSource()).edit();
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());

        root.addChild(LiteralArgumentBuilder.<CommandSource>literal("load")
                        .requires(source -> source.hasPermission("personalizedfavicon.command.edit"))
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("argument", StringArgumentType.word())
                                .executes(context -> {

                                    new PersonalizedFaviconCommand(context.getSource()).load(context.getArgument("argument", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .build());

        return new BrigadierCommand(root);
    }
}

package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.command.RootCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandRegistry {
    @SubscribeEvent
    public static void onServerStaring(RegisterCommandsEvent event) {
        RootCommand.register(event.getDispatcher());
    }
}

package com.anatawa12.telnetCommandSender;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@SuppressWarnings("unused")
@Mod(modid = ModTelnetCommandSender.MODID)
public class ModTelnetCommandSender {
    public static final String MODID = "telnet-minecraft-command-sender";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }
}

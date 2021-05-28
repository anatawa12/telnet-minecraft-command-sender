package com.anatawa12.telnetMc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@SuppressWarnings("unused")
@Mod(modid = "telnet-minecraft-command-sender")
public class ModTelnetCommandSender {
    public static final Logger LOGGER = LogManager.getLogger("ModTelnetCommandSender");

    public static String address;
    public static int port;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        cfg.load();

        address = cfg.getString("address", "telnet", "0.0.0.0",
                "ip address to bind");
        port = cfg.getInt("port", "telnet", 25566, 
                0, 65535, 
                "port to bind");

        cfg.save();
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server instanceof DedicatedServer) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(address, port));
                TelnetServerThread thread =
                        new TelnetServerThread(serverSocket, (DedicatedServer) server);
                thread.start();
            } catch (IOException e) {
                LOGGER.error("Exception starting server", e);
                IOUtils.closeQuietly(serverSocket);
            }
        }
    }
}

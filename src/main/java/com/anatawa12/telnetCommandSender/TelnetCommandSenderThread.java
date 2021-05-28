package com.anatawa12.telnetCommandSender;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TelnetCommandSenderThread extends Thread implements ICommandSender {
    private final Logger LOGGER;
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final DedicatedServer server;

    public TelnetCommandSenderThread(Socket socket, DedicatedServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.LOGGER = LogManager.getLogger("TelnetCommandSenderThread/" + socket.getInetAddress());
    }

    @Override
    public void run() {
        try {
            String s4;

            while (!server.isServerStopped() && server.isServerRunning() &&
                    (s4 = reader.readLine()) != null) {
                server.addPendingCommand(s4, this);
            }
        } catch (IOException ioexception1) {
            LOGGER.error("Exception handling input", ioexception1);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(socket);
        }
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        try {
            writer.write(component.getUnformattedText());
            writer.write("\r\n");
        } catch (IOException e) {
            LOGGER.error("Exception handling output", e);
        }
    }

    @Override
    public String getCommandSenderName() {
        return "Telnet";
    }

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    // highest privileges

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
        return true;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return new ChunkCoordinates(0, 0, 0);
    }

    @Override
    public World getEntityWorld() {
        return server.worldServers[0];
    }
}

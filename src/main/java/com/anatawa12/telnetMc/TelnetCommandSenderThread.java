package com.anatawa12.telnetMc;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static com.anatawa12.telnetMc.ModTelnetCommandSender.LOGGER;

public class TelnetCommandSenderThread extends Thread implements ICommandSender {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final DedicatedServer server;

    public TelnetCommandSenderThread(Socket socket, DedicatedServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String name = "TelnetCommandSenderThread/" + socket.getInetAddress();
        this.setName(name);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            String s4;

            writer.write(">");
            writer.flush();
            while (!server.isServerStopped() && server.isServerRunning() &&
                    (s4 = reader.readLine()) != null) {
                writer.write(">");
                LOGGER.info("{} issued command: {}", socket.getInetAddress(), s4);
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
            writer.write("\r");
            writer.write(component.getUnformattedText());
            writer.write("\r\n>");
            writer.flush();
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

package com.anatawa12.telnetMc;

import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.anatawa12.telnetMc.ModTelnetCommandSender.LOGGER;

public class TelnetServerThread extends Thread {
    private final ServerSocket serverSocket;
    private final DedicatedServer server;

    public TelnetServerThread(ServerSocket serverSocket, DedicatedServer server) {
        this.serverSocket = serverSocket;
        this.server = server;
        this.setName(TelnetServerThread.class.getName());
        this.setDaemon(true);
        assert serverSocket.isBound() : "the serverSocket must be bound.";
    }

    @Override
    public void run() {
        try {
            LOGGER.info("telnet server starting...");
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.info("accepted a connection from: {}", socket.getInetAddress());
                try {
                    new TelnetCommandSenderThread(socket, server).start();
                } catch (IOException e) {
                    LOGGER.error("Exception handling connect", e);
                    socket.close();
                } catch (Throwable e) {
                    socket.close();
                    throw e;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception handling connect", e);
        } finally {
            IOUtils.closeQuietly(serverSocket);
        }
    }
}

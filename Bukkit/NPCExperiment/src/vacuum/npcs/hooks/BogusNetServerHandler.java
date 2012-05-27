package vacuum.npcs.hooks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;

import vacuum.npcs.Plugin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet0KeepAlive;
import net.minecraft.server.Packet13PlayerLookMove;
import net.minecraft.server.Packet8UpdateHealth;
import net.minecraft.server.Packet9Respawn;
import net.minecraft.server.WorldServer;

public class BogusNetServerHandler extends NetServerHandler{

	private BogusSocket mysocket;
	private FileOutputStream filewriter;
	private PrintWriter pw;

	public BogusNetServerHandler(MinecraftServer minecraftserver, EntityNPC entityplayer) {
		super(minecraftserver, new BogusNetworkManager(new BogusSocket(), "entitynpc#" + entityplayer.toString(), new BogusNetHandler()), entityplayer);
		this.mysocket = (BogusSocket)super.networkManager.socket;
		File log = new File(Plugin.pluginDataFolder + File.separator + "log.txt");

		try {
			log.getParentFile().mkdirs();
			log.delete();
			log.createNewFile();
			filewriter = new FileOutputStream(log);
			pw = new PrintWriter(filewriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendPacket(final Packet packet) {
		if(packet instanceof Packet13PlayerLookMove || packet instanceof Packet0KeepAlive){
			System.out.println("Returning packet " + packet.b() + "!");
			mysocket.queuePacket(packet);
		} else if (packet instanceof Packet8UpdateHealth){
			if(((Packet8UpdateHealth) packet).a <= 0){
				System.out.println("Recieved packet " + packet.b() + " with field a = " + ((Packet8UpdateHealth) packet).a + ", replying with packet 9");
				WorldServer worldserver = ((CraftServer)Bukkit.getServer()).getHandle().server.getWorldServer(0);
				mysocket.queuePacket(new Packet9Respawn((byte)worldserver.getWorld().getEnvironment().getId(), (byte)worldserver.difficulty, worldserver.getWorldData().getType(), worldserver.getHeight(), player.itemInWorldManager.getGameMode()));
			}
		}
		try {
			pw.write("Got packet: " + packet.b() + "; packet data: ");
			pw.flush();
			Packet.a(packet, new DataOutputStream(filewriter));
			filewriter.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO: handle chat
		//
	}



}

package vacuum.npcs.hooks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.Bukkit;

import vacuum.npcs.Plugin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet0KeepAlive;

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
	public void sendPacket(Packet arg0) {
		System.out.println("Packet!");
		if(arg0 instanceof Packet0KeepAlive){
			System.out.println("Got packet 0!");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				Packet.a(arg0, new DataOutputStream(out));
			} catch (IOException e) {
				e.printStackTrace();
			}
			mysocket.queuePacket(out.toByteArray());
		}
		try {
			pw.write("Got packet: " + arg0.b() + "; packet data: ");
			pw.flush();
			Packet.a(arg0, new DataOutputStream(filewriter));
			filewriter.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO: handle chat
	}



}

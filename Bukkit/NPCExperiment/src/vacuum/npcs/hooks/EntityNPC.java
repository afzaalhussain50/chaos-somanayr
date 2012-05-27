package vacuum.npcs.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigurationManager;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;

public class EntityNPC extends EntityPlayer {
	public EntityNPC(String name) {
		super(((CraftServer) Bukkit.getServer()).getHandle().server,
				((CraftServer) Bukkit.getServer()).getHandle().server
						.getWorldServer(0), name, new ItemInWorldManager(
						((CraftServer) Bukkit.getServer()).getHandle().server
								.getWorldServer(0)));

		/*
		 * Login! This is all taken from the MineCraft source. I tried to omit
		 * as much as I could (network stuff)
		 */

		/* load player data */
		// ((CraftServer)Bukkit.getServer()).getHandle().b(this);
		System.out.println(this.getBukkitEntity());
		server.serverConfigurationManager.b(this);
		this.itemInWorldManager.a((WorldServer) world);

		/* We are officially "logged in" */

		WorldServer worldserver = (WorldServer) this.world;
		ChunkCoordinates chunkcoordinates = worldserver.getSpawn();

		this.itemInWorldManager.b(worldserver.getWorldData().getGameType());

		new BogusNetServerHandler(server, this);

		int maxPlayers = this.server.serverConfigurationManager.getMaxPlayers();
		if (maxPlayers > 60) {
			maxPlayers = 60;
		}

		this.server.serverConfigurationManager.players.add(this);

		// spawn player
		worldserver.chunkProviderServer.getChunkAt((int) this.locX >> 4,
				(int) this.locZ >> 4);

		if (!((CraftServer) Bukkit.getServer()).useExactLoginLocation()) {
			while (worldserver.getCubes(this, this.boundingBox).size() != 0) {
				this.setPosition(this.locX, this.locY + 1.0D, this.locZ);
			}
		}
		this.setPosition(this.locX, this.locY
				+ this.getBukkitEntity().getEyeHeight(), this.locZ);

		worldserver.addEntity(this);

		System.out.println("Spawned player at " + this.locX + ", " + this.locY
				+ ", " + this.locZ);

		// add to player manager
		PlayerManager playerManager = server.getWorldServer(this.dimension).manager;

		playerManager.addPlayer(this);

		// notify other players that we joined and tell them our name
		Packet201PlayerInfo packet = new Packet201PlayerInfo(this.listName,
				true, 1000);
		for (int i = 0; i < server.serverConfigurationManager.players.size(); i++) {
			EntityPlayer entityplayer1 = (EntityPlayer) server.serverConfigurationManager.players
					.get(i);

			if (entityplayer1.getBukkitEntity().canSee(this.getBukkitEntity())) {
				entityplayer1.netServerHandler.sendPacket(packet);
			}

		}

		// teleport to spawn position
		netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw,
				this.pitch); // FIXME: do I need this?

		// add to net thread
		this.server.networkListenThread.a(this.netServerHandler); // FIXME: do I
																	// need
																	// this?

		syncInventory(); // FIXME: do I need this?

	}
}

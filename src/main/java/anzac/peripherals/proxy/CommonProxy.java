package anzac.peripherals.proxy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.network.ChatMessage;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.ChatTileEntity;
import anzac.peripherals.upgrades.DropConsumer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public abstract class CommonProxy implements IProxy {
	protected static WeakReference<EntityPlayer> internalFakePlayer = new WeakReference<EntityPlayer>(null);
	private final static Map<Entity, DropConsumer> dropConsumers = new WeakHashMap<Entity, DropConsumer>();
	private final static Map<ChatTileEntity, Boolean> chatBoxMap = new WeakHashMap<ChatTileEntity, Boolean>();
	public static SimpleNetworkWrapper NETWORK;

	private WeakReference<EntityPlayer> createNewPlayer(final WorldServer world) {
		final EntityPlayer player = FakePlayerFactory.get(world, Reference.gameProfile);

		return new WeakReference<EntityPlayer>(player);
	}

	private WeakReference<EntityPlayer> createNewPlayer(final WorldServer world, final int x, final int y, final int z) {
		final EntityPlayer player = FakePlayerFactory.get(world, Reference.gameProfile);
		player.posY = y;
		player.posZ = z;
		return new WeakReference<EntityPlayer>(player);
	}

	@Override
	public final WeakReference<EntityPlayer> getInternalFakePlayer(final WorldServer world) {
		if (internalFakePlayer.get() == null) {
			internalFakePlayer = createNewPlayer(world);
		} else {
			internalFakePlayer.get().worldObj = world;
		}

		return internalFakePlayer;
	}

	@Override
	public final WeakReference<EntityPlayer> getInternalFakePlayer(final WorldServer world, final int x, final int y,
			final int z) {
		if (internalFakePlayer.get() == null) {
			internalFakePlayer = createNewPlayer(world, x, y, z);
		} else {
			internalFakePlayer.get().worldObj = world;
			internalFakePlayer.get().posX = x;
			internalFakePlayer.get().posY = y;
			internalFakePlayer.get().posZ = z;
		}

		return internalFakePlayer;
	}

	public void setEntityDropConsumer(final Entity entity, final DropConsumer consumer) {
		if (!dropConsumers.containsKey(entity)) {
			final boolean captured = entity.captureDrops;

			if (!captured) {
				entity.captureDrops = true;

				final ArrayList<EntityItem> items = entity.capturedDrops;

				if ((items == null) || (items.size() == 0)) {
					dropConsumers.put(entity, consumer);
				}
			}
		}
	}

	public void clearEntityDropConsumer(final Entity entity) {
		if (dropConsumers.containsKey(entity)) {
			final boolean captured = entity.captureDrops;

			if (captured) {
				entity.captureDrops = false;

				final ArrayList<EntityItem> items = entity.capturedDrops;

				if (items != null) {
					dispatchEntityDrops(entity, items);
					items.clear();
				}
			}
			dropConsumers.remove(entity);
		}
	}

	private static void dispatchEntityDrops(final Entity entity, final ArrayList<EntityItem> drops) {
		final DropConsumer consumer = dropConsumers.get(entity);
		if (consumer != null) {
			final Iterator<EntityItem> it = drops.iterator();
			while (it.hasNext()) {
				final EntityItem entityItem = it.next();
				consumer.consumeDrop(entity, entityItem.getEntityItem());
			}
			drops.clear();
		}
	}

	public void addChatBox(final ChatTileEntity box) {
		chatBoxMap.put(box, true);
	}

	public void removeChatBox(final ChatTileEntity box) {
		chatBoxMap.remove(box);
	}

	@Override
	public void registerNetwork() {
		NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("anzac");
		NETWORK.registerMessage(ChatMessage.ChatMessageHandler.class, ChatMessage.class, 0, Side.CLIENT);
	}

	public void sendMessage(final TileEntity te, final String text, final double range) {
		NETWORK.sendToAllAround(new ChatMessage(text), new NetworkRegistry.TargetPoint(
				te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, range));
	}

	public boolean sendMessage(final String ign, final TileEntity te, final String text, final double range) {
		final EntityPlayer player = getPlayer(ign, te.getWorldObj());
		if (player != null) {
			final Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
			if (playerPos.distanceTo(Vec3.createVectorHelper(te.xCoord, te.yCoord, te.zCoord)) > range) {
				return false;
			}
			NETWORK.sendTo(new ChatMessage(text), (EntityPlayerMP) player);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static EntityPlayer getPlayer(final String ign, final World w) {
		final List<EntityPlayer> players = w.playerEntities;
		for (final EntityPlayer p : players) {
			if (p.getDisplayName().equalsIgnoreCase(ign)) {
				return p;
			}
		}
		return null;
	}

	public static class ForgeHandlers {
		@SubscribeEvent
		public void onEntityLivingDrops(final LivingDropsEvent event) {
			dispatchEntityDrops(event.entity, event.drops);
		}

		@SubscribeEvent
		public void onChat(final ServerChatEvent event) {
			for (final ChatTileEntity box : chatBoxMap.keySet()) {
				if (Vec3.createVectorHelper(box.xCoord, box.yCoord, box.zCoord).distanceTo(
						event.player.getPosition(1.0f)) <= ConfigurationHandler.chatRange)
					box.fireChatEvent(event.player, event.message);
			}
		}

		@SubscribeEvent
		public void onDeath(final LivingDeathEvent event) {
			if (event.entity instanceof EntityPlayer) {
				for (final ChatTileEntity box : chatBoxMap.keySet()) {
					if (Vec3.createVectorHelper(box.xCoord, box.yCoord, box.zCoord).distanceTo(
							((EntityPlayer) event.entity).getPosition(1.0f)) <= ConfigurationHandler.chatRange)
						box.fireDeathEvent((EntityPlayer) event.entity, event.source);
				}
			}
		}
	}
}

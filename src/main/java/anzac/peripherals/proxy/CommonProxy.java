package anzac.peripherals.proxy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.upgrades.DropConsumer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class CommonProxy implements IProxy {
	protected static WeakReference<EntityPlayer> internalFakePlayer = new WeakReference<EntityPlayer>(null);
	private final static Map<Entity, DropConsumer> dropConsumers = new WeakHashMap<Entity, DropConsumer>();

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

	public static class ForgeHandlers {
		@SubscribeEvent
		public void onEntityLivingDrops(final LivingDropsEvent event) {
			dispatchEntityDrops(event.entity, event.drops);
		}
	}
}

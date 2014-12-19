package anzac.peripherals.proxy;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import anzac.peripherals.upgrades.DropConsumer;

public interface IProxy {
	public void registerKeyBindings();

	public WeakReference<EntityPlayer> getInternalFakePlayer(final WorldServer world);

	public WeakReference<EntityPlayer> getInternalFakePlayer(final WorldServer world, final int x, final int y,
			final int z);

	public void setEntityDropConsumer(final Entity entity, final DropConsumer consumer);

	public void clearEntityDropConsumer(final Entity entity);
}

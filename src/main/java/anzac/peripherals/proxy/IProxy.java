package anzac.peripherals.proxy;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import anzac.peripherals.tile.ChatTileEntity;
import anzac.peripherals.upgrades.DropConsumer;

public interface IProxy {
	public void registerKeyBindings();

	public WeakReference<EntityPlayer> getInternalFakePlayer(final WorldServer world);

	public WeakReference<EntityPlayer> getInternalFakePlayer(final WorldServer world, final int x, final int y,
			final int z);

	public void setEntityDropConsumer(final Entity entity, final DropConsumer consumer);

	public void clearEntityDropConsumer(final Entity entity);

	public void addChatBox(final ChatTileEntity box);

	public void removeChatBox(final ChatTileEntity box);

	public void registerNetwork();

	public void sendMessage(final TileEntity te, final String text, final double range);

	public boolean sendMessage(final String ign, final TileEntity te, final String text, final double range);
}

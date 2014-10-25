package anzac.peripherals.proxy;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import anzac.peripherals.reference.Reference;

public abstract class CommonProxy implements IProxy {
	/* BUILDCRAFT PLAYER */
	protected static WeakReference<EntityPlayer> internalFakePlayer = new WeakReference<EntityPlayer>(null);

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
}

package anzac.peripherals.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import anzac.peripherals.Peripherals;
import anzac.peripherals.annotations.Event;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.peripherals.PeripheralEvent;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Peripheral(type = "chat")
public class ChatTileEntity extends BaseTileEntity {

	@Event(PeripheralEvent.chat)
	public void fireChatEvent(final EntityPlayer player, final String message) {
		fireEvent(PeripheralEvent.chat, player.getDisplayName(), message);
	}

	@Event(PeripheralEvent.death)
	public void fireDeathEvent(final EntityPlayer player, final DamageSource source) {
		String killer = null;
		if (source instanceof EntityDamageSource) {
			final Entity ent = ((EntityDamageSource) source).getEntity();
			if (ent != null)
				killer = ent.getCommandSenderName();
		}
		fireEvent(PeripheralEvent.death, player.getDisplayName(), killer, source.damageType);
	}

	@PeripheralMethod
	public void say(final String text) {
		say(text, ConfigurationHandler.chatRange, null);
	}

	@PeripheralMethod
	public void say(final String text, final int range, final String sender) {
		// if (ticker == Config.sayRate) {
		// throw new LuaException("Please try again later, you are sending messages too often");
		// }
		String message;
		if (sender != null) {
			message = "[" + sender + "] " + text;
		} else {
			message = "[@] " + text;
		}
		synchronized (this) {
			Peripherals.proxy.sendMessage(this, message, range);
			// subticker = TICKER_INTERVAL;
			// ticker++;
		}
	}

	@PeripheralMethod
	public void tell(final String player, final String text) {
		tell(player, text, ConfigurationHandler.chatRange, null);
	}

	@PeripheralMethod
	public void tell(final String player, final String text, final int range, final String sender) {
		// if (ticker == Config.sayRate) {
		// throw new LuaException("Please try again later, you are sending messages too often");
		// }
		String message;
		if (sender != null) {
			message = "[" + sender + "] " + text;
		} else {
			message = "[@] " + text;
		}
		Peripherals.proxy.sendMessage(player, this, message, range);
		// subticker = TICKER_INTERVAL;
		// ticker++;
	}

	@Override
	public void attach(final IComputerAccess computer) {
		Peripherals.proxy.addChatBox(this);
		super.attach(computer);
	}

	@Override
	public void detach(final IComputerAccess computer) {
		super.detach(computer);
		Peripherals.proxy.removeChatBox(this);
	}
}

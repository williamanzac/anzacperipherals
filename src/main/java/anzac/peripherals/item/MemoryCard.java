package anzac.peripherals.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import anzac.peripherals.reference.Names;

public class MemoryCard extends BaseItem {
	public MemoryCard() {
		setUnlocalizedName(Names.Items.memorycard);
	}

	public boolean shouldPassSneakingClickToBlock(final World par2World, final int par4, final int par5, final int par6) {
		return true;
	}

	@Override
	public boolean onItemUseFirst(final ItemStack stack, final EntityPlayer player, final World world, final int x,
			final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ) {
		final ItemStack copy = stack.copy();
		// AnzacPeripheralsCore.logger.info("isRemote: " + tileEntity.worldObj.isRemote);
		if (world.isAirBlock(x, y, z)) {
			if (copy.hasTagCompound() && player.isSneaking()) {
				copy.setTagCompound(null);
				player.addChatMessage(new ChatComponentText("clearing stored coordinates"));
				updateInventory(player, copy);
			}
			return false;
		}
		// is card
		NBTTagCompound tagCompound;
		if (player.isSneaking()) {
			if (!copy.hasTagCompound()) {
				copy.setTagCompound(new NBTTagCompound());
			}
			tagCompound = copy.getTagCompound();
			player.addChatMessage(new ChatComponentText("storing; x:" + x + ", y:" + y + ", z:" + z));
			tagCompound.setInteger("linkx", x);
			tagCompound.setInteger("linky", y);
			tagCompound.setInteger("linkz", z);
			tagCompound.setInteger("linkd", world.provider.dimensionId);
			updateInventory(player, copy);
			return true;
		}
		return false;
	}

	private void updateInventory(final EntityPlayer player, final ItemStack stack) {
		player.setCurrentItemOrArmor(0, stack);
		// TODO get fhis working
		// final Packet PlayerInventory packet = new Packet5PlayerInventory(player.entityId,
		// player.inventory.currentItem, stack);
		// PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
		// player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (stack.hasTagCompound()) {
			final NBTTagCompound tag = stack.getTagCompound();
			final int x = tag.getInteger("linkx");
			final int y = tag.getInteger("linky");
			final int z = tag.getInteger("linkz");
			final int d = tag.getInteger("linkd");
			list.add("Destination:");
			list.add(EnumChatFormatting.GRAY + "X: " + x);
			list.add(EnumChatFormatting.GRAY + "Y: " + y);
			list.add(EnumChatFormatting.GRAY + "Z: " + z);
			list.add(EnumChatFormatting.GRAY + "D: " + d);
		}
	}
}

package anzac.peripherals.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;

/**
 * This item is used as a storage device by some of the peripherals added by this mod. It has a similar capacity to that
 * of a standard ComputerCraft computer.
 */
public class HDD extends BaseItem implements IMedia {
	public HDD() {
		setUnlocalizedName(Names.Items.hdd);
	}

	@Override
	public String getLabel(ItemStack stack) {
		final NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("label")) {
			return null;
		}
		return tag.getString("label");
	}

	@Override
	public boolean setLabel(ItemStack stack, String label) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound tag = stack.getTagCompound();
		tag.setString("label", label);
		return true;
	}

	@Override
	public String getAudioTitle(ItemStack stack) {
		return null;
	}

	@Override
	public String getAudioRecordName(ItemStack stack) {
		return null;
	}

	@Override
	public IMount createDataMount(ItemStack stack, World world) {
		int diskID = getDiskID(stack);
		if (diskID < 0) {
			diskID = ComputerCraftAPI.createUniqueNumberedSaveDir(world, "anzac/hdd");
			setDiskID(stack, diskID);
		}
		return ComputerCraftAPI.createSaveDirMount(world, "anzac/hdd/" + diskID, ConfigurationHandler.hddSize);
	}

	public int getDiskID(final ItemStack stack) {
		final NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("diskid")) {
			return -1;
		}
		return tag.getInteger("diskid");
	}

	protected void setDiskID(final ItemStack stack, final int id) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound tag = stack.getTagCompound();
		tag.setInteger("diskid", id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (stack.hasTagCompound()) {
			final NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("diskid")) {
				final int diskid = tag.getInteger("diskid");
				list.add(EnumChatFormatting.GRAY + "ID: " + diskid);
			}
			if (tag.hasKey("label")) {
				final String label = tag.getString("label");
				list.add(EnumChatFormatting.GRAY + "Label: " + label);
			}
		}
	}
}

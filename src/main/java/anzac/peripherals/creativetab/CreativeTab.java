package anzac.peripherals.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import anzac.peripherals.init.ModItems;
import anzac.peripherals.reference.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTab {
	public static final CreativeTabs peripheralTab = new CreativeTabs(Reference.MOD_ID.toLowerCase()) {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return ModItems.basiccpu;
		}
	};
}

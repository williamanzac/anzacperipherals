package anzac.peripherals.peripherals;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;

public class Trigger {
	private final ITrigger trigger;
	private final ItemStack parameter;
	private final ForgeDirection side;

	public Trigger(final ITrigger trigger, final ItemStack parameter, final ForgeDirection side) {
		this.trigger = trigger;
		this.parameter = parameter;
		this.side = side;
	}

	public ITrigger getTrigger() {
		return trigger;
	}

	public ItemStack getParameter() {
		return parameter;
	}

	public ITriggerParameter getTriggerParameter() {
		if (parameter == null) {
			return null;
		}
		final ITriggerParameter triggerParameter = trigger.createParameter();
		triggerParameter.set(parameter);
		return triggerParameter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((side == null) ? 0 : side.hashCode());
		result = prime * result + ((trigger == null) ? 0 : trigger.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trigger other = (Trigger) obj;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		if (side != other.side)
			return false;
		if (trigger == null) {
			if (other.trigger != null)
				return false;
		} else if (!trigger.equals(other.trigger))
			return false;
		return true;
	}

	public String getUniqueTag() {
		return trigger.getUniqueTag();
	}

	public ForgeDirection getSide() {
		return side;
	}
}

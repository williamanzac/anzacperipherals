package anzac.peripherals.tile;

import static anzac.peripherals.utility.ClassUtils.callPeripheralMethod;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;

import org.apache.commons.lang3.ArrayUtils;

import anzac.peripherals.Peripherals;
import anzac.peripherals.peripherals.LuaManager;
import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.utility.ClassUtils;
import anzac.peripherals.utility.LogHelper;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class BaseTileEntity extends TileEntity implements IPeripheral {
	private String[] methodNames;

	protected final Set<IComputerAccess> computers = new HashSet<IComputerAccess>();

	@Override
	public final String getType() {
		return ClassUtils.getType(getClass());
	}

	@Override
	public final String[] getMethodNames() {
		if (methodNames == null) {
			methodNames = ClassUtils.getMethodNames(getClass());
		}
		return methodNames;
	}

	@Override
	public final Object[] callMethod(final IComputerAccess computer, final ILuaContext context, final int method,
			final Object[] arguments) throws LuaException, InterruptedException {
		final String methodName = getMethodNames()[method];
		return callPeripheralMethod(this, context, methodName, arguments);
	}

	@Override
	public void attach(final IComputerAccess computer) {
		computers.add(computer);
		LuaManager.mount(computer);
		LogHelper.info("attaching; computer: " + computer + ", name: " + computer.getAttachmentName() + ", this: "
				+ this);
		Peripherals.peripheralMappings.put(computer.getID(), computer.getAttachmentName(), this);
	}

	@Override
	public void detach(final IComputerAccess computer) {
		computers.remove(computer);
		LuaManager.unmount(computer);
		Peripherals.peripheralMappings.remove(computer.getID(), computer.getAttachmentName());
	}

	public boolean isConnected() {
		return !computers.isEmpty();
	}

	@Override
	public boolean equals(final IPeripheral other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		return true;
	}

	protected void fireEvent(final PeripheralEvent event, final Object... values) {
		fireEvent(event.name(), values);
	}

	private void fireEvent(final String eventName, final Object... values) {
		for (final IComputerAccess computer : computers) {
			Object[] clone = ArrayUtils.clone(values);
			clone = ArrayUtils.add(clone, 0, computer.getAttachmentName());
			computer.queueEvent(eventName, clone);
		}
	}
}

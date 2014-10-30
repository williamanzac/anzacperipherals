package anzac.peripherals.peripherals;

import static dan200.computercraft.api.ComputerCraftAPI.createResourceMount;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import anzac.peripherals.Peripherals;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class LuaManager {

	private static final Map<IComputerAccess, Set<String>> computerMounts = new HashMap<IComputerAccess, Set<String>>();

	private static final Map<String, String> mounts = new HashMap<String, String>();

	static {
		mounts.put("rom/anzacperipherals", "lua/anzacperipherals");
		mounts.put("rom/autorun/anzacperipherals", "lua/autorun");
	}

	public static synchronized void mount(final IComputerAccess computer) {
		if (!computerMounts.containsKey(computer)) {
			final Set<String> set = new HashSet<String>();
			for (final Entry<String, String> entry : mounts.entrySet()) {
				final IMount resourceMount = createResourceMount(Peripherals.class, "anzacperipherals",
						entry.getValue());
				final String mount = computer.mount(entry.getKey(), resourceMount);
				if (mount != null) {
					set.add(mount);
				}
			}
			computerMounts.put(computer, set);
		}
	}

	public static void unmount(final IComputerAccess computer) {

	}
}

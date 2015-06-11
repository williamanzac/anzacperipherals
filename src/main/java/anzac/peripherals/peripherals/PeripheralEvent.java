package anzac.peripherals.peripherals;

public enum PeripheralEvent {
	/**
	 * This event is fired every time an item is successfully crafted.
	 * 
	 * @param name
	 *            The first argument is the name of the peripheral that fired the event.
	 * @param uuid
	 *            The uuid of the crafted item.
	 * @param count
	 *            The number of items crafted.
	 */
	crafted,
	/**
	 * This event is fired every time some fluid is inserted in to the internal tank.
	 * 
	 * @param name
	 *            The first argument is the name of the peripheral that fired the event.
	 * @param uuid
	 *            The uuid of the inserted fluid.
	 * @param count
	 *            The amount inserted.
	 */
	fluid_route,
	/**
	 * This event is fired every time some items are inserted in to the internal storage.
	 * 
	 * @param name
	 *            The first argument is the name of the peripheral that fired the event.
	 * @param uuid
	 *            The uuid of the inserted item.
	 * @param count
	 *            The number of items inserted.
	 */
	item_route,
	/**
	 * This event is fired every time a valid recipe is defined in the GUI.
	 * 
	 * @param name
	 *            The first argument is the name of the peripheral that fired the event.
	 * @param uuid
	 *            The uuid of the defined recipe. This can be used to see if it is a new recipe.
	 */
	recipe_changed,
	/**
	 * 
	 */
	redstone,
	/**
	 * 
	 */
	chat,
	/**
	 * 
	 */
	death;
}

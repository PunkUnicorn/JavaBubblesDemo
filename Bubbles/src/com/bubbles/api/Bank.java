package com.bubbles.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Template class to provide exclusive access to an array element in
 * pre-allocated memory. On a call to <i>T thing = get()</i> the instance gotten
 * is locked until a subsequent call to <i>forget(thing)</i>.
 * 
 * Because of the reference tagging involved calling <i>forget(thing)</i> is an
 * important annoying step. If you don't call <i>forget(thing)</i> for each
 * <i>get()</i> your Bank<T> will become like a hungry hippo but with your
 * memory until something crashes.
 * 
 * @param <T>
 *            The type to make a bank of, must implement IBankable
 */
public class Bank<T extends IBankable> {
	private ArrayList<ItemTag<T>> bank = null;
	private final Map<Integer, ItemTag<T>> map = new HashMap<Integer, ItemTag<T>>();
	private final Object bankLock = new Object();
	private int bankIndex = 0;
	private int size = 0;
	private IMakeT<T> maker;

	/**
	 * Constructs a new Bank instance
	 * 
	 * @param psize
	 *            Size required
	 * @param maker
	 *            Instance of a factory for the type
	 */
	public Bank(final int psize, final IMakeT<T /* must be IBankable */> maker) {
		this.maker = maker;
		// Eat exception on construction for clean property assignment code
		// declaration side.
		// The resize will trigger again later on a <i>get()</i> and throw up
		// again then.
		try {
			resize(psize);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Gets exclusive access to an instance from an array of previously
	 * allocated instances. Paired with annoying calls to <i>forget()</i>. This
	 * function allocates more memory if it can't find a free slot so not
	 * calling the annoying <i>forget()</i> will result in memory fragging
	 * 
	 * @return Locked instance of T, freed with corresponding calls to
	 *         <i>forget()</i>
	 * @throws Exception
	 */
	public T get() throws Exception {
		synchronized (bankLock) {
			for (int count = size; count > -1; count--) {
				@SuppressWarnings("unchecked")
				final ItemTag<T> itemTag = (ItemTag<T>) getInternal(this, bank.size());
				if (itemTag.getTagged().compareAndSet(false, true)) {
					map.put(((IBankable) (itemTag.getItem())).getId(), itemTag);
					return itemTag.getItem();
				}
			}

			resize(size * 2);
			return get();
		}
	}

	/**
	 * Eat memory and crash the VM if not supplementing calls to <i>get()</i>
	 * with exactly corresponding calls to <i>forget()</i>
	 * 
	 * @param newSize
	 *            New size to grow to. Must be greater than 0
	 * @throws Exception
	 *             Exception("IMakeT<T> not making instanceof IBankable")
	 * @throws Exception
	 *             Exception("Size is zero or less")
	 */
	private void resize(final int newSize) throws Exception {
		System.out.println("resize " + newSize);
		if (newSize <= 0)
			throw new Exception("Requested size is zero or less");
		T newOne = maker.Make();
		if (newOne instanceof IBankable == false)
			new Exception("IMakeT<T> not making instanceof IBankable");

		if (bank == null)
			bank = new ArrayList<ItemTag<T>>(newSize);
		else
			bank.ensureCapacity(newSize);

		Thread.yield();
		for (int i = size == 0 ? 0 : size - 1; i < newSize; i++) {
			if (newOne == null)
				newOne = maker.Make();
			final ItemTag<T> tag = new ItemTag<T>(newOne);
			newOne = null;
			bank.add(i, tag);
		}
		size = newSize;
	}

	/**
	 * Frees the item previously gotten with <i>get()</i>
	 * 
	 * @param forgetMe
	 *            Item previously acquired with <i>get()</i> to now discard
	 * @throws Exception
	 *             Exception("map returned null, item not found ")
	 */
	public void forget(final T forgetMe) throws Exception {
		synchronized (bankLock) {
			final ItemTag<T> found = map.get(((IBankable) forgetMe).getId());
			if (found == null)
				throw new Exception("map returned null, item not found ");
			found.getTagged().getAndSet(false);
		}
	}

	/**
	 * Gets the next item
	 * 
	 * @param bank
	 *            The Bank<T> instance
	 * @param size
	 *            Bank size
	 * @return
	 */
	private static Object getInternal(final Bank<?> bank, final int size) {
		synchronized (bank.bankLock) {
			int ourIndex = bank.bankIndex++;
			if (bank.bankIndex >= size) {
				ourIndex = bank.bankIndex = 0;
			}
			return bank.bank.get(ourIndex);
		}
	}
}
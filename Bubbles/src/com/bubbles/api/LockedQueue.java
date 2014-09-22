package com.bubbles.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The other types of thread safe concurrent queue's weren't doing it for me
 * 
 * @param <T>
 *            The type of things to Queue
 */
public class LockedQueue<T> implements Queue<T> {
	private final Object lock = new Object();
	private final List<T> list = new LinkedList<T>();

	@Override
	public boolean addAll(final Collection<? extends T> arg0) {
		synchronized (lock) {
			return list.addAll(arg0);
		}
	}

	@Override
	public void clear() {
		synchronized (lock) {
			list.clear();
		}
	}

	@Override
	public boolean contains(final Object arg0) {
		synchronized (lock) {
			return list.contains(arg0);
		}
	}

	@Override
	public boolean containsAll(final Collection<?> arg0) {
		synchronized (lock) {
			return list.containsAll(arg0);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (lock) {
			return list.isEmpty();
		}
	}

	@Override
	public Iterator<T> iterator() {
		synchronized (lock) {
			return list.iterator();
		}
	}

	@Override
	public boolean remove(final Object arg0) {
		synchronized (lock) {
			return list.remove(arg0);
		}
	}

	@Override
	public boolean removeAll(final Collection<?> arg0) {
		synchronized (lock) {
			return list.removeAll(arg0);
		}
	}

	@Override
	public boolean retainAll(final Collection<?> arg0) {
		synchronized (lock) {
			return list.retainAll(arg0);
		}
	}

	@Override
	public int size() {
		synchronized (lock) {
			return list.size();
		}
	}

	@Override
	public Object[] toArray() {
		synchronized (lock) {
			return list.toArray();
		}
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(final T[] arg0) {
		synchronized (lock) {
			return list.toArray(arg0);
		}
	}

	@Override
	public boolean add(final T arg0) {
		synchronized (lock) {
			return list.add(arg0);
		}
	}

	@Override
	public T element() {
		synchronized (lock) {
			return list.get(list.size() - 1);
		}
	}

	@Override
	public boolean offer(final T arg0) {
		synchronized (lock) {
			try {
				return list.add(arg0);
			} catch (final IndexOutOfBoundsException e) {
				return false;
			}
		}
	}

	@Override
	public T peek() {
		synchronized (lock) {
			return list.get(list.size() - 1);
		}
	}

	@Override
	public T poll() {
		synchronized (lock) {
			if (list.size() == 0)
				return null;
			final int index = list.size() - 1;
			final T retVal = list.get(index);
			list.remove(index);
			return retVal;
		}
	}

	@Override
	public T remove() {
		synchronized (lock) {
			final int index = list.size() - 1;
			final T retVal = list.get(index);
			list.remove(index);
			return retVal;
		}
	}
}

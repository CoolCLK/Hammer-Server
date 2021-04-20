package org.bukkit.craftbukkit.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LimitedSet<T> implements Set<T> {

  private final Set<T> wrappedSet;
  private final int maxCapacity;

  public LimitedSet(Set<T> wrappedSet, int maxCapacity) {
    this.wrappedSet = wrappedSet;
    this.maxCapacity = maxCapacity;
  }

  public LimitedSet(int maxCapacity) {
    this(new HashSet<>(), maxCapacity);
  }

  public boolean isMaxCapacityReached() {
    return getCapacityLeft() == 0;
  }

  public int getCapacityLeft() {
    return getMaxCapacity() - this.size();
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  @Override
  public int size() {
    return wrappedSet.size();
  }

  @Override
  public boolean isEmpty() {
    return wrappedSet.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return wrappedSet.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return wrappedSet.iterator();
  }

  @Override
  public Object[] toArray() {
    return wrappedSet.toArray();
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return wrappedSet.toArray(a);
  }

  @Override
  public boolean add(T t) {
    boolean wasAdded = wrappedSet.add(t);
    if (isMaxCapacityReached() && wasAdded) {
      throw new MaxCapacityReachedException("The maximum capacity of this set is already reached.");
    }
    return wasAdded;
  }

  @Override
  public boolean remove(Object o) {
    return wrappedSet.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return wrappedSet.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean changedContent = wrappedSet.addAll(c);
    if (!changedContent) {
      return false;
    }
    if (getCapacityLeft() < 0) {
      wrappedSet.removeAll(c);
      throw new MaxCapacityReachedException("Adding this collection would surpass the max capacity.");
    }
    return true;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return wrappedSet.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return wrappedSet.removeAll(c);
  }

  @Override
  public void clear() {
    wrappedSet.clear();
  }

  private static class MaxCapacityReachedException extends IllegalStateException {

    public MaxCapacityReachedException(String message) {
      super(message);
    }
  }

}

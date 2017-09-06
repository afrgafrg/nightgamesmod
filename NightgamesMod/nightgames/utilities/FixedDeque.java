package nightgames.utilities;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * An ArrayDeque extension that does NOT resize.
 */
public class FixedDeque<E> extends ArrayDeque<E> {
    private static final long serialVersionUID = -8519659265172182218L;
    private final int sizeLimit;

    public FixedDeque(int maxSize) {
        super(maxSize);
        sizeLimit = maxSize;
    }

    /**
     * Constructs a fixed-size deque containing the elements of the specified collection. The deque's size limit will be equal to the number of elements in the collection.
     * @param c The collection whose elements are to be placed in the deque
     */
    public FixedDeque(Collection<? extends E> c) {
        super(c);
        sizeLimit = c.size();
    }

    /**
     * Constructs a fixed-size deque of specified size containing the elements of the specified collection.
     * @param c The collection whose elements are to be placed in the deque
     * @param maxSize The maximum size of the deque.
     * @throws IllegalStateException when the specified size is too small for the initial collection.
     */
    public FixedDeque(Collection<? extends E> c, int maxSize) {
        super(c);
        if (c.size() <= maxSize) {
            sizeLimit = maxSize;
        } else {
            throw new IllegalStateException(
                            String.format("Tried to construct a deque of fixed-size %d with too many initial elements %d", maxSize, c.size()));
        }
    }

    private boolean checkLimit(int numElementsToAdd) {
        return (size() + numElementsToAdd <= sizeLimit);
    }

    private void enforceLimit(int numElementsToAdd) {
        if (!checkLimit(numElementsToAdd)) {
            throw new IllegalStateException(
                            String.format("Fixed-size deque does not have enough space for %d new elements.",
                                            numElementsToAdd));
        }
    }

    @Override public void addFirst(E e) {
        enforceLimit(1);
        super.addFirst(e);
    }

    @Override public void addLast(E e) {
        enforceLimit(1);
        super.addLast(e);
    }

    @Override public boolean offerFirst(E e) {
        return checkLimit(1) && super.offerFirst(e);
    }

    @Override public boolean offerLast(E e) {
        return checkLimit(1) && super.offerLast(e);
    }

    @Override public boolean add(E e) {
        enforceLimit(1);
        return super.add(e);
    }

    @Override public boolean offer(E e) {
        return checkLimit(1) && super.offer(e);
    }

    @Override public void push(E e) {
        enforceLimit(1);
        super.push(e);
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        enforceLimit(c.size());
        return super.addAll(c);
    }
}

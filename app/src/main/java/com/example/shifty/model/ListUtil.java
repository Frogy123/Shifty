package com.example.shifty.model;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for operations on sorted {@link List} collections.
 * <p>
 * This class provides static methods to insert or remove elements while keeping
 * the list sorted. All elements in the list must implement {@link Comparable}.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * List<Integer> list = new ArrayList<>();
 * ListUtil.insertSorted(list, 5);
 * ListUtil.insertSorted(list, 2);
 * ListUtil.insertSorted(list, 3);
 * // list is now [2, 3, 5]
 * ListUtil.removeSorted(list, 3);
 * // list is now [2, 5]
 * }
 * </pre>
 *
 * @author Eitan Navon
 * @see Collections#binarySearch(List, Object)
 * @see Comparable
 */
public class ListUtil {

    /**
     * Inserts the specified item into the sorted list at the correct position
     * to maintain sort order.
     *
     * <p>
     * This method assumes the list is already sorted according to the natural ordering of its elements.
     * The insertion is performed using {@link Collections#binarySearch(List, Object)} to determine the
     * correct index for the new element.
     * </p>
     *
     * @param <T>  the type of elements in the list, which must implement {@link Comparable}
     * @param list the sorted list into which the item should be inserted
     * @param item the item to insert into the list
     * @throws NullPointerException if the list or item is {@code null}
     * @throws ClassCastException   if the item is not comparable to the list elements
     * @see Collections#binarySearch(List, Object)
     * @see Comparable
     * @link List
     */
    public static <T extends Comparable<? super T>> void insertSorted(List<T> list, T item) {
        int index = Collections.binarySearch(list, item);
        if (index < 0) index = -index - 1;
        list.add(index, item);
    }

    /**
     * Removes the specified item from the sorted list, if it exists.
     *
     * <p>
     * The method searches for the item using {@link Collections#binarySearch(List, Object)}.
     * If the item is found, it is removed. If not found, the method does nothing.
     * </p>
     *
     * @param <T>  the type of elements in the list, which must implement {@link Comparable}
     * @param list the sorted list from which the item should be removed
     * @param item the item to remove from the list
     * @throws NullPointerException if the list or item is {@code null}
     * @throws ClassCastException   if the item is not comparable to the list elements
     * @see Collections#binarySearch(List, Object)
     * @see Comparable
     * @link List
     */
    public static <T extends Comparable<? super T>> void removeSorted(List<T> list, T item) {
        int index = Collections.binarySearch(list, item);
        if (index >= 0) {
            list.remove(index);
        }
    }

}

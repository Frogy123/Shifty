package com.example.shifty.model;

import java.util.Collections;
import java.util.List;

public class ListUtil {


    public static <T extends Comparable<? super T>> void insertSorted(List<T> list, T item) {
        int index = Collections.binarySearch(list, item);
        if (index < 0) index = -index - 1;
        list.add(index, item);
    }

    public static <T extends Comparable<? super T>> void removeSorted(List<T> list, T item) {
        int index = Collections.binarySearch(list, item);
        if (index >= 0) {
            list.remove(index);
        }
    }

}

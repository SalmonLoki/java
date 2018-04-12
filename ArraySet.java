package ru.ifmo.rain.khromova.arrayset;

import java.util.*;

// неизменяемое упорядоченное множество
public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private List<E> container;
    private Comparator<? super E> comparator;

    //Constructors
    public ArraySet() {
        this((Comparator<E>) null);
    }

    public ArraySet(Comparator<? super E> comparator) {
        container = Collections.emptyList();
        this.comparator = comparator;
    }

    public ArraySet(Collection<? extends E> container) {
        this(container, null);
    }

    public ArraySet(Collection<? extends E> container, Comparator<? super E> comparator) {
        this.comparator = comparator;
        TreeSet<E> tree = new TreeSet<>(comparator);
        tree.addAll(container);
        this.container = new ArrayList<>(tree);
    }

    public ArraySet(SortedSet<E> sortedSet) {
        container = new ArrayList<>(sortedSet);
        comparator = sortedSet.comparator();
    }

    private ArraySet(List<E> sortedSet, Comparator<? super E> comparator) {
        this.container = sortedSet;
        this.comparator = comparator;
    }

    //Set
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return binarySearch((E) o) >= 0;
    }

    //AbstractSet

    @Override
    public int size() {
        return container == null ? 0 : container.size();
    }

    //SortedSet

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }


    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return container.get(0);
    }

    @Override
    public E last() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return container.get(container.size() - 1);
    }

    //NavigableSet
    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInc, E toElement, boolean toInc) {
        if (fromElement == null || toElement == null) {
            throw new NullPointerException();
        }
        int fromI = findFromInd(fromElement, fromInc);
        int toI = findToInd(toElement, toInc) + 1;
        if (toI < fromI) {
            toI = fromI;
        }
        return new ArraySet<>(container.subList(fromI, toI), comparator);
    }

    //возвращает элементы с начала набора до указанного элемента
    @Override
    public NavigableSet<E> headSet(E toElement, boolean inc) {
        if (toElement == null) {
            throw new NullPointerException();
        }
        return new ArraySet<>(container.subList(0, findToInd(toElement, inc) + 1), comparator);
    }

    //элементы от указанного элемента до конца набора
    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        if (fromElement == null) {
            throw new NullPointerException();
        }
        return new ArraySet<>(container.subList(findFromInd(fromElement, inclusive), container.size()), comparator);
    }

    //меньший по отношению к зданному эл-ту
    @Override
    public E lower(E e) {
        int ind = findToInd(e, false);
        return ind >= 0 ? container.get(ind) : null;
    }

    //меньше или равный
    @Override
    public E floor(E e) {
        int ind = findToInd(e, true);
        return ind >= 0 ? container.get(ind) : null;
    }

    //равный или больше
    @Override
    public E ceiling(E e) {
        int ind = findFromInd(e, true);
        return ind < size() ? container.get(ind) : null;
    }

    //больший
    @Override
    public E higher(E e) {
        int ind = findFromInd(e, false);
        return ind < size() ? container.get(ind) : null;
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    //итератор для доступа ко всем элементам набора поочередно
    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(container).iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(container instanceof DescendingList ?
                ((DescendingList<E>) container).objects :
                new DescendingList<>(container), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }


    //индекс эл-та
    private int findFromInd(E element, boolean inclusive) {
        int ind = binarySearch(element);
        if (ind < 0) {
            ind = ~ind;
        } else if (!inclusive) {
            ++ind;
        }
        return ind;
    }

    private int findToInd(E element, boolean inclusive) {
        int ind = binarySearch(element);
        if (ind < 0) {
            ind = ~ind - 1;
        } else if (!inclusive) {
            --ind;
        }
        return ind;
    }

    private int binarySearch(E element) {
        return Collections.binarySearch(container, element, comparator);
    }

    private class DescendingList<T> extends AbstractList<T> implements RandomAccess {
        public List<T> objects;

        public DescendingList(List<T> objects) {
            this.objects = objects;
        }

        @Override
        public T get(int index) {
            return objects.get(objects.size() - index - 1);
        }


        @Override
        public int size() {
            return objects.size();
        }
    }


}

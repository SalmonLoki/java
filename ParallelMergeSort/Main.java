import java.util.*;
import java.util.concurrent.*;


public class Main implements AutoCloseable {

    private ExecutorService executor;
    private int countThreads;

    public Main(int countThreads) {
        executor = Executors.newFixedThreadPool(countThreads);
        this.countThreads = countThreads;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Testing nature order...");
        Main sortObject = new Main(20);
        Integer[] arr = new Integer[20];
        Random rnd = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rnd.nextInt();
        }
        List<Integer> res = sortObject.sortNature(Arrays.asList(arr));
        Integer prev = Integer.MIN_VALUE;
        for (Integer el : res) {
            System.out.println(el);
            if (el < prev) {
                System.out.println("ERROR");
            }
            prev = el;
        }
        System.out.println("Testing other order...");

        sortObject.close();
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    public <T extends Comparable<T>> List<T> sortNature(List<T> ts) throws ExecutionException, InterruptedException {
        sort(ts, Comparator.naturalOrder());
        return ts;
    }

    public <T extends Comparable<T>> List<T> sortOther(List<T> ts) throws ExecutionException, InterruptedException {
        HashComparator comparator = new HashComparator();
        sort(ts, comparator);
        return ts;
    }

    private <T extends Comparable<T>> List<T> sortUnsorted(List<T> a) {
        sortUnsorted(a, 0, a.size(), Comparator.naturalOrder());
        return a;
    }

    private <T> void sortUnsorted(List<T> a, int lo, int hi, Comparator<? super T> comparator) {
        if (hi <= lo + 1) // single element
            return;
        int mid = lo + (hi - lo) / 2;
        sortUnsorted(a, lo, mid, comparator);
        sortUnsorted(a, mid, hi, comparator);

        merge(a, lo, hi, mid, comparator);
    }

    private <T> void merge(List<T> a, int lo, int hi, int mid, Comparator<? super T> comparator) {
        List<T> buf = new ArrayList<>(a);

        int i = lo, j = mid;
        int r = lo;
        while (i < mid && j < hi) {

            if (comparator.compare(buf.get(i), buf.get(j)) <= 0) { //(buf.get(i) < buf.get(j))
                a.set(r, buf.get(i));
                i++;
                r++;
            } else {
                a.set(r, buf.get(j));
                j++;
                r++;
            }

            if (i == mid) {
                while (j < hi) {
                    a.set(r, buf.get(j));
                    j++;
                    r++;
                }
            }

            if (j == hi) {
                while (i < mid) {
                    a.set(r, buf.get(i));
                    i++;
                    r++;
                }
            }
        }
    }

    private <T> List<T> merge(List<T> objects, int partSize, Comparator<? super T> comparator) {
        int start = 0;
        int end1 = partSize;
        int end2 = 2 * partSize;
        while (end1 < objects.size()) {
            merge(objects, start, Math.min(end2, objects.size()), end1, comparator);
            end1 += partSize;
            end2 += partSize;
        }
        return objects;
    }

    //принимает кол-во потоков, список
    //возвращает отсортированный естественным п. список
    public <T> List<T> sort(List<T> objects, Comparator<? super T> comparator) throws InterruptedException, ExecutionException {
        //собственно сортировка списка без потоков

        int threadCountNew = Math.min(countThreads, objects.size());
        int chunkSize = (objects.size() / threadCountNew);

        List<Future> results = new ArrayList<>(threadCountNew);

        for (int i = 0; i < threadCountNew; i++) {
            int l = chunkSize * i;
            int r = Math.min(objects.size(), chunkSize * (i + 1));

            results.add(executor.submit(() -> sortUnsorted(objects, l, r, comparator)));
        }

        for (Future res : results) {
            res.get();
        }
        return merge(objects, chunkSize, comparator);
    }

    class HashComparator<T> implements Comparator<T> {
        public int compare(T a, T b) {
            return a.hashCode() - b.hashCode();
        }
    }


}

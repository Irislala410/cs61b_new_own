package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max() {
        if (this.size() == 0) {
            return null;
        } else {
            T max = get(0);
            for (int i = 0; i < size(); i++) {
                if (comparator.compare(get(i), max) > 0) {
                    max = get(i);
                }
            }
            return max;
        }
    }

    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        } else {
            T max = get(0);
            for (int i = 0; i < size(); i++) {
                if (c.compare(get(i), max) > 0) {
                    max = get(i);
                }
            }
            return max;
        }
    }

}

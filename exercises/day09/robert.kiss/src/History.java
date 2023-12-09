import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class History {
    private final long[] history;
    List<long[]> dists;

    public History(String line) {
        this.history = Arrays.asList(line.split(" ")).stream().mapToLong(Long::parseLong).toArray();
        this.dists = new ArrayList();
        long[] h = this.history;
        while (h.length > 1) {
            long[] hnext = new long[h.length-1];
            for (int i=0;i<hnext.length;i++) {
                hnext[i] = h[i+1]-h[i];
            }
            dists.add(hnext);
            h = hnext;
        }
    }

    public long findNext() {
        return this.history[this.history.length-1] + this.dists.stream().mapToLong(list->list[list.length-1]).sum();
    }
    public long findPrev() {
        long retval = this.history[0];
        long mult = -1;
        for (int i=0;i<this.dists.size();i++) {
            retval += mult * this.dists.get(i)[0];
            mult = mult * -1;
        }
        return retval;
    }

}

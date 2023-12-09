import java.util.ArrayList;
import java.util.List;

public class Main extends MySolutionBase {


    private List<History> histories = new ArrayList<>();

    public Main(String inputFilename) {
        super(inputFilename);
        for(String line:getInputLinesAsList()) {
            this.histories.add(new History(line));
        }
    }

    private Main play1() {
        long sumOfNexts = histories.stream().mapToLong(History::findNext).sum();
        System.out.println(sumOfNexts);
        return this;
    }

    private Main play2() {
        //histories.stream().forEach(h->System.out.println(h.findPrev()));
        long sumOfPrevs = histories.stream().mapToLong(History::findPrev).sum();
        System.out.println(sumOfPrevs);
        return this;
    }

    public static void main(String args[]) {
        try {
            new Main("sample.txt").play1().play2();
            new Main("input.txt").play1().play2();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

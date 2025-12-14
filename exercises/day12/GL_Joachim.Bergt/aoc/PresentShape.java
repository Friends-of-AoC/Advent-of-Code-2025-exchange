package aoc;

import java.util.ArrayList;
import java.util.List;

public record PresentShape(int size, List<Form> options) {
    static PresentShape from(Form present) {
        var iterations = new ArrayList<Form>();
        iterations.addAll(present.allIterations());
        return new PresentShape(present.filledSpaces(), iterations);
    }
}

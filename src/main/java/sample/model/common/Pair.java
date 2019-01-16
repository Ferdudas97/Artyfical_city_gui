package sample.model.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class Pair<F,S> {
    private F first;
    private S second;

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}

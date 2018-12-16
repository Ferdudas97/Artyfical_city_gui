package sample.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class Pair<F,S> {
    private F first;
    private S second;

}

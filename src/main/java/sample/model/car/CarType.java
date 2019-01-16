package sample.model.car;

import lombok.val;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum  CarType {
    BIG,MEDIUM,SMALL;

    private static  final Random random = new Random();
    private static final List<CarType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    public static CarType getRandomedType() {
        return VALUES.get(random());
    }


    private static int random() {
        val r = random.nextInt(100);
        if (r<5) return 0;
        if (r<98) return 1;
        if (r<100) return 2;

        return 0;
    }




}

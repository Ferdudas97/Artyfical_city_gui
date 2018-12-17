package sample.model.car;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


@Getter
public class CarHolder {
    public static final Map<CarId,Car> cars= new ConcurrentHashMap<>();

    public static void addCar(final Car car) {
        if (car!=null && car.getId()!= null) cars.put(car.getId(), car);
    }

    public static void removeCar(final Car car) {
        cars.remove(car.getId());
    }
    public static  synchronized Stream<Car> getAllCars(){
        return cars.values().stream();
    }
    public static void clear() {
        cars.clear();
    }
}

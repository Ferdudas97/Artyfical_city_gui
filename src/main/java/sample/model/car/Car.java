package sample.model.car;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import sample.model.node.Node;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "of")
@Getter
public class Car {
    private Double currentSpeed = 1.0;
    private Double maxSpeed;
    private Node head;
    private int size;
    private CarId id;
    private final Function<Double, Double> accelerationFunction;
    private static Integer idIterator = 0;

    public Car(final Node head, final int size, final Double maxSpeed) {
        this.id = CarId.of(idIterator.toString());
        idIterator++;
        this.accelerationFunction = speed -> 0.5;
        this.head = head;
        this.size = size;
        this.maxSpeed = maxSpeed;

    }

    public synchronized void move() {
        if (head != null) {
            val previousHead = head;
            System.out.println(" id = " + id.getId() + "x =" + head.getPosition().getHoriziontalPosition() + " y= " + head.getPosition().getVerticalPosition());
            int distance = currentSpeed.intValue();
            boolean shouldChange = isStripEndingSoon(head, distance);
            if (shouldChange) changeStrip();
            else {
                changeToTheFastestPossible();
            }
            currentSpeed = getMaximumPossibleSpeedOnStrip(head);
            if (currentSpeed!=0)removePresenceFromPreviousNodes(previousHead);
            moveNodes();
        } else {
            CarHolder.removeCar(this);
        }

    }

    private void moveNodes() {
        for (int i = 0; i < currentSpeed; i++) {
            if (head.getNeighbors().getRight() != null) head = head.getNeighbors().getRight();
        }
        setPresenceToNewNodes(head);

    }

    private void changeStrip() {
        Node newHead = getTheNodeWithTheLongestDistance();
        boolean isChanging = randomDecisionAboutChangingStrip(newHead);
        if (isChanging) head = newHead;
    }

    private void changeToTheFastestPossible() {
        Node nodeWithTheHigherPossibleSpeed = getStripWithMaximumPossibleSpeed();
        if (head != nodeWithTheHigherPossibleSpeed && randomDecisionAboutChangingStrip(nodeWithTheHigherPossibleSpeed))
            head = nodeWithTheHigherPossibleSpeed;

    }

    private void setPresenceToNewNodes(Node node) {
        for (int i = 0; i < size; i++) {
            if (node != null) {
                node.setCarId(id);
                node.setIsTaken(true);
                node = node.getNeighbors().getLeft();

            }
        }
    }

    private void removePresenceFromPreviousNodes(Node node) {
        for (int i = 0; i < size; i++) {
            if (node != null) {
                node.setIsTaken(false);
                node.setCarId(null);
                node = node.getNeighbors().getLeft();
            }
        }
    }

    private boolean isStripEndingSoon(Node node, int speed) {
        int distance = 0;
        while (speed - 1 > distance && node != null) {
            node = node.getNeighbors().getRight();
            distance++;
        }
        if (node == null || speed < distance) return true;
        return false;
    }

    private Node getTheNodeWithTheLongestDistance() {
        return Stream.of(head.getNeighbors().getTop(), head.getNeighbors().getBottom(), head)
                .collect(Collectors.toMap(Function.identity(), this::checkStripDistance,(a,b) ->a))
                .entrySet()
                .stream()
                .filter(e ->e.getValue()>0)
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();
    }


    private boolean randomDecisionAboutChangingStrip(Node node) {
        val random = new Random();
        val difference = getDifferenceBeetwenDistanceAndSpeedOfTheClosestCar(node);
        int probabilty;
        if (difference > 0) probabilty = 9;
        else probabilty = 2;
        return random.nextInt(10) <= probabilty;
    }

    private Double getDifferenceBeetwenDistanceAndSpeedOfTheClosestCar(Node node) {
        int distance = 0;
        while (node != null && !node.getIsTaken()) {
            node = node.getNeighbors().getLeft();
            distance++;
        }
        double carSpeed = Optional.ofNullable(node)
                .map(Node::getCarId)
                .map(CarHolder.cars::get)
                .map(Car::getCurrentSpeed)
                .orElse(0.0);

        return distance - carSpeed;

    }

    private Node getStripWithMaximumPossibleSpeed() {
        val headSpead = getMaximumPossibleSpeedOnStrip(head);
        return Stream.of(head.getNeighbors().getTop(), head.getNeighbors().getBottom(),head)
                .collect(Collectors.toMap(Function.identity(), this::getMaximumPossibleSpeedOnStrip, (a, b) -> a))
                .entrySet()
                .stream()
                .filter(e ->e.getValue()>0)
                .max(Comparator.comparing(Map.Entry::getValue))
                .filter(e -> e.getValue() > headSpead)
                .map(Map.Entry::getKey)
                .orElse(head);
    }

    private Double getMaximumPossibleSpeedOnStrip(Node node) {
            int dist = checkStripDistance(node);
            if (shouldBrake(dist)) return brakeSpeed(dist);
            else return acceleratedSpeed();
    }

    private boolean shouldBrake(final int distance) {
        return distance - acceleratedSpeed() < 0;
    }

    private int checkStripDistance(Node node) {
        int dist = 0;
        //lub         while (node != null && !node.getIsTaken()) {
        while ((node != null && !node.getIsTaken() && dist < maxSpeed) || node == head) {
            dist++;
            node = node.getNeighbors().getRight();
        }
        return dist;
    }

    private Double acceleratedSpeed() {
        return Math.min(maxSpeed, currentSpeed + accelerationFunction.apply(currentSpeed));
    }

    private Double brakeSpeed(int dist) {
        return Math.min(currentSpeed, dist - 3);
    }

}

package sample.model.car;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;
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

    public Car(final Node head, final int size, final Double maxSpeed) {
        this.id = CarId.of(UUID.randomUUID().toString());
        this.accelerationFunction = speed -> 0.5;
        this.head = head;
        this.size = size;
        this.maxSpeed = maxSpeed;

    }

    public synchronized void move() {
        if (head != null) {
            val previousHead = head;
            int distance = currentSpeed.intValue();
            boolean shouldChange = isStripEndingSoon(head, distance);
            if (shouldChange) changeStrip();
            else {
                changeToTheFastestPossible();
            }
            currentSpeed = getMaximumPossibleSpeedOnStrip(head);
            removePresenceFromPreviousNodes(previousHead);
            if (isEndOfMap(head)) CarHolder.removeCar(this);
            else {
                moveNodes();
            }
        }

    }

    private  synchronized void moveNodes() {
        for (int i = 0; i < currentSpeed.intValue(); i++) {
            if (head.getNeighbors().getRight() != null && !head.getNeighbors().getRight().getIsTaken()) head = head.getNeighbors().getRight();
        }
        setPresenceToNewNodes(head);

    }


    private synchronized void changeStrip() {
        Node newHead = getTheNodeWithTheLongestDistance();
        boolean isChanging = randomDecisionAboutChangingStrip(newHead);
        if (isChanging && newHead!=null && checkIfIsEnoughSpace(newHead)) head = newHead;
    }

    private synchronized void changeToTheFastestPossible() {
        Node nodeWithTheHigherPossibleSpeed = getStripWithMaximumPossibleSpeed();
        if (nodeWithTheHigherPossibleSpeed != null && checkIfIsEnoughSpace(nodeWithTheHigherPossibleSpeed) &&
                head != nodeWithTheHigherPossibleSpeed && randomDecisionAboutChangingStrip(nodeWithTheHigherPossibleSpeed))
            head = nodeWithTheHigherPossibleSpeed;

    }

    private synchronized void setPresenceToNewNodes(Node node) {
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
                node.setCarId(null);
                node.setIsTaken(false);
                node = node.getNeighbors().getLeft();

            }
        }
    }

    private boolean isStripEndingSoon(Node node, int speed) {
        int distance = 0;
        if (speed==0) return true;
        while (speed - 1 > distance && node != null) {
            node = node.getNeighbors().getRight();
            distance++;
        }
        if (node == null || speed < distance) return true;
        return false;
    }

    private Node getTheNodeWithTheLongestDistance() {
        return Stream.of(head.getNeighbors().getTop(), head.getNeighbors().getBottom())
                .collect(Collectors.toMap(Function.identity(), this::checkStripDistance,(a,b) ->a))
                .entrySet()
                .stream()
                .filter(e ->e.getValue()>0)
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

    }


    private boolean randomDecisionAboutChangingStrip(Node node) {
        val random = new Random();
        val difference = getDifferenceBeetwenDistanceAndSpeedOfTheClosestCar(node);
        int probabilty;
        if (difference > 0) probabilty = 8;
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
                .orElse(-1.0);

        return distance - carSpeed;

    }

    private Node getStripWithMaximumPossibleSpeed() {
        val headSpead = getMaximumPossibleSpeedOnStrip(head);
        val max = Stream.of(head.getNeighbors().getTop(), head.getNeighbors().getBottom())
                .collect(Collectors.toMap(Function.identity(), this::getMaximumPossibleSpeedOnStrip, (a, b) -> a))
                .entrySet()
                .stream()
                .filter(e ->e.getValue()>0)
                .max(Comparator.comparing(Map.Entry::getValue))
                .filter(e -> e.getValue() > headSpead)
                .map(Map.Entry::getKey);
        if (max.isPresent()) return max.get();
        if (headSpead>0) return head;
        else return null;
    }

    private Double getMaximumPossibleSpeedOnStrip(Node node) {
        if(node !=null) {
            int dist = checkStripDistance(node);
            if (shouldBrake(dist)) return brakeSpeed(dist);
            if (dist!=0) return acceleratedSpeed();
            else return 0.0;
        }
        else return 0.0;
    }

    private boolean shouldBrake(final int distance) {
        return distance - acceleratedSpeed() < 0;
    }

    private int checkStripDistance(Node node) {
        int dist = 0;
        //lub         while (node != null && !node.getIsTaken()) {
        while ((node != null && !node.getIsTaken()) || node == head) {
            dist++;
            node = node.getNeighbors().getRight();
        }
        return dist;
    }

    private boolean checkIfIsEnoughSpace(Node node) {
        for(int i = 0; i <size+1 ; i++) {
            if (node!= null) {
                if (node.getIsTaken()) return false;
                node = node.getNeighbors().getLeft();
            }
            else return false;
        }
        return true;

    }

    private Double acceleratedSpeed() {

        return Stream.of(maxSpeed, currentSpeed + accelerationFunction.apply(currentSpeed),maxSpeedAHead(head))
                .min(Double::compareTo)
                .get();
    }

    private Double maxSpeedAHead( Node node) {
        int it = 0;
        Set<Double> speed = new HashSet<>();
        while (node!= null && it<=currentSpeed) {
            speed.add(node.getMaxSpeedAllowed());
            node = node.getNeighbors().getRight();
            it++;
        }
        return speed.stream().min(Comparator.naturalOrder()).orElse(0.0);
    }


    private Double brakeSpeed(int dist) {
        return Math.max(Math.min(currentSpeed, dist - 1),0);
    }

    private boolean isEndOfMap(Node node) {
        var bottomNode = node.getNeighbors().getBottom();
        while (node!= null) {
            if (node.getNeighbors().getRight() != null) return false;
            node = node.getNeighbors().getTop();
        }
        while (bottomNode!= null) {
            if (bottomNode.getNeighbors().getRight() != null) return false;
            bottomNode = bottomNode.getNeighbors().getBottom();
        }
        return true;
    }

}

package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lombok.val;
public class Controller {


    public Canvas simulationMap;
    public AnchorPane anchor;
    private final int nodeSize = 25;
    public ToggleGroup NodeType;

    public void initialize(){
    }

    public void drawNode(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getX());
    }

    public void draw(MouseEvent mouseEvent) {
        simulationMap.setWidth(anchor.getWidth());
        simulationMap.setHeight(anchor.getHeight());

        GraphicsContext gc = simulationMap.getGraphicsContext2D();
        val width= simulationMap.getWidth();
        val height = simulationMap.getHeight();
        drawGrass(gc,width, height);
        drawGrid(gc,width,height);
    }


    private void drawGrass(final GraphicsContext gc, final double width, final double height) {
        gc.setStroke(Color.BLACK);
        val grass = new Image("sample/img/grass.png",nodeSize,nodeSize,true,true);
        for(int i = 0 ; i < height/nodeSize ; i++) {
            for (int j = 0; j < width / nodeSize; j++) {

                gc.drawImage(grass, j* nodeSize, i * nodeSize);
            }
        }

    }
    private void drawGrid(final GraphicsContext gc, double width, double height){
        gc.setStroke(Color.BLUE);
        for(int i = 0 ; i < width ; i+=nodeSize){
            gc.strokeLine(i, 0, i, height - (height%nodeSize));
        }

        // horizontal lines
        gc.setStroke(Color.RED);
        for(int i = 0 ; i < height ; i+=nodeSize){
            gc.strokeLine(0, i, width, i);
        }
    }
}

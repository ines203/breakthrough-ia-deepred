package deepRed.model;

import deepRed.model.Point;

public class Move {
    private Point source;
    private Point target;

    public Move() {
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public Point getTarget() {
        return target;
    }

    public void setTarget(Point target) {
        this.target = target;
    }
}
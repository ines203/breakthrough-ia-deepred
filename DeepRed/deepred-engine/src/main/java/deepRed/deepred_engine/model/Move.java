package deepRed.deepred_engine.model;

import deepRed.deepred_engine.model.Point;

public class Move {
    private Point source;
    private Point target;

    public Move() {
    }

    public Move(Point source, Point target)
    {
        this.source = source;
        this.target = target;
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
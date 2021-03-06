package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Player_ball extends Circle {
    private double velocity;
    private boolean isFrozen = true;
    private final double jumpval= -650;
    private double max_height = 0;
    Player_ball(){
        super(17, Color.web("0xFF0082",1.0));
        velocity = 0;

    }
    public double getMax_height(){
        return max_height;
    }
    public void freeze(){
        this.isFrozen = true;
    }
    public void unfreeze(){
        this.isFrozen = false;
    }
    public void addVelocity(double v){
        if(!isFrozen)velocity+=v;
    }
    public double getVelocity(){return velocity;}
    public void setVelocity(double v){
        this.velocity = v;
    }
    public void update(double ti){
        if(!this.isFrozen){
            this.setLayoutY(this.getLayoutY()+velocity*ti);
            if(this.getLayoutY()<max_height)max_height = this.getLayoutY();
        }
    }

    public void jump(){

        this.setVelocity(jumpval);
        Sound.play_sound("jump");
    }
    public int get_code(){
        if(this.getFill().equals(Color.web("0xFF0082"))){
          return 0;
        }
        if(this.getFill().equals(Color.web("0x8D13FA"))){
            return 1;
        }
        if(this.getFill().equals(Color.web("0x35E2F2"))){
            return 2;
        }
        if(this.getFill().equals(Color.web("0xF5DF0D"))){
            return 3;
        }
        return -1;
    }
    public static Color get_code(int val){
        return switch (val) {
            case 0 -> Color.web("0xFF0082");
            case 1 -> Color.web("0x8D13FA");
            case 2 -> Color.web("0x35E2F2");
            case 3 -> Color.web("0xF5DF0D");
            default -> null;
        };
    }

}

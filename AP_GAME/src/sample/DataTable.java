package sample;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class DataTable implements Serializable {
    private static final long serialVersionUID=23L;
    private static int id = 0;
    private int gameId;
    private ArrayList<Integer> obsid;
    private ArrayList<Boolean> star;
    private ArrayList<Boolean> colorSwitcher;
    private ArrayList<Boolean> cleared;
    private ArrayList<Float> difficulty;
    private ArrayList<Double> timeElapsed;
    private ArrayList<Double> Ycoords;
    private ArrayList<Double> Ycoordstrans;
    private String date;
    private String time;
    private int ball_color_code;
    private double YcoordBall;
    private int stars_collected;
    private float difficulty_val;
    private double next_obs;
    private double art_pos;
    private double max_dist_val;
    public double get_art_pos(){
        return art_pos;
    }
    public int getStars_collected() {
        return stars_collected;
    }
    public int get_color_code(){
        return ball_color_code;
    }
    public double getYcoordBall(){
        return YcoordBall;
    }
    public String getDate() {
        return date;
    }
    public double getMsx(){
        return max_dist_val;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<Integer> getObsid() {
        return obsid;
    }

    public ArrayList<Boolean> getStar() {
        return star;
    }
    public ArrayList<Boolean> getCleared() {
        return cleared;
    }
    public ArrayList<Boolean> getColorSwitcher() {
        return colorSwitcher;
    }

    public ArrayList<Float> getDifficulty() {
        return difficulty;
    }

    public ArrayList<Double> getTimeElapsed() {
        return this.timeElapsed;
    }
    public ArrayList<Double> getYcoords(){
        return Ycoords;
    }
    public ArrayList<Double> getYcoordstrans(){
        return Ycoordstrans;
    }
    public float getDifficulty_val(){
        return difficulty_val;
    }
    public DataTable(){
        LocalDate obj1 = LocalDate.now();
        LocalTime obj2 = LocalTime.now();
        DateTimeFormatter FormatObj1 = DateTimeFormatter.ofPattern("dd-MMM-yy");
        DateTimeFormatter FormatObj2 = DateTimeFormatter.ofPattern("hh:mm a");
        date = obj1.format(FormatObj1);
        time = obj2.format(FormatObj2);
        id++;
        this.gameId=id;
        this.obsid = new ArrayList<>();
        this.star = new ArrayList<>();
        this.colorSwitcher = new ArrayList<>();
        this.difficulty = new ArrayList<>();
        this.timeElapsed = new ArrayList<>();
        this.cleared = new ArrayList<>();
        this.Ycoords = new ArrayList<>();
        this.Ycoordstrans = new ArrayList<>();

    }

    public void update(LinkedList<Obstacle> obs, Score_board score, Player_ball pb,float difficulty_val,double t,double pos_art) {
        clear_data();

        for(int i=0;i<obs.size();i++){
            obsid.add(obs.get(i).getId());
            star.add(obs.get(i).isHasStar());
            colorSwitcher.add(obs.get(i).isHasswitch());
            difficulty.add(obs.get(i).getDifficulity_float());
            timeElapsed.add(t-obs.get(i).getTime_of_creation());
            cleared.add(obs.get(i).isCleared());
            Ycoords.add(obs.get(i).complete_group.getLayoutY());
            Ycoordstrans.add(obs.get(i).complete_group.getTranslateY());

            }


        this.stars_collected = score.getScore();
        this.difficulty_val = difficulty_val;
        this.ball_color_code = pb.get_code();
        this.YcoordBall = pb.getLayoutY();
        this.art_pos = pos_art;
        //below saving player data;

    }
    private void clear_data(){
        obsid.clear();
        star.clear();
        colorSwitcher.clear();
        difficulty.clear();
        timeElapsed.clear();
        cleared.clear();
    }
    public void saveGame(){
        game_launcher.getDatabase().getDatabaseFiles().add(this);
        try{
            game_launcher.serialize();
        }
        catch (IOException e){
            System.out.println("Cant close stream");
        }
    }
}

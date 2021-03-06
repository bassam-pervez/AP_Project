package sample;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


public class game {
    private static game instance;
    public game(){
        instance = this;
    }
    public static game getInstance() {
        return instance;
    }
    LinkedList<Obstacle> obs;
    Stage theStage;
    private Scene theScene;
    private ArrayList<String> input;
    private Score_board score_board;
    private Group gp;
    private LinkedList<Screen_art> immobile_gui = new LinkedList<Screen_art>();
    private  Pause pause_button;
    private LinkedList<ColorSwitcher> colorSwitchers = new LinkedList<ColorSwitcher>();
    private LinkedList<Screen_art> mobile_gui = new LinkedList<Screen_art>();
    private Start_art st_art;
    private VBox ObstaclePanel;
    private Player_ball pb;
    private double obs_vel = 0;
    private float difficulty_increment = 0.025f;
    private float difficulty  = 1f;
    private DataTable data;
    double elapsedTime;
    private double pause_time;
    private boolean wasLoaded = false;

    public DataTable getData() {
        return data;
    }

    public boolean isWasLoaded() {
        return wasLoaded;
    }

    public void setWasLoaded(boolean wasLoaded) {
        this.wasLoaded = wasLoaded;
    }

    public Score_board getScore_board() {
        return score_board;
    }

    public void start_game(Stage theStage){
        this.theStage = theStage;
        init_gui(theStage);
        gameloop();
    }
    public void load_a_game(Stage theStage,DataTable d){
        System.out.println("loading a game");
        this.theStage = theStage;
        setWasLoaded(true);
        data = d;

        init_gui(theStage);
        resume_initialization();
        loadGame();
        gameloop();

    }
    private void gameloop(){
        input = new ArrayList<String>();

        new AnimationTimer()
        {   long lastNanoTime = System.nanoTime();
            public void handle(long currentNanoTime)
            {
                // calculate time since last update.
                elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;
                lastNanoTime = currentNanoTime;

                //Rotation
                animate_obs(elapsedTime);
                while (at_60percent()){
                    simulate_climb();
                }
                if(pb.getLayoutY()>=st_art.getPositionY()-50){
                    pb.setVelocity(0);
                    pb.setLayoutY(st_art.getPositionY()-50);
                } else{
                    // acceleration below
                    pb.addVelocity(3000*elapsedTime);
                }


                if (input.contains("SPACE")){
                    pb.unfreeze();
                    pb.jump();
                }

                update_obs();
                update_and_refresh(elapsedTime);
                if(check_collisions()){
                    try {
                        Sound.play_sound("dead");
                        exit_menu();
                        this.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(at_0percent(pb)){
                    try {

                        Sound.play_sound("dead");
                        exit_menu();
                        pb.freeze();
                        pb.setTranslateY(pb.getTranslateY()-50);
                        this.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(pause_button.isClicked() ){
                    pause_button.Clickcheckdone();
                    try {
                        pause_menu();
                        this.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
//        System.out.println("testend");
        System.out.println("test5");
        theStage.show();
    }
//    private void set_resume_point(){
//        obs.
//    }
    private void pause_menu() throws IOException {
        Date date = new Date();
        pause_time = date.getTime();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PauseMenu.fxml"));
        Parent root = (Parent)loader.load();
        PauseMenu controller = (PauseMenu) loader.getController();
        controller.setStage(theStage);
        Scene s = new Scene(root, 512, 800);
        theStage.setTitle("Pause Game");
        theStage.setScene(s);
        theStage.show();


    }
    public void resume_game(){
        this.theStage.setScene(theScene);
        pb.freeze();
        gameloop();


    }
    private void exit_menu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ExitMenu.fxml"));
        Parent root = (Parent)loader.load();
        ExitMenu controller = (ExitMenu) loader.getController();
        controller.setStage(theStage);
        Scene s = new Scene(root, 512, 800);
        theStage.setTitle("Exit Menu");
        theStage.setScene(s);
        theStage.show();
    }

    private void animate_obs(double elapsed_time){
        for(Obstacle ob: obs ){
            ob.motion(elapsed_time);
        }
    }
    private boolean check_collisions(){
        for(Obstacle ob : obs){
            ob.check_cleared(pb);
            if(ob.check_collision(pb)){
                translate_to_safe(ob);
                return true;
            }
             if(ob.check_collectible_collision(pb,theScene,score_board)){
                 difficulty+=difficulty_increment;
             }
        }

        return false;
    }
    private void translate_to_safe(Obstacle ob){
       if(!ob.isCleared()){
           ob.check_bound_collision(pb,1);
       }
       else {
           ob.check_bound_collision(pb,-1);
       }
       if(at_0percent(pb)){
           pb.setTranslateY(pb.getTranslateY()-20);
       }
       shift_frame_to_60();
    }
    private void shift_frame_to_60(){
        double diff  = get_diff_till_60();
        for(Node x: ObstaclePanel.getChildren()){
            x.setTranslateY(x.getTranslateY()+diff);
        }

        pb.setTranslateY(pb.getTranslateY()+diff);
        for(Screen_art sc: mobile_gui){
            sc.translateY(diff);
            sc.render();
        }
    }

    private void update_obs(){
        Obstacle ob; ob = obs.getFirst();
        if(at_neg100percent_obs(ob)){
            obs.remove(ob);
            ObstaclePanel.getChildren().remove(ob);
        }
        while(obs.size()<7){
            addobs();
        }
    }
    private void update_and_refresh(double elapsedTime){
        input.clear();
        pb.update(elapsedTime);
        score_board.render();
    }

    private boolean at_neg100percent_obs(Obstacle ob){

        return (ob.complete_group.localToScene(ob.complete_group.getBoundsInLocal()).getMinY()>theScene.getHeight()*(2));
    }
    private boolean at_0percent(Player_ball pb){

        return (pb.getBoundsInParent().getMaxY()>theScene.getHeight());
    }
    private boolean at_60percent(){
        return (pb.getBoundsInParent().getMaxY()<theScene.getHeight()/2-60);
    }
    private double get_diff_till_60(){
        return (-pb.getBoundsInParent().getMaxY()+theScene.getHeight()/2-60);
    }
    private void init_gui(Stage theStage){
        theStage.setTitle( "Color Switch" );
        gp = new Group();
        st_art = new Start_art();
        score_board = new Score_board();
        pause_button = new Pause();
        this.mobile_gui.add(st_art);
        this.immobile_gui.add(pause_button);
        ObstaclePanel = new VBox();
        ObstaclePanel.setSpacing(0);
        obs= new LinkedList<Obstacle>();
        pb = new Player_ball();
        pb.setLayoutX(256);
        pb.setLayoutY(500);
        ObstaclePanel.setMinSize(1000, 800);
        gp.getChildren().addAll(ObstaclePanel,pb);

        pause_button.addto(gp);
        score_board.addto(gp);
        st_art.addto(gp);
        addobs();
        if (obs.getFirst().hascs()) {
            obs.getFirst().apply_cs(pb, score_board);
        }
        addobs();
        for (Node nm: ObstaclePanel.getChildren()){
//            nm.setLayoutX(256);
            nm.setLayoutY(-1200);
        }
        theScene =new Scene(gp,512,800,Color.web("242520"));
        theStage.setScene(theScene);
        theScene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        input.add( code );
                    }
                });

        for(Screen_art sc : immobile_gui) sc.render();
        for(Screen_art sc: mobile_gui) sc.render();
        System.out.println("test4");
    }

    private void addobs(){
        Random random = new Random();
        ObstacleFactory o = new ObstacleFactory();
        Obstacle ob = o.getObstacle(random.nextInt(9),difficulty);
        obs.add(ob);
        addtoVbox(ob.complete_group);


    }
    private void addtoVbox(Node addition){
        double max = 0;
        for(Node x: ObstaclePanel.getChildren()){
            x.setTranslateY(x.getTranslateY()-addition.getBoundsInLocal().getHeight()-ObstaclePanel.getSpacing());
            max = x.getTranslateY();
        }
        ObstaclePanel.getChildren().add(0,addition);
        addition.setTranslateY(max);
    }

    private void simulate_climb(){
        for(Node x: ObstaclePanel.getChildren()){
            x.setTranslateY(x.getTranslateY()+2);
        }

        pb.setTranslateY(pb.getTranslateY()+2);
        for(Screen_art sc: mobile_gui){
            sc.translateY(2);
            sc.render();
        }
    }

    public DataTable updateData() {
        data = new DataTable();
        data.update( obs, getScore_board(), pb,difficulty,pause_time,st_art.gc.getCanvas().getLayoutY());
        game_launcher.getDatabase().addData(data);
        return data;
    }

    public void loadGame() {
        System.out.println("test");
        System.out.println("updating obstacles");
        ArrayList<Integer> o = data.getObsid();
       int  size  = o.size()-1;
        ObstacleFactory factory = new ObstacleFactory();
        for(int i=0;i<=size;i++) {
            float diff = data.getDifficulty().get(i);
            Obstacle ob = factory.getObstacle(o.get(i),diff);
            ob.setHasStar(data.getStar().get(i));
            ob.setHasswitch(data.getColorSwitcher().get(i));
            //setting obstacle properties;
            if(!data.getStar().get(i)){
                ob.star.setDisabled();
                ob.setHasStar(false);
            }
            if(data.getColorSwitcher().get(i)){
                if(!ob.hascs()){
                    ob.add_color_switcher();
                    ob.setHasswitch(true );
                }
            }
            else{
                if(ob.hascs()){
                    ob.cs.setDisabled();
                    ob.setHasswitch(false);
                }
            }
            System.out.println(data.getCleared());
            ob.set_cleared(data.getCleared().get(i));

            ob.motion(data.getTimeElapsed().get(i));
            addtoVbox(ob.complete_group);
//            ob.complete_group.setTranslateY(data.getYcoordstrans().get(i));
            ob.complete_group.setLayoutY((data.getYcoords().get(i)));
            obs.add(ob);
        }
        pb.setFill(Player_ball.get_code(data.get_color_code()));
        pb.setLayoutY(data.getYcoordBall());
        st_art.gc.getCanvas().setLayoutY(data.get_art_pos());

//             setting game and scoreboard properties;
            difficulty = data.getDifficulty_val();
//            //setting playerball properties;
        System.out.println("code " + data.get_color_code());

//            for(Obstacle ob : obs){
//            ob.complete_group.setTranslateY(ob.complete_group.getTranslateY()-800);
//            }

//            check_collisions();
//            shiftby(getTranslateValNeeded(getFirstunClearedObs()));
            score_board.setScore(data.getStars_collected());
//            System.out.println("test2");
    }
    private void shiftby(double val){
        for(Obstacle ob : obs){
           // ob.complete_group.setTranslateY(ob.complete_group.getTranslateY() - val);
        }

    }
    private double getTranslateValNeeded(Obstacle ob){
        return pb.getLayoutY()-ob.complete_group.getBoundsInParent().getMaxY();

    }
    private Obstacle getFirstunClearedObs(){
        for(Obstacle o : obs){
            if (!o.isCleared())return o;
        }
        return null;
    }
    private void resume_initialization(){
        theStage.setTitle( "Color Switch" );
        gp = new Group();
        st_art = new Start_art();
        score_board = new Score_board();
        pause_button = new Pause();
        this.mobile_gui.add(st_art);
        this.immobile_gui.add(pause_button);
        ObstaclePanel = new VBox();
        ObstaclePanel.setMinSize(1000,800);
        ObstaclePanel.setSpacing(0);
        obs= new LinkedList<Obstacle>();
        pb = new Player_ball();
        pb.setLayoutX(256);
        pb.setLayoutY(500);
        gp.getChildren().addAll(ObstaclePanel,pb);
        pause_button.addto(gp);
        score_board.addto(gp);
        st_art.addto(gp);

        theScene =new Scene(gp,512,800,Color.web("242520"));
        theStage.setScene(theScene);
        theScene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        input.add( code );
                    }
                });
        for(Screen_art sc : immobile_gui) sc.render();
        for(Screen_art sc: mobile_gui) sc.render();
        System.out.println("test4");
    }

}
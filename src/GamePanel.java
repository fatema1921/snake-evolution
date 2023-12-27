import javax.swing.*;
import java.awt.*;


import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;


public class GamePanel extends JPanel implements KeyListener {
    public static final int FPS = 60;
    public static final int CELL_COUNT = 40;
    public static final int CELL_SIZE = GameFrame.WINDOW_SIZE.x / CELL_COUNT;


    //changed from final
    public static int FPS = 60;
    private BgPanel bg;
    private Snake snake;
    private Food food, bonusFood;
    private Obstacle obstacle;

    private Random rand;
    private BgPanel bg;
    private Snake snake;
    private Food food;
    private ObstacleList obstacles;


    private int score;
    private final Timer gameLoop;
    private long startTime;
    private boolean fastMode, slowMode;
    private StateChangeListener stateChanger;


    public GamePanel(StateChangeListener listener) {
        super();
        this.setPreferredSize(new Dimension(GameFrame.WINDOW_SIZE.x, GameFrame.WINDOW_SIZE.y));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        rand = new Random();
        bg = new BgPanel();
        snake = new Snake();

        stateChanger = listener;
        food = new Food();

        bonusFood = new Food();
        obstacle = new Obstacle(snake.getBody());

        obstacles = new ObstacleList();

        score = 0;
        startTime = 0;
        fastMode = false;
        slowMode = false;

        int delay = (int) (1000 / (FPS * Snake.SPEED));
        gameLoop = new Timer(delay, e -> { // GAME LOOP, runs every 1/60*SPEED -th of a second

            //1000/(int)(FPS * Snake.SPEED)
            update();
            repaint(); // calls paintComponent()

        });
    }

    public void update() {

        // update positions, etc
        if (fastMode || slowMode) {
            if (System.currentTimeMillis() - startTime > 5000) {
                fastMode = false;
                slowMode = false;
                adjustSnakeSpeed(1); // Set the speed back to normal
            }
        }

        snake.move();

        snake.move(); // add a new "head" based on the movement direction


        // snake collisions (self and borders)
        if (snake.doCollisions()) {
            stopGame();
            return;
        }

        // collision with obstacles
        if (snake.checkCollisionWith(obstacles.getAllCells())) {
            stopGame();
            return;
        }

        // collision with food
        if (snake.checkCollisionWith(food.getFoodLocation())) {
            score++;


        if (snake.checkCollisionWith(bonusFood.getBonusFoodLocation())) {
            switch (bonusFood.getBonusFoodType()) {
                case SPEEDFOOD -> {
                    fastMode = true;
                    adjustSnakeSpeed(2);
                    startTime = System.currentTimeMillis();
                }
                case SLOWFOOD -> {
                    slowMode = true;
                    adjustSnakeSpeed(0.5);
                    startTime = System.currentTimeMillis();
                }
                case PLUSFOOD -> {
                    score += 2;
                }
                case MINUSFOOD -> {
                    score -= 2;
                    if (score <0){
                        score = 0;
                    }
                }
            }
            bonusFood.setBonusFoodType();
            bonusFood.respawnBonusFood();
        }

        if (snake.checkCollisionWith(obstacle.getCells())) {
            stateChanger.changeState(GameState.GAME_OVER);
            gameLoop.stop();

            // spawn new food in a valid position
            CellPosition newFoodPos;
            do {
                food.respawn(); // respawn food until its in a valid position
                newFoodPos = food.getFoodLocation();
            } while (snake.checkCollisionWith(newFoodPos) || obstacles.getAllCells().contains(newFoodPos));

            // spawn new obstacle every 5th time food is eaten
            if (score % 5 == 0) {
                obstacles.add(new Obstacle(snake.getBody()));
            }
        }
        else {
            snake.getBody().remove(snake.getBody().size() - 1); // remove the tail to complete movement

        }
    }

    private void adjustSnakeSpeed(double speedMultiplier) {
        int delay = (int) (1000 / (FPS * Snake.SPEED * speedMultiplier));
        gameLoop.setDelay(delay);
    }

    public int getScore () {
        return this.score;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        bg.paintComponent(g); // draw background first

        Graphics2D frame = (Graphics2D) g; // frame for drawing 2d graphics

        food.draw(frame);
        for (Obstacle obstacle : obstacles.getObstacles())
            obstacle.draw(frame);

        bonusFood.drawBonusFood(frame);
    // timer


        g.setColor(Color.black);
        g.drawString("Score: "+ score, 65 , GameFrame.WINDOW_SIZE.y - 770);

        snake.draw(frame);
        frame.dispose();
    }

    // starts the game loop
    public void startGame() {
        gameLoop.start();
    }

    public void stopGame() {
        gameLoop.stop();

        Players tempPlayer = new Players("", score);

        if (score > 0 && Leaderboard.isTopTen(tempPlayer)) {
            stateChanger.changeState(GameState.GAME_OVER_ENTERNAME);
        }
        else {
            stateChanger.changeState(GameState.GAME_OVER);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_UP) {
            snake.updateDirection(Direction.UP);
        }
        if (code == KeyEvent.VK_DOWN) {
            snake.updateDirection(Direction.DOWN);
        }
        if (code == KeyEvent.VK_LEFT) {
            snake.updateDirection(Direction.LEFT);
        }
        if (code == KeyEvent.VK_RIGHT) {
            snake.updateDirection(Direction.RIGHT);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

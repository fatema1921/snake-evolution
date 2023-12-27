package panels;

import main.engine.*;
import utilities.GameButton;
import utilities.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * A panel representing the main menu.
 * @author Victoria Rönnlid
 */
public class MainMenu extends JPanel implements ActionListener { // the mainMenu class javas JPanel & implements ActionListener interface.
    private GameButton startBtn; // Declaring Button references
    private GameButton leaderboardBtn;
    private GameButton exitBtn;
    public BgPanel bg; // BgPanel reference for instantiation

    private ArrayList<GameButton> buttons; //declaring arrayList of Buttons to perform redundant button-tasks.

    private StateChangeListener stateChanger; // reference to state changer instance.

    /**
     * Creates the menu object.
     * @param listener reference to the observer object to allow requesting state change
     * @author Victoria Rönnlid
     */
    public MainMenu(StateChangeListener listener) {
        bg = new BgPanel();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); //creates a box layout for the panel.
        this.setPreferredSize(new Dimension(GameConstants.WINDOW_SIZE.x, GameConstants.WINDOW_SIZE.y));
        this.setBackground(Color.decode("#A9E000")); // sets the color to the nokia snake green background color.

        JLabel titleLabel = new JLabel("Snake Evolution", SwingConstants.CENTER); //creates the title "snake evolution" for the menu.
        titleLabel.setForeground(Color.BLACK); //colors it black.
        titleLabel.setFont(new Font("Public Pixel", Font.BOLD, 25)); //changes font and size.
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //centers the text.
        this.add(Box.createRigidArea(new Dimension(0, 15))); //creates a blank area above title for visual spacing.
        this.add(titleLabel); // adds title to panel.
        this.add(Box.createRigidArea(new Dimension(0, 100)));

        startBtn = new GameButton("Start"); //assigning buttons.
        leaderboardBtn = new GameButton("Leaderboard");
        exitBtn = new GameButton("Exit");

        buttons = new ArrayList<>();// initializing the Button ArrayList.
        buttons.add(startBtn);// adding the existing Button objects to the list.
        buttons.add(leaderboardBtn);
        buttons.add(exitBtn);

        for (GameButton button : buttons) {
            this.add(button); // add the buttons to the panel
            button.setActionCommand(button.getText()); //sets action command for the button that is the same as its name
            button.addActionListener(this); //adds listener to register button interaction
        }

        stateChanger = listener;
    }

    /**
     * Draws all menu elements and the background.
     * @param g graphics component supplied by the GameFrame
     * @author Victoria Rönnlid
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        bg.paintComponent(g);
    }

    /**
     * Defines button behaviour
     * @param event the button-press event to be processed
     * @author Victoria Rönnlid
     */
    @Override
    public void actionPerformed(ActionEvent event) { // logic for when buttons are clicked.
        String actionCommand = event.getActionCommand();

        if ("Start".equals(actionCommand)) {
            stateChanger.changeState(GameState.GAME); // switches to state GAME.
        } else if ("Leaderboard".equals(actionCommand)) {
            stateChanger.changeState(GameState.LEADERBOARD); // switches to state LEADERBOARD.
        } else if ("Exit".equals(actionCommand)) {
            System.exit(0); // terminates the program.
        }
    }
}








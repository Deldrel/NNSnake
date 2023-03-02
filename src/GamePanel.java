import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 600;
    final int DELAY = 1;
    boolean running = false;
    Timer timer;

    final int populationSize = 1000;
    Snake[] snakes = new Snake[populationSize];
    int generation = 0;
    int max_moves = 100;

    GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyBoardListener());
        newPopulation();
        startGame();
    }

    public void startGame() {
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.BLACK);
        draw(g);
    }

    public void draw(Graphics g) {
        max_moves--;
        if (max_moves <= 0) {
            nextGen();
        }
        for (int i = 0; i < populationSize; i++) {
            if (snakes[i].state) {
                snakes[i].draw(g, true);
                return;
            }
        }
        snakes[0].draw(g, true);
        nextGen();
    }

    public void nextGen() {
        int score = 0, bestScore = 0, bestIndex = 0;
        for (int i = 0; i < populationSize; i++) {
            score = (snakes[i].getSnakeSize() - 3) * snakes[i].getTimeAlive();
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        for (int i = 0; i < populationSize; i++) {
            if (i != bestIndex) {
                snakes[i].setBrain(snakes[bestIndex].getBrain());
                snakes[i].mutate(1f / populationSize);
                snakes[i].reset();
            }
        }

        max_moves = 100 + generation / 100 * 10;
        generation++;
        Util.print("Generation: " + generation + " Best Score: " + bestScore + " Max Moves: " + max_moves);
    }

    public void newPopulation() {
        for (int i = 0; i < populationSize; i++) {
            snakes[i] = new Snake(SCREEN_WIDTH / 2, 0, SCREEN_WIDTH / 2, SCREEN_HEIGHT, 20, SCREEN_WIDTH);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running)
            return;

        for (int i = 0; i < populationSize; i++) {
            snakes[i].update();
        }

        repaint();
    }

    public class KeyBoardListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {

            }
        }
    }
}

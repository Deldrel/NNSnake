import java.awt.*;
import java.util.Random;

public class Snake {
    final int X, Y, WIDTH, HEIGHT, gridSize;
    int x, y, snakeSize, appleX, appleY, timeAlive = 0;
    int [][]grid;
    boolean state = true;
    Random random;
    Color minColor = new Color(0, 30, 0, 0);
    int inputSize = 9;

    NeuralNetwork brain;

    public Snake(int _x, int _y, int _width, int _height, int _gridSize, int _screen_width) {
        random = new Random();
        X = _x;
        Y = _y;
        WIDTH = _width;
        HEIGHT = _height;
        gridSize = (int) Util.constraint(_gridSize, 10, Math.min(_width, _height));
        grid = new int[gridSize][gridSize];
        snakeSize = 3;
        x = gridSize / 2;
        y = gridSize / 2;

        brain = new NeuralNetwork(0, 0, _screen_width / 2, HEIGHT, new int[]{inputSize, 4});

        newApple();
    }

    public void move(int direction) {
        switch (direction) {
            case 0:
                if (y > 0)
                    y--;
                else
                    state = false;
                break;
            case 1:
                if (x < gridSize - 1)
                    x++;
                else
                    state = false;
                break;
            case 2:
                if (y < gridSize - 1)
                    y++;
                else
                    state = false;
                break;
            case 3:
                if (x > 0)
                    x--;
                else
                    state = false;
                break;
            default:
                break;
        }

        if (grid[x][y] > 0) {
            state = false;
            return;
        }

        if (x == appleX && y == appleY) {
            snakeSize++;
            newApple();
        } else {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (grid[i][j] > 0) grid[i][j]--;
                }
            }
        }
        grid[x][y] = snakeSize;
    }

    public void draw(Graphics g, boolean drawBrain) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j] == 0)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(Util.lerpColor(minColor, Color.GREEN, Util.map(grid[i][j], 0, snakeSize, 0, 1)));
                g.fillRect(X + i * WIDTH / gridSize, Y + j * HEIGHT / gridSize, WIDTH / gridSize, HEIGHT / gridSize);
            }
        }
        g.setColor(Color.RED);
        g.fillOval(X + appleX * WIDTH / gridSize, Y + appleY * HEIGHT / gridSize, WIDTH / gridSize, HEIGHT / gridSize);

        if (drawBrain) {
            brain.draw(g);
        }
    }

    public void newApple() {
        do {
            appleX = random.nextInt(gridSize);
            appleY = random.nextInt(gridSize);
        } while (grid[appleX][appleY] > 0);
    }

    public void update() {
        if (state) {
            float[] inputs = new float[inputSize];
            inputs[0] = Util.map(x, 0, gridSize, 0, 1);
            inputs[1] = Util.map(y, 0, gridSize, 0, 1);
            inputs[2] = Util.map(appleX - x, 0, gridSize, 0, 1);
            inputs[3] = Util.map(appleY - y, 0, gridSize, 0, 1);
            inputs[4] = Util.map(snakeSize, 0, gridSize * gridSize, 0, 1);
            inputs[5] = Util.map(gridSize - x, 0, gridSize, 0, 1);
            inputs[6] = Util.map(gridSize - y, 0, gridSize, 0, 1);
            inputs[7] = Util.map(appleX, 0, gridSize, 0, 1);
            inputs[8] = Util.map(appleY, 0, gridSize, 0, 1);
            brain.feedForward(inputs);
            move(brain.getHighestOutput());
            timeAlive++;
        }
    }

    public void mutate(float mutationRate) {
    	brain.mutate(mutationRate);
    }

    public void reset() {
    	grid = new int[gridSize][gridSize];
    	snakeSize = 3;
        x = gridSize / 2;
        y = gridSize / 2;
    	timeAlive = 0;
    	state = true;
    	newApple();
    }

    //getter snakeSize
    public int getSnakeSize() {
    	return snakeSize;
    }

    //getter timeAlive
    public int getTimeAlive() {
    	return timeAlive;
    }

    //setter brain
    public void setBrain(NeuralNetwork _brain) {
    	brain = _brain;
    }

    //getter brain
    public NeuralNetwork getBrain() {
    	return brain;
    }
}

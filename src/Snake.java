import java.awt.*;
import java.util.Random;

public class Snake {
    final int X, Y, WIDTH, HEIGHT, gridSize;
    int x, y, snakeSize, appleX, appleY, timeAlive = 0;
    int[][] grid;
    boolean state = true;
    Random random;
    Color minColor = new Color(0, 30, 0, 0);
    int inputSize;

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

        inputSize = 28;
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
            brain.feedForward(getInputs());
            move(brain.getHighestOutput());
            timeAlive++;
        }
    }

    public float[] getInputs() {
        float[] inputs = new float[inputSize];

        int index = 0;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i == 0 && j == 0) continue;
                if (x + i < 0 || x + i >= gridSize || y + j < 0 || y + j >= gridSize) {
                    inputs[index] = 1;
                } else {
                    inputs[index] = grid[x + i][y + j] > 0 ? 1 : 0;
                }
                index++;
            }
        }
        inputs[index] = appleX > x ? 1 : 0;
        index++;
        inputs[index] = appleX < x ? 1 : 0;
        index++;
        inputs[index] = appleY > y ? 1 : 0;
        index++;
        inputs[index] = appleY < y ? 1 : 0;


        return inputs;
    }

    public void mutate(float mutationRate) {
        brain.mutate(mutationRate);
    }

    public void reset() {
        grid = new int[gridSize][gridSize];
        snakeSize = 8;
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

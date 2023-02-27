import java.awt.*;
import java.util.Arrays;

public class NeuralNetwork {
    int X;
    int Y;
    int WIDTH;
    int HEIGHT;
    private float[][] neurons;
    private float[][] weights;
    private int[][] x;
    private int[][] y;
    private int[] neuronSize;

    public NeuralNetwork(int x, int y, int width, int height, int[] layers) {
        X = x;
        Y = y;
        WIDTH = width;
        HEIGHT = height;
        init(layers);
        computeXY();
    }

    public void feedForward(float[] inputs) {
        System.arraycopy(inputs, 0, neurons[0], 0, inputs.length);
        for (int i = 1; i < neurons.length; i++) {
            for (int j = 0; j < neurons[i].length; j++) {
                float sum = 0;
                for (int k = 0; k < neurons[i - 1].length; k++) {
                    sum += neurons[i - 1][k] * weights[i - 1][k * neurons[i].length + j];
                }
                neurons[i][j] = (float) (1 / (1 + Math.exp(-sum)));
            }
        }
    }

    public void train(float[] inputs, float[] targets, float learningRate) {
        feedForward(inputs);
        float[][] errors = new float[neurons.length][];
        for (int i = 0; i < errors.length; i++) {
            errors[i] = new float[neurons[i].length];
        }
        for (int i = 0; i < neurons[neurons.length - 1].length; i++) {
            errors[neurons.length - 1][i] = (targets[i] - neurons[neurons.length - 1][i]) * neurons[neurons.length - 1][i] * (1 - neurons[neurons.length - 1][i]);
        }
        for (int i = neurons.length - 2; i > 0; i--) {
            for (int j = 0; j < neurons[i].length; j++) {
                float error = 0;
                for (int k = 0; k < neurons[i + 1].length; k++) {
                    error += errors[i + 1][k] * weights[i][j * neurons[i + 1].length + k];
                }
                errors[i][j] = error * neurons[i][j] * (1 - neurons[i][j]);
            }
        }
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] += learningRate * neurons[i][j / neurons[i + 1].length] * errors[i + 1][j % neurons[i + 1].length];
            }
        }
    }

    public void init(int[] layers) {
        neurons = new float[layers.length][];
        neuronSize = new int[layers.length];
        weights = new float[layers.length - 1][];
        for (int i = 0; i < layers.length; i++) {
            neurons[i] = new float[layers[i]];
            Arrays.fill(neurons[i], 1);
        }
        for (int i = 0; i < layers.length - 1; i++) {
            weights[i] = new float[layers[i] * layers[i + 1]];
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = 0;
            }
        }
    }

    public void computeXY() {
        x = new int[neurons.length][];
        y = new int[neurons.length][];
        for (int i = 0; i < neurons.length; i++) {
            x[i] = new int[neurons[i].length];
            y[i] = new int[neurons[i].length];
            int cols = WIDTH / neurons.length;
            int rows = HEIGHT / neurons[i].length;
            neuronSize[i] = Math.min(cols, rows) / 2;
            for (int j = 0; j < neurons[i].length; j++) {
                x[i][j] = X + i * cols + cols / 2;
                y[i][j] = Y + j * rows + rows / 2;
            }
        }
    }

    public void draw(Graphics g) {
        float val;
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                val = (weights[i][j] + 1) / 2;
                g.setColor(Util.lerpColor(Color.RED, Color.GREEN, val, true));
                g.drawLine(x[i][j / neurons[i + 1].length], y[i][j / neurons[i + 1].length], x[i + 1][j % neurons[i + 1].length], y[i + 1][j % neurons[i + 1].length]);
            }
        }

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y[i].length; j++) {
                g.setColor(Util.lerpColor(Color.BLACK, Color.WHITE, neurons[i][j]));
                g.fillOval(x[i][j] - neuronSize[i] / 2, y[i][j] - neuronSize[i] / 2, neuronSize[i], neuronSize[i]);
            }
        }

        //write neuron value in neurons
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y[i].length; j++) {
                g.setColor(Color.BLACK);
                g.drawString(String.format("%.2f", neurons[i][j]), x[i][j] - neuronSize[i] / 2, y[i][j] - neuronSize[i] / 2);
            }
        }
    }

    public int getHighestOutput() {
        int index = 0;
        float highest = 0;
        for (int i = 0; i < neurons[neurons.length - 1].length; i++) {
            if (neurons[neurons.length - 1][i] > highest) {
                highest = neurons[neurons.length - 1][i];
                index = i;
            }
        }
        return index;
    }

    public void mutate(float rate) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                if (Util.random(0, 1) < rate) {
                    weights[i][j] += Util.random(-1, 1) / 10;
                    weights[i][j] = Util.constraint(weights[i][j], -1, 1);
                }
            }
        }
    }
}

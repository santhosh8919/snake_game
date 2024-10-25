import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    
    // Inner class to represent each tile (either a part of the snake or the food)
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }  

    // Board dimensions
    int BoardWidth;
    int BoardHeight;
    int tileSize = 20;  // Size of each tile in pixels
    
    // Snake components
    Tile snakeHead;                 // Snake head (a tile)
    ArrayList<Tile> SnakeBody;      // Snake body segments

    // Food component
    Tile food;                      // Food position on the board
    Random random;                  // Random generator to place food

    // Movement logic
    int velocityX;                  // X-axis velocity for snake's movement
    int velocityY;                  // Y-axis velocity for snake's movement
    Timer gameLoop;                 // Timer for game updates

    boolean gameOver = false;       // Flag to check if the game is over

    // Constructor for SnakeGame
    SnakeGame(int BoardWidth, int BoardHeight) {
        // Set board dimensions
        this.BoardWidth = BoardWidth;
        this.BoardHeight = BoardHeight;
        setPreferredSize(new Dimension(this.BoardWidth, this.BoardHeight));
        setBackground(Color.black);
        addKeyListener(this); // Add key listener for movement controls
        setFocusable(true);   // Ensure the JPanel can receive keyboard input

        // Initialize snake position and food placement
        snakeHead = new Tile(5, 5); // Start snake head at (5,5)
        SnakeBody = new ArrayList<Tile>(); // Initialize empty body

        food = new Tile(10, 10); // Start food at (10,10)
        random = new Random();   // Create random generator
        placeFood();             // Place food in a random position

        velocityX = 1;           // Initial movement direction to the right
        velocityY = 0;
        
        // Set up the game timer
        gameLoop = new Timer(100, this); // Timer triggers `actionPerformed` every 100ms
        gameLoop.start();                // Start the game loop
    }   
    
    // Method to render the game screen
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the screen
        draw(g);                  // Call the draw method
    }

    // Draw method to render all game elements
    public void draw(Graphics g) {
        // Draw grid lines
        // for(int i = 0; i < BoardWidth/tileSize; i++) {
        //     g.drawLine(i * tileSize, 0, i * tileSize, BoardHeight); // Vertical lines
        //     g.drawLine(0, i * tileSize, BoardWidth, i * tileSize);   // Horizontal lines
        // }

        // Draw food in red color
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Draw snake head in green
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Draw snake body in green
        for (Tile snakePart : SnakeBody) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Display score and game over message
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.white);
            g.drawString("Game Over: " + SnakeBody.size(), tileSize - 14, tileSize);
        } else {
            g.drawString("Score: " + SnakeBody.size(), tileSize - 16, tileSize);
        }
    }

    // Method to place food at a random location
    public void placeFood(){
        food.x = random.nextInt(BoardWidth / tileSize);  // Random x position within board width
        food.y = random.nextInt(BoardHeight / tileSize); // Random y position within board height
    }

    // Method to move the snake
    public void move() {
        // Check if snake has eaten the food
        if (collision(snakeHead, food)) {
            SnakeBody.add(new Tile(food.x, food.y)); // Grow the snake
            placeFood();                             // Place new food
        }

        // Move snake body segments to follow the head
        for (int i = SnakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = SnakeBody.get(i);
            if (i == 0) { // Part next to the head follows the head
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = SnakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        
        // Move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Check for collisions with the snake's body
        for (Tile snakePart : SnakeBody) {
            if (collision(snakeHead, snakePart)) { // If head collides with any body part
                gameOver = true;
            }
        }

        // Check for border collisions
        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize >= BoardWidth ||
            snakeHead.y * tileSize < 0 || snakeHead.y * tileSize >= BoardHeight) {
            gameOver = true; // Game over if snake goes out of bounds
        }
    }

    // Method to check for collisions between two tiles
    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Called every timer tick
        move();         // Update snake position and check game state
        repaint();      // Redraw the game
        if (gameOver) { // If game is over, stop the timer
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        // Check key press to change direction if it's not opposite to current
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    // Unused methods from KeyListener interface
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

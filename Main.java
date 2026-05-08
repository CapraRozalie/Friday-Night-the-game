import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.Timer;

class Frem extends JFrame{
    public Frem(JPanel berea, JPanel picture) {
        this.setTitle("Prinde Berea!");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, berea, picture);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(1280 * 2 / 3);
        splitPane.setEnabled(false);

        this.add(splitPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}

class Lada{
    int yLocation;
    int xLocation;
    Image ladaImage;

    public Lada(){
        this.xLocation=0;
        this.yLocation=520;
        ladaImage = new ImageIcon("manamea1.png").getImage();
    }

    public void update(int x, int y){
        if(x<800)
            xLocation=x-60;
    }

    public void draw(Graphics g){
        g.drawImage(ladaImage, xLocation, yLocation, null);
    }
}

class Bottle{
    static int score = 0;
    static int lives = 5;
    static int maxScore = 15000;

    int xLocation;
    int yLocation;
    double fSpeed;
    boolean cought=false;

    Image sticla;

    Random random = new Random();

    public Bottle() {
        reset();
        sticla = new ImageIcon("beer1.png").getImage();
    }

    public void reset(){
        this.xLocation = random.nextInt((1280/3*2-40)+10);
        this.yLocation = -60-random.nextInt(20000);
        this.fSpeed = random.nextInt(5)+1;
        this.cought=false;
        sticla = new ImageIcon("beer1.png").getImage();
    }

    public void update(int x, int y){
        if(yLocation<=800){
            yLocation+=fSpeed;
        }

        if(yLocation<=600 && yLocation >=450 && xLocation>=x && xLocation<=x+40 && cought==false){
            sticla=null;
            score+=100;
            cought=true;
        }

        if(yLocation>650 && cought==false){
            lives--;
            cought=true;
        }
    }

    public void draw(Graphics g){
        if(sticla!=null)
            g.drawImage(sticla, xLocation, yLocation, null);
    }
}

class BeerPanel extends JPanel {
    Bottle[] bottles;
    Lada lada;
    JLabel scoreLabel;
    int mouseX=0;
    int mouseY=0;

    public BeerPanel(int count) {
        this.setBackground(new Color(0, 90, 0));
        this.setPreferredSize(new Dimension(1280 * 2 / 3, 720));
        this.setLayout(null);

        bottles = new Bottle[count];
        for (int i = 0; i < count; i++) {
            bottles[i] = new Bottle();
        }

        lada = new Lada();

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(10,10,300,40);

        this.add(scoreLabel);
    }

    public void restartGame(){
        Bottle.score = 0;
        Bottle.lives = 5;

        for(Bottle b : bottles){
            b.reset();
        }

        repaint();
    }

    public void update() {
        for (Bottle b : bottles) {
            b.update(mouseX, mouseY);
        }

        lada.update(mouseX,mouseY);

        scoreLabel.setText("Score: " + Bottle.score + "  Lives: " + Bottle.lives);
        scoreLabel.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Bottle b : bottles) {
            b.draw(g);
        }

        lada.draw(g);
    }
}

class picturePanel extends JPanel {
    static int number = 1;
    static int lastScore = 0;

    JLabel picLabel;

    public picturePanel() {
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(1280 / 3, 720));
        this.picLabel = new JLabel(new ImageIcon("a1.png"));
        this.add(picLabel);
    }

    public void restartPictures(){
        number = 1;
        lastScore = 0;

        this.remove(picLabel);

        this.picLabel = new JLabel(new ImageIcon("a1.png"));

        this.add(picLabel);

        this.revalidate();
        this.repaint();
    }

    public void update() {
        if (Bottle.score >= 2500 * number && Bottle.score != lastScore) {
            lastScore = Bottle.score;
            picturePanel.number++;

            this.remove(picLabel);

            this.picLabel = new JLabel(new ImageIcon("a" + number + ".png"));

            this.add(picLabel);

            this.revalidate();
            this.repaint();
        }
    }
}

class GameLoop implements ActionListener {
    BeerPanel beerPanel;
    picturePanel picturePanel;
    Timer timer;

    public GameLoop(BeerPanel beerPanel, picturePanel picturePanel, Timer timer) {
        this.beerPanel = beerPanel;
        this.picturePanel = picturePanel;
        this.timer = timer;
    }

    public void restart(){
        beerPanel.restartGame();
        picturePanel.restartPictures();
        timer.start();
    }

    public void gameOverPopup(){
        timer.stop();

        int choice = JOptionPane.showOptionDialog(
                null,
                "Final Score: " + Bottle.score,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Restart", "Exit"},
                "Restart"
        );

        if (choice == JOptionPane.YES_OPTION){
            restart();
        }
        else{
            System.exit(0);
        }
    }

    public void winPopup(){
        timer.stop();

        int choice = JOptionPane.showOptionDialog(
                null,
                "Max Score Reached: " + Bottle.score,
                "You Win!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Restart", "Exit"},
                "Restart"
        );

        if(choice == JOptionPane.YES_OPTION){
            restart();
        }
        else{
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(Bottle.score >= Bottle.maxScore){
                    winPopup();
                }
                else if(Bottle.lives>0){
                    beerPanel.update();
                    beerPanel.repaint();
                    picturePanel.update();
                }
                else{
                    gameOverPopup();
                }
            }
        });
    }
}

public class Main {
    public static void main(String[] args) {
        BeerPanel berea = new BeerPanel(155);
        picturePanel picture = new picturePanel();

        new Frem(berea, picture);

        Timer timer = new Timer(16, null);
        GameLoop gameLoop = new GameLoop(berea, picture, timer);

        timer.addActionListener(gameLoop);
        timer.start();
    }
}
package Interface;

import java.awt.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import GeneticAlgorithm.Individual;
import GeneticAlgorithm.World;

public class ParetoPanel extends JPanel{

    String nameID;

    private List<Color> paretoColor = new ArrayList<Color>();
    private static int pointWidth = 6;  // Tamanho do tracinho

    List<Individual> individuals;

    double minDistance;
    double maxDistance;
    double minTime;
    double maxTime;


    boolean alreadyFindFitness = false;
    boolean hasUpdate = true;

    private BufferedImage image;
    Image img;

    public ParetoPanel() throws IOException{

        this.nameID = "Pareto";

        genParetoColor();
        this.individuals = new ArrayList<Individual>();

        // Adiciona a imagem
        image = ImageIO.read(new File("src\\Interface\\Resources\\Graph.png"));

        this.setFocusable(true);
        this.setVisible(true);
    }

    private void genParetoColor(){
        paretoColor.add(new Color(200, 0, 0));
        paretoColor.add(new Color(0, 200, 0));
        paretoColor.add(new Color(0, 0, 200));
        paretoColor.add(new Color(200, 0, 200));
        paretoColor.add(new Color(200, 200, 0));
        paretoColor.add(new Color(0, 200, 200));
    }

    public void UpdatePareto(World world){

        this.individuals.clear();

        for (Individual individual : world.populationOfIndividuals){
            this.individuals.add(individual);
        }
        hasUpdate = true;

        this.minDistance = Double.MAX_VALUE / 2;
        this.maxDistance = -1;
        this.minTime = Double.MAX_VALUE / 2;
        this.maxTime = -1;

        for (Individual individual : this.individuals){
            if (this.minDistance > individual.NormalizedDistanceFitness){
                this.minDistance = individual.NormalizedDistanceFitness;
            }
            if (this.maxDistance < individual.NormalizedDistanceFitness){
                this.maxDistance = individual.NormalizedDistanceFitness;
            }

            if (this.minTime > individual.NormalizedTimeFitness){
                this.minTime = individual.NormalizedTimeFitness;
            }
            if (this.maxTime < individual.NormalizedTimeFitness){
                this.maxTime = individual.NormalizedTimeFitness;
            }
        }
    }

    private void drawPareto(Graphics g){

        int minOffsetX = 40;
        int minOffsetY = 263;
        int maxOffsetX = 272;
        int maxOffsetY = 47;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        double diffTime = this.maxTime - this.minTime;
        double diffDistance = this.maxDistance - this.minDistance;

        if (!individuals.isEmpty()){

            for (Individual individual : this.individuals){
                int rank = individual.rank - 1;
                if (rank > paretoColor.size() - 1){
                    rank = paretoColor.size() - 1;
                }

                g2.setColor(paretoColor.get(rank));

                int posX = (int)((individual.NormalizedTimeFitness - this.minTime) / diffTime * (maxOffsetX - minOffsetX)) + minOffsetX;
                int posY = (int)((individual.NormalizedDistanceFitness - this.minDistance) / diffDistance * (maxOffsetY - minOffsetY)) + minOffsetY;

                Point visualPosition = new Point(posX, posY);

                int x = visualPosition.x - pointWidth / 2;
                int y = visualPosition.y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

        drawPareto(g); // Desenha os pontos do pareto
    }
}

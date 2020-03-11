package life;


import java.util.Random;

public class MotoreImmobile{

    Universe universe;
    int size;

    MotoreImmobile(Universe universe){
        this.universe = universe;
        initialize();
    }

    public void initialize(){
        this.size = universe.getSize();

        universe.setMap(generate());
        universe.setAlive(countAlive());
    }

    private byte[][] generate(){

        byte[][] tmpMap = new byte[size][size];

        Random rand = new Random();

        for (int i = 0; i < size; i++)
            for (int k = 0; k < size; k++) {
                tmpMap[i][k] = (byte)(rand.nextBoolean() ? 1 : 0);
                updateNeighboursOf(i, k, (byte) 0, tmpMap[i][k]);
            }

        return tmpMap;
    }

    public void evolve(int generations){
        for (int i = 0; i < generations; i++)
            this.evolve();
    }

    public void evolve(){
        byte[][] futureMap = new byte[size][size];
        byte[][] neighbours = universe.getNeighbours();
        byte[][] map = universe.getMap();

        for(int i = 0; i < size; i++){
            for(int k = 0; k < size; k++){

                byte futureStatus = 0;
                byte thisNeighbours = neighbours[i][k];

                if(map[i][k] == 1) {
                    if (thisNeighbours == 2 || thisNeighbours == 3)
                        futureStatus = 1;

                }else if(thisNeighbours == 3)
                    futureStatus = 1;

                futureMap[i][k] = futureStatus;
            }
        }

        for(int i = 0; i < size; i++)
            for(int k = 0; k < size; k++)
                updateNeighboursOf(i, k, futureMap[i][k]);

        universe.setMap(futureMap);
        universe.setAlive(countAlive());
        universe.setNeighbours(neighbours);
    }

    private int countAlive(){
        byte[][] map = universe.getMap();
        int total = 0;

        for (byte[] row : map)
            for (byte e : row)
                total += e;

        return total;
    }

    private void updateNeighboursOf(int x0, int y0, byte newStatus){

        byte oldStatus = universe.getMap()[x0][y0];
        updateNeighboursOf(x0, y0, oldStatus, newStatus);
    }

    private void updateNeighboursOf(int x0, int y0, byte oldStatus, byte newStatus){

        byte[][] neighbours = universe.getNeighbours();
        byte var = (byte) (newStatus - oldStatus);

        for(int dx = -1; dx <= 1; dx++){

            int x = (x0 + dx) % size;
            x = x < 0 ? size + x : x;

            for(int dy = -1; dy <= 1; dy++){

                int y = (y0 + dy) % size;
                y = y < 0 ? size + y : y;

                if(!(dx == 0 && dy == 0))
                    neighbours[x][y] += var;
            }
        }

        universe.setNeighbours(neighbours);
    }

    public void loadSavedMap(int size, int generationNumber, byte[][] map){
        this.size = size;
        universe.loadSavedMap(size, generationNumber, map);
        universe.setNeighbours(countNeighbours());
        universe.setAlive(countAlive());
    }

    private byte[][] countNeighbours(){
        byte[][] neighbours = new byte[size][size];
        byte[][] map = universe.getMap();

        for(int i = 0; i < size; i++) {
            for(int k = 0; k < size; k++) {
                for (int dx = -1; dx <= 1; dx++) {

                    int x = (i + dx) % size;
                    x = x < 0 ? size + x : x;

                    for (int dy = -1; dy <= 1; dy++) {

                        int y = (k + dy) % size;
                        y = y < 0 ? size + y : y;

                        if (!(dx == 0 && dy == 0))
                            neighbours[i][k] += map[x][y];
                    }
                }
            }
        }

        return neighbours;
    }

}

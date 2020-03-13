package life;


import javafx.geometry.Pos;

import java.util.Random;

public class MotoreImmobile{

    Universe universe;
    Size size;

    MotoreImmobile(Universe universe){
        this.universe = universe;
        initialize();
    }

    public void initialize(){
        this.size = universe.getSize();

        universe.setMap(size.makeByteMatrix());
        universe.setAlive(0);
    }

    public void fillRandom(){
        universe.setMap(generateRandom());
        universe.setAlive(countAlive());
    }

    private byte[][] generateRandom(){

        byte[][] tmpMap = size.makeByteMatrix();

        Random rand = new Random();

        PositionPointer position = new PositionPointer(size);
        while(position.increment()) {
            tmpMap[position.y][position.x] = (byte) (rand.nextBoolean() ? 1 : 0);
            updateNeighboursOf(universe.getNeighbours(), position, (byte) 0, tmpMap[position.y][position.x]);
        }

        return tmpMap;
    }

    public void evolve(int generations){
        for (int i = 0; i < generations; i++)
            this.evolve();
    }

    public void evolve(){
        byte[][] futureMap = size.makeByteMatrix().clone();
        byte[][] neighbours = universe.getNeighbours();
        byte[][] map = universe.getMap();


        byte thisNeighbours;
        byte futureStatus;

        PositionPointer pos = new PositionPointer(size);
        while(pos.increment()){

            if((thisNeighbours = neighbours[pos.y][pos.x]) == 0)
                continue;

            futureStatus = 0;

            if(thisNeighbours == 3)
                futureStatus = 1;

            else if(map[pos.y][pos.x] == 1 && thisNeighbours == 2)
                futureStatus = 1;

            futureMap[pos.y][pos.x] = futureStatus;
            universe.incrementAlive(futureMap[pos.y][pos.x] - map[pos.y][pos.x]);
        }

        pos = new PositionPointer(size);
        while (pos.increment())
            updateNeighboursOf(universe.getNeighbours(), pos, futureMap[pos.y][pos.x]);

        universe.setMap(futureMap);
    }

    private int countAlive(){
        byte[][] map = universe.getMap();
        int total = 0;

        for (byte[] row : map)
            for (byte e : row)
                total += e;

        return total;
    }

    private void updateNeighboursOf(byte[][] neighbours, Position pos, byte newStatus){

        byte oldStatus = universe.getCell(pos);
        updateNeighboursOf(neighbours, pos, oldStatus, newStatus);
    }

    private void updateNeighboursOf(byte[][] neighbours, Position pos, byte oldStatus, byte newStatus){
        byte var = (byte) (newStatus - oldStatus);

        if(var == 0)
            return;

        for(int dx = -1; dx <= 1; dx++){

            int x = (pos.x + dx) % size.getX();
            x = x < 0 ? size.getX() + x : x;

            for(int dy = -1; dy <= 1; dy++){

                int y = (pos.y + dy) % size.getY();
                y = y < 0 ? size.getY() + y : y;

                if(!(dx == 0 && dy == 0))
                    neighbours[y][x] += var;

            }
        }
    }

    public void loadSavedMap(byte[][] map){
        Size newSize =  new Size(map);

        universe.initialize(newSize);
        this.size = newSize;

        universe.setMap(map);
        universe.setNeighbours(countNeighbours());
        universe.setAlive(countAlive());
    }

    private byte[][] countNeighbours(){
        byte[][] neighbours = size.makeByteMatrix();
        byte[][] map = universe.getMap();

        PositionPointer pos = new PositionPointer(size);
        while(pos.increment()){
            for (int dx = -1; dx <= 1; dx++) {

                int x = (pos.x + dx) % size.getX();
                x = x < 0 ? size.getX() + x : x;

                for (int dy = -1; dy <= 1; dy++) {

                    int y = (pos.y + dy) % size.getY();
                    y = y < 0 ? size.getY() + y : y;

                    if (!(dx == 0 && dy == 0))
                        neighbours[pos.y][pos.x] += map[y][x];

                }
            }
        }

        return neighbours;
    }

    public void insertPatternCentered(byte[][] pattern){
        insertPatternInPosition(pattern, (size.getX() - pattern[0].length) / 2, (size.getY() - pattern.length) / 2);
    }

    public void insertPatternInPosition(byte[][] pattern, int x, int y){
        byte[][] map = universe.getMap();

        PositionPointer pos = new PositionPointer(new Size(pattern));
        while(pos.increment())
            map[y + pos.y][x + pos.x] |= pattern[pos.y][pos.x];

        universe.setMap(map);
        universe.setNeighbours(countNeighbours());
        universe.setAlive(countAlive());
    }

}

package life;

import java.io.FileInputStream;

public class Universe{


    public static final char ALIVE = 'O';
    public static final char DEAD = ' ';

    private byte[][] map;
    private byte[][] neighbours;
    private int generationNumber;
    private int alive;
    private int size;

    Universe(int size){
        initialize(size);
    }

    public void initialize(int size){
        this.size = size;
        map = new byte[size][size];
        neighbours = new byte[size][size];
        generationNumber = -1;
        alive = 0;
    }

    public void loadSavedMap(int size, int generationNumber, byte[][] map){
        this.size = size;
        this.generationNumber = generationNumber;
        this.map = map;
    }

    public void setMap(byte[][] map) {
        generationNumber++;
        this.map = map;
    }

    public byte[][] getMap() {
        return map;
    }

    public void setNeighbours(byte[][] neighbours) {
        this.neighbours = neighbours;
    }

    public byte[][] getNeighbours() {
        return neighbours;
    }

    public int getSize() {
        return size;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public int getAlive() {
        return alive;
    }

    public void printNeighbours(){
        for (byte[] row : neighbours) {
            for (byte e : row) {
                System.out.print(e);
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {

        StringBuilder acc = new StringBuilder();

//        int total = 0;
//        for (byte[] row : map)
//            for (byte e : row)
//                total += e;


        acc.append("size");
        acc.append("\n");
        acc.append(size);
        acc.append("\n");

        acc.append("generationNumber");
        acc.append("\n");
        acc.append(generationNumber);
        acc.append("\n");

        for (byte[] row : map) {
            for (byte e : row) {
                acc.append(e == 1 ? ALIVE : DEAD);
            }
            acc.append("\n");
        }

        return acc.toString();
    }

}
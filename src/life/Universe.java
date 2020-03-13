package life;

public class Universe{

    public static final char ALIVE = 'O';
    public static final char DEAD = '.';

    private byte[][] map;
    private byte[][] neighbours;
    private int generationNumber;
    private int alive;
    private Size size;

    Universe(Size size){
        initialize(size);
    }

    public void initialize(Size size){
        this.size = size;
        map = size.makeByteMatrix();
        neighbours = size.makeByteMatrix();
        generationNumber = -1;
        alive = 0;
    }

    public void setMap(byte[][] map) {
        generationNumber++;
        this.map = map;
    }

    public byte[][] getMap() {
        return map;
    }

    public byte getCell(Position position){
        return map[position.y][position.x];
    }

    public void setNeighbours(byte[][] neighbours) {
        this.neighbours = neighbours;
    }

    public byte[][] getNeighbours() {
        return neighbours;
    }

    public Size getSize() {
        return size;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public void incrementAlive(int var){
        this.alive += var;
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

        acc.append("Size : ");
        acc.append(size);
        acc.append("\n");

        acc.append("Generation #");
        acc.append(generationNumber);
        acc.append("\n");

        acc.append("Alvie #");
        acc.append(alive);
        acc.append("\n");

        acc.append("Current map:");
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

class Size{
    private final int x;
    private final int y;

    Size(int x, int y){
        this.x = x;
        this.y = y;
    }

    Size(byte[][] mapToMatch){
        this.x = mapToMatch[0].length;
        this.y = mapToMatch.length;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getArea(){
        return x*y;
    }

    public byte[][] makeByteMatrix(){
        return new byte[y][x];
    }

    @Override
    public String toString() {
        return "x = " + x + ", y = " + y;
    }

}

class Position{

    int x;
    int y;

    Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Position offset(Position offset){
        return new Position(offset.x + x, offset.y + y);
    }

    public Position offset(Size offset){
        return new Position(offset.getX() + x, offset.getY() + y);
    }

    @Override
    public String toString() {
        return "x = " + x + ", y = " + y;
    }
}

class PositionPointer extends Position{

    private Size size;

    PositionPointer(Size size, int initialX, int initialY){
        super(initialX, initialY);
        this.size = size;
    }

    PositionPointer(Size size, Position initialPosition){
        super(initialPosition.x, initialPosition.y);
        this.size = size;
    }

    PositionPointer(Size size){
        super(0, 0);
        this.size = size;
    }

    public boolean increment(){
        if(x++ == size.getX() - 1){

            if(y++ == size.getY() - 1)
                return false;

            x = 0;
        }

        return true;
    }

    public boolean decrement(){
        if(x-- == 0){

            if(y-- == 0)
                return false;

            x = size.getX();
        }

        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " in max (" + size + ")";
    }
}
package life;

import java.util.ArrayList;
import java.util.List;

public class Analyzer {

    public static List<Pattern> findPatterns(Map fullMap){

        Map remaindersMap = fullMap.clone();

        // todo precompile the map into a new boolean map that has true:
        //  - where there is a non zero
        //  - in all the cells around a 3 (the 3 will become a live cell)
        // In this way the patterns found will remain isolated for at least one more generation

        List<Pattern> patterns = new ArrayList<>();

        Pattern tmpMap;
        while((tmpMap = findPattern(remaindersMap)) != null)
            patterns.add(tmpMap);

        return patterns;
    }

    public static Pattern findPattern(Map map){
        // todo find a non zero (random?)
        // todo find the edge, going to one side until it's zero
        // todo right hand it until you are back where you started
        return null;
    }
}

class Pattern{

    Map patternData;
    String patternName;

    Pattern(Map patternData, String patternName){
        this.patternData = patternData;
        this.patternName = patternName;
    }

    Pattern(Map patternData){
        this(patternData, "no name");
    }
}

class Map{

    private byte[][] map;
    private Size size;

    Map(byte[][] map, Size size){
        this.map = map;
        this.size = size;
    }

    Map(Size size){
        this(new byte[size.getY()][size.getX()], size);
    }

    public byte getCell(Position position){
        return map[position.y][position.x];
    }

    public void setCell(Position position, byte value){
        map[position.y][position.x] = value;
    }

    public Map getSubMap(Position topLeft, Size size){
        PositionPointer tmpPointer = new PositionPointer(size, size.getX(), size.getY());
        tmpPointer.increment();
        Position bottomRight = topLeft.offset(tmpPointer);
        return getSubMap(topLeft, bottomRight);
    }

    public Map getSubMap(Position topLeft, Position bottomRight){
        Size size = new Size(bottomRight.x - topLeft.x + 1, bottomRight.y - topLeft.y + 1);
        Map subMap = new Map(size);

        PositionPointer pos = new PositionPointer(size);
        while(pos.increment())
            subMap.setCell(pos, this.getCell(pos.offset(topLeft)));

        return subMap;
    }

    @Override
    public Map clone(){
        return new Map(map.clone(), new Size(size.getX(), size.getY()));
    }

}

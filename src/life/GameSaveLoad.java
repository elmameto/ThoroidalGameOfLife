package life;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class GameSaveLoad {

    public static void saveToFile(Universe universe, String path){
        System.out.print(path);
        System.out.println(universe);
        File saveFile = new File(path + ".life");

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(saveFile);
            outputStream.write(generateGameSaveString(universe).getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSavedGame(MotoreImmobile motoreImmobile, String path){
        System.out.println(path);

        File savedFile = new File(path);
        FileReader savedFileReader;


        try {
            savedFileReader = new FileReader(savedFile);
            List<Byte> buffer = new ArrayList<>();

            while(savedFileReader.ready())
                buffer.add((byte)savedFileReader.read());

            Byte[] helper = new Byte[buffer.size()];
            buffer.toArray(helper);

            loadFromSaved(motoreImmobile, helper);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadFromSaved(MotoreImmobile motoreImmobile, Byte[] data){

        int positionCounter = 0;
        String line;
        boolean done = false;

        int size = 0;
        int generationNumber = 0;

        while(!done){
            line = nextLine(data, positionCounter);
            positionCounter += line.length() + 1;

            String additionalLine = "";

            switch (line){
                case("size"):
                    additionalLine = nextLine(data, positionCounter);
                    size = Integer.decode(additionalLine);
                    System.out.println("size");
                    System.out.println(size);
                    break;

                case("generationNumber"):
                    additionalLine = nextLine(data, positionCounter);
                    generationNumber = Integer.decode(additionalLine);
                    System.out.println("Generation Number");
                    System.out.println(generationNumber);
                    break;

                default:
                    done = true;
                    positionCounter -= line.length() + 1;
                    break;
            }

            if(!additionalLine.equals(""))
                positionCounter += additionalLine.length() + 1;

        }

        byte[][] map = new byte[size][size];

        for(int i = 0; i < size; i++){
            line = nextLine(data, positionCounter);
            positionCounter += line.length() + 1;

            for(int k = 0; k < size; k++)
                map[i][k] = (byte)(line.charAt(k) == 'O' ? 1 : 0);
        }

        motoreImmobile.loadSavedMap(size, generationNumber, map);
    }

    private static String nextLine(Byte[] data, int start){
        StringBuilder line = new StringBuilder();
        for (int i = start; i < data.length; i++) {
            char c = (char)data[i].byteValue();

            if(c != '\n')
                line.append(c);
            else
                return line.toString();
        }

        return "";
    }

    public static String generateGameSaveString(Universe universe){
        StringBuilder acc = new StringBuilder();

        acc.append("size");
        acc.append("\n");
        acc.append(universe.getSize());
        acc.append("\n");

        acc.append("generationNumber");
        acc.append("\n");
        acc.append(universe.getGenerationNumber());
        acc.append("\n");

        byte[][] map = universe.getMap();

        for (byte[] row : map) {
            for (byte e : row)
                acc.append(e == 1 ? Universe.ALIVE : Universe.DEAD);

            acc.append("\n");
        }

        return acc.toString();
    }

}

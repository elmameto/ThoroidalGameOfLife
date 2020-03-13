package life;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

public class GameSaveLoad {

    public static void saveToFile(byte[][] map, String path){
        System.out.print(path);
        File saveFile = new File(path + ".life");

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(saveFile);
            outputStream.write(generateGameSaveString(map).getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[][] loadPattern(String path){
        File savedFile =  new File(path);

        if(savedFile.exists()){
            String[] parts = savedFile.getName().split("[.]");

            if(parts.length == 2){
                String desinence = parts[1];

                if(desinence.equals("life"))
                    return loadFromSavedLife(savedFile);

                else if(desinence.equals("rle"))
                    return loadFromSavedRle(savedFile);
            }
        }

        return new byte[0][0];
    }

    private static byte[][] loadFromSavedLife(File savedFile){

        byte [][] savedData;

        try(Scanner scanner = new Scanner(savedFile)){

            Size size = extractSizeData(scanner);

            savedData = size.makeByteMatrix();

            for(int i = 0; i < size.getY(); i++) {

                byte[] rowData = scanner.nextLine().getBytes();

                for (int k = 0; k < size.getX(); k++)
                    if(rowData[k] == 'O')
                        savedData[i][k] = 1;
            }

        }catch (IOException e){
            return new byte[0][0];
        }

        return savedData;
    }

    public static String generateGameSaveString(byte[][] map){
        StringBuilder acc = new StringBuilder();

        acc.append("x = ");
        acc.append(map[0].length);
        acc.append(", y = ");
        acc.append(map.length);
        acc.append("\n");

        for (byte[] row : map) {
            for (byte e : row)
                acc.append(e == 1 ? Universe.ALIVE : Universe.DEAD);

            acc.append("\n");
        }

        return acc.toString();
    }

    private static Size extractSizeData(Scanner scanner){

        String line;
        int[] data = new int[2];

        while((line = scanner.nextLine()).charAt(0) == '#');

        String[] infoLine = line.split(",");

        data[0] = Integer.parseInt(infoLine[0].trim().split(" ")[2]);
        data[1] = Integer.parseInt(infoLine[1].trim().split(" ")[2]);

        return new Size(data[0], data[1]);
    }

    private static byte[][] loadFromSavedRle(File savedFile){

        byte[][] pattern;

        try (Scanner scanner = new Scanner(savedFile)){

            Size size = extractSizeData(scanner);

            StringBuilder dataAccumulator = new StringBuilder();
            while(!scanner.hasNext(".*!"))
                dataAccumulator.append(scanner.nextLine());

            String compressedData = dataAccumulator.append(scanner.next(".*!")).toString();
            compressedData = compressedData.substring(0, compressedData.length()-1);

            String[] compressedRows = compressedData.split("[$]");

            System.out.println(size);
            for (String compressedRow : compressedRows) {
                System.out.println(compressedRow);
            }

            pattern = size.makeByteMatrix();

            int rowCounter = 0;

            for(int i = 0; i < size.getY(); i++){
                char[] rowData = compressedRows[rowCounter++].toCharArray();
                int positionCounter = 0;

                for(int k = 0; k < rowData.length; k++){
                    int repetitions = 1;

                    StringBuilder repetitionsFinder = new StringBuilder();
                    while(k < rowData.length && Character.isDigit(rowData[k]))
                        repetitionsFinder.append(rowData[k++]);

                    if(repetitionsFinder.length() > 0)
                        repetitions = Integer.parseInt(repetitionsFinder.toString());

                    // Skip n lines, as if there where n$
                    if(k == rowData.length) {
                        i += repetitions - 1;
                        continue;
                    }

                    char status = rowData[k];

                    for(int f = 0; f < repetitions; f++)
                        pattern[i][positionCounter++] = (byte)(status == 'o' ? 1 : 0);

                }
            }


            for (byte[] bytes : pattern) {
                for (byte aByte : bytes) {
                    System.out.print(aByte);
                }
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new byte[0][0];
        }

        return pattern;
    }

}

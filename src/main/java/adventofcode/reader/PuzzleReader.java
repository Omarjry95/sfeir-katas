package adventofcode.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PuzzleReader {

    private String filename;

    private List<String> lines;

    public PuzzleReader(String filename) {
        this.filename = filename;

        try {
            String currentFilePath = new File("").getAbsolutePath();
            BufferedReader reader = new BufferedReader(new FileReader(currentFilePath + "\\src\\main\\resources\\" +
                    filename));
            String line = reader.readLine();

            List<String> lines = new ArrayList<>();

            while (line != null) {
//                System.out.println(line);
                lines.add(line);
                line = reader.readLine();
            }

            this.lines = lines;

            reader.close();
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String[] getLinesAsArray() {
        return lines.toArray(new String[lines.size()]);
    }

    public List<String> getLines() {
        return lines;
    }
}

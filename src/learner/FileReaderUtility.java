package learner;

import java.io.*;
import java.util.ArrayList;

public class FileReaderUtility {
    private String _path;

    public FileReaderUtility(String Path)

    {
        _path = Path;
    }

    public ArrayList<String> ReadFile()
    {
        ArrayList<String> clauses  = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(_path);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;

            while((line = bufferedReader.readLine()) != null)
            {
                clauses.add(line.trim());
            }

            bufferedReader.close();
            return clauses;
        }

        catch(FileNotFoundException ex) {

        }
        catch(IOException ex) {
        }

        return clauses;
    }
}

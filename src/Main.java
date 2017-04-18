import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException{
        BufferedReader f = new BufferedReader(new FileReader(args[0]));
        String str;
        ArrayList<Router> network = new ArrayList<>();
        while((str = f.readLine()) != null) {
            String[] tokens = str.split(" ");

        }
    }
}

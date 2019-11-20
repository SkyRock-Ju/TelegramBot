package TelegramBot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Text {

    public Text(int count) {
        this.count = count;
    }

    private int count;

    private int getCount() {
        return count;
    }

    private void setCount(int count) {
        this.count = count;
    }

    public String getText (){
        try{
        List<String> reader = Files.readAllLines(Paths.get("C:\\Users\\79627\\Desktop\\for me\\myProject\\text.txt"), StandardCharsets.UTF_8);
            String [] arrayOfTexts = reader.get(0).split("z");
            if (count==arrayOfTexts.length)
                setCount(0);
            String result = arrayOfTexts[getCount()];
            count++;
            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean containsBadWords (String word) {
        try{
            List<String> reader = Files.readAllLines(Paths.get("C:\\Users\\79627\\Desktop\\for me\\myProject\\text.txt"), StandardCharsets.UTF_8);
            List<String> arrayOfTexts = new ArrayList<>(Arrays.asList(reader.get(1).toLowerCase().split("2")));
            return arrayOfTexts.contains(word.toLowerCase());
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
}

package cloudstorage.client;

import cloudstorage.shared.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class FileDownloader {
    public static void download(String path, File file) {
        String filename = file.getName();
        String[] pieces = filename.split("[.]");
        if (!pieces[pieces.length - 1].equals("txt")) {
            filename += ".txt";
        }

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(path + filename))) {
            String text = file.getText();
            text = text.replace("\n", System.lineSeparator());
            writer.write(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package cloudstorage.client;

import cloudstorage.shared.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class FileDownloader {
    public static void download(String path, File file) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(path + file.getName()))) {
            writer.write(file.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

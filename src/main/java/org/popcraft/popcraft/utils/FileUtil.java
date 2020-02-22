package org.popcraft.popcraft.utils;

import org.popcraft.popcraft.PopCraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    private static File dataFolder = PopCraft.getPlugin().getDataFolder();

    public static void writeLine(String name, String content) {
        try {
            FileWriter fileWriter = new FileWriter(loadOrCreateFile(name), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(content);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            PopCraft.getPlugin().getLogger().severe("Could not write to file " + name);
        }
    }

    private static File loadOrCreateFile(String name) throws IOException {
        File file = new File(dataFolder.getAbsolutePath() + File.separatorChar + name);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        }
        return file;
    }

}

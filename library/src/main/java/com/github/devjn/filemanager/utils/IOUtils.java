package com.github.devjn.filemanager.utils;

import com.github.devjn.filemanager.FileData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class IOUtils {

    public static void copyFile(FileData source, String destFolder) throws IOException {
        copyFile(new File(source.getPath()), new File(destFolder, source.getName()));
    }

    public static void copyFile(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }

    public static void moveFile(FileData source, String destFolder) throws IOException {
        moveFile(new File(source.getPath()), new File(destFolder, source.getName()));
    }

    public static void moveFile(File source, File dest) {
        source.renameTo(dest); // This is a temporary solution as it doesn't allow moving across different disks
    }

}

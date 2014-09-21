package com.sydtrip.android.sydtrip.util;

import org.tukaani.xz.XZInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompressionUtils {

    public static File decompress(File inputFile, String outputFile) throws IOException {
        final XZInputStream in = new XZInputStream(new FileInputStream(inputFile));

        final FileOutputStream out = new FileOutputStream(outputFile);

        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }

        return new File(outputFile);
    }
}

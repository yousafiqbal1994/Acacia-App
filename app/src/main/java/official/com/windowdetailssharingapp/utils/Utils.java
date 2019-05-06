package official.com.windowdetailssharingapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 1:05 PM
 */
public class Utils {

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void writeToFile(File file, String data) throws IOException {
        // Delete existing metadata, if any
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete existing metadata.");
            }
        }

        // Create new file
        if (!file.createNewFile()) {
            throw new IOException("Failed to create new file.");
        }

        // Write file contents
        FileOutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }
}

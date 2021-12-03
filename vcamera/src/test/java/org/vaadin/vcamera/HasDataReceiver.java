package org.vaadin.vcamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface HasDataReceiver extends DataReceiver {

    @Override
    public default OutputStream getOutputStream(String mimeType) {
        String suffix;
        if (mimeType.contains("jpeg")) {
            suffix = ".jpeg";
        } else if (mimeType.contains("matroska")) {
            suffix = ".mkv";
        } else {
            suffix = ".file";
        }
        File latest = getLatest();
        if (latest != null) {
            latest.delete();
        }
        try {
            latest = File.createTempFile("camera", suffix);
            setLatest(latest);
            System.out.println("Streaming to temp file " + latest);
            return new FileOutputStream(latest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getLatest();

    public void setLatest(File file);

}

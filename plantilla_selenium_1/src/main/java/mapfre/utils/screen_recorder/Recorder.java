package mapfre.utils.screen_recorder;

import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class Recorder extends ScreenRecorder {
    private String videoName;

    public Recorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder) throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
    }

    @Override
    public File createMovieFile(Format fileFormat) throws IOException {
        if (!this.movieFolder.exists()) {
            this.movieFolder.mkdirs();
        } else if (!this.movieFolder.isDirectory()) {
            throw new IOException("\"" + this.movieFolder + "\" is not a directory.");
        }

        String finalVideoName = null==videoName?"NoNameGiven":videoName;
        File f = new File(this.movieFolder, finalVideoName + "." + Registry.getInstance().getExtension(fileFormat));
        return f;
    }




    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}

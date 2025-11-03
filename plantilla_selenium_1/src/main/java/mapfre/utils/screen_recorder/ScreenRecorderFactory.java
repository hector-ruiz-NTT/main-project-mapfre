package mapfre.utils.screen_recorder;


import org.monte.media.Format;
import org.monte.media.math.Rational;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.monte.media.VideoFormatKeys.*;

public class ScreenRecorderFactory {
    private static final String DEFAULT_PATH = "./";

    public static Recorder makeRecorder(String path, String videoName, boolean captureMouse) throws IOException, AWTException {
        String finalPath = null == path ? DEFAULT_PATH : path;

        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        Format mouseFormat = null;
        if (captureMouse) {
            mouseFormat = new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                    FrameRateKey, Rational.valueOf(5));
        }

        Recorder recorder = new Recorder(gc, null,
        /*
        // FORMATO AVI
        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                DepthKey, (int)24, FrameRateKey, Rational.valueOf(5),
                QualityKey, 1.0f,
                KeyFrameIntervalKey, (int) (15 * 60)),
        mouseFormat, null, new File(finalPath));
        */

                // FORMATO MOV
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_QUICKTIME),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, (int) 24, FrameRateKey, Rational.valueOf(5),
                        QualityKey, 1.0f,
                        KeyFrameIntervalKey, (int) (15 * 60)),
                mouseFormat, null, new File(finalPath));

        recorder.setVideoName(videoName);

        return recorder;
    }
}

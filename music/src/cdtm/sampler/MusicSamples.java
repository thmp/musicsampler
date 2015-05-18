package cdtm.sampler;

import com.jsyn.data.FloatSample;
import com.jsyn.util.SampleLoader;

import java.io.InputStream;
import java.util.Vector;

public class MusicSamples {

    private static MusicSamples ourInstance = new MusicSamples();

    public static MusicSamples getInstance() {
        return ourInstance;
    }

    public Vector<FloatSample> samples = new Vector<FloatSample>();

    private MusicSamples() {

        String[] files = new String[]{
                "Bass Addon.wav",
                "Bass.wav",
                "Drums Simple.wav",
                "Drums Full.wav",
                "Organ.wav",
                "Vocals Art People.wav",
                "Vocals Makes Smile.wav",
                "Wind.wav"
            };

        for (int i = 0; i < files.length; i++) {
            try {
                InputStream file = this.getClass().getClassLoader().getResourceAsStream(files[i]);
                samples.add(SampleLoader.loadFloatSample(file));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}

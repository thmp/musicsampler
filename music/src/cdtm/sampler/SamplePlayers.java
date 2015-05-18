package cdtm.sampler;

import cdtm.HttpUpdateThread;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateStereoReader;

import java.util.Vector;

public class SamplePlayers {

    private static SamplePlayers ourInstance = new SamplePlayers();

    public static SamplePlayers getInstance() {
        return ourInstance;
    }

    public boolean[] active = new boolean[]{true, true, true, false, true, false, false, false};
    public int[] volume = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
    public Vector<VariableRateStereoReader> samplePlayers = new Vector<VariableRateStereoReader>();

    public Synthesizer synth;
    public LineOut lineOut;

    private SamplePlayers() {

        try {
            synth = JSyn.createSynthesizer();
            synth.getAudioDeviceManager().setSuggestedOutputLatency( 0.33 );
            lineOut = new LineOut();
            synth.add(lineOut);

            for(int i = 0; i < MusicSamples.getInstance().samples.size(); i++) {
                samplePlayers.add(new VariableRateStereoReader());
                synth.add(samplePlayers.get(i));
                samplePlayers.get(i).output.connect(0, lineOut.input, 0);
                samplePlayers.get(i).output.connect(1, lineOut.input, 1);
                samplePlayers.get(i).rate.set(MusicSamples.getInstance().samples.get(i).getFrameRate());
                //samplePlayers.get(i).rate.set();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        synth.start();
        lineOut.start();

        synth.queueCommand(new UpdateCommand());

        // update the configuration
        (new HttpUpdateThread()).start();
    }

    public void stop() {
        synth.stop();
    }

    public void setVolume(int[] volume) {
    	this.volume = volume;
    	
    	for(int i = 0; i < volume.length; i++) {
    		active[i] = volume[i] > 50;
    	}
    }
}

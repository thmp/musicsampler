package cdtm.sampler;

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
    public Vector<VariableRateStereoReader> samplePlayers = new Vector<VariableRateStereoReader>();

    public Synthesizer synth;
    public LineOut lineOut;

    private SamplePlayers() {

        try {
            synth = JSyn.createSynthesizer();
            synth.getAudioDeviceManager().setSuggestedOutputLatency( 0.50 );
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
    }

    public void stop() {
        synth.stop();
    }

}

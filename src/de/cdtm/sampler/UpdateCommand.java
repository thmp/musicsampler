package de.cdtm.sampler;

import com.jsyn.data.FloatSample;
import com.jsyn.ports.QueueDataCommand;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.softsynth.shared.time.ScheduledCommand;

public class UpdateCommand implements ScheduledCommand {
    @Override
    public void run() {

        System.out.printf("System load %.2f\n", SamplePlayers.getInstance().synth.getUsage());

        FloatSample sampleBeat = MusicSamples.getInstance().samples.get(0);
        double duration = sampleBeat.getNumFrames() / sampleBeat.getFrameRate();

        double currentTime = SamplePlayers.getInstance().synth.getCurrentTime();
        //double currentFrame = SamplePlayers.getInstance().synth.getFrameCount();

        for(int i = 0; i < SamplePlayers.getInstance().samplePlayers.size(); i++) {
            // check if this sample is active right now
            if (SamplePlayers.getInstance().active[i]) {
                VariableRateStereoReader samplePlayer = SamplePlayers.getInstance().samplePlayers.get(i);
                FloatSample beat = MusicSamples.getInstance().samples.get(i);
                //double beatDuration = beat.getNumFrames() / beat.getFrameRate();
                samplePlayer.dataQueue.clear(); // important to stay in sync if there are longer beats
                QueueDataCommand command = samplePlayer.dataQueue.createQueueDataCommand(beat, 0, beat.getNumFrames());
                SamplePlayers.getInstance().synth.queueCommand(command);
            }
        }

        SamplePlayers.getInstance().synth.scheduleCommand(currentTime + duration, new UpdateCommand());
    }
}

package cdtm.sampler;

import com.jsyn.data.FloatSample;
import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.ports.QueueDataCommand;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.softsynth.shared.time.ScheduledCommand;

public class UpdateVolumeCommand implements ScheduledCommand {

    public static boolean[] oldConfiguration = new boolean[]{true, true, true, true, true, true, true, true};

    @Override
    public void run() {


        /*double[] data =
                {
                        0.02, 1.0,  // duration,value pair for frame[0]
                        0.30, 0.1,  // duration,value pair for frame[1]
                        0.50, 0.7,  // duration,value pair for frame[2]
                        0.50, 0.9,  // duration,value pair for frame[3]
                        0.80, 0.0   // duration,value pair for frame[4]
                };*/
        double[] data = {
                0.04, 1.0,
                0.05, 1.0,
                0.09, 0.0
            };
        SegmentedEnvelope myEnvData = new SegmentedEnvelope( data );

        double duration = 0.05; // start every x seconds

        double currentTime = SamplePlayers.getInstance().synth.getCurrentTime();
        //double currentFrame = SamplePlayers.getInstance().synth.getFrameCount();

        for(int i = 0; i < SamplePlayers.getInstance().samplePlayers.size(); i++) {
            // check if this sample is active right now
            if (SamplePlayers.getInstance().active[i] != oldConfiguration[i]) {
                VariableRateMonoReader envPlayer = SamplePlayers.getInstance().envPlayers.get(i);

                System.out.println("Channel " + i + " changed");

                if(SamplePlayers.getInstance().active[i]) {
                    envPlayer.dataQueue.clear( );
                    //envPlayer.dataQueue.queue( myEnvData, 0, 3 );
                    envPlayer.dataQueue.queue( myEnvData, 0, 1 );
                    //envPlayer.dataQueue.queueLoop( myEnvData, 1, 2 );
                    envPlayer.dataQueue.queueLoop( myEnvData, 0, 2 );
                } else {
                    //envPlayer.dataQueue.queue( myEnvData, 3, 2 );
                    envPlayer.dataQueue.queue( myEnvData, 1, 2 );
                }

                //QueueDataCommand command = samplePlayer.dataQueue.createQueueDataCommand(beat, 0, beat.getNumFrames());
                //SamplePlayers.getInstance().synth.queueCommand(command);
            }

            oldConfiguration[i] = SamplePlayers.getInstance().active[i];
        }

        SamplePlayers.getInstance().synth.scheduleCommand(currentTime + duration, new UpdateVolumeCommand());

    }

}

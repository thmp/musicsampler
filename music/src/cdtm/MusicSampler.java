package cdtm;

import cdtm.sampler.SamplePlayers;

// To play sound using Clip, the process need to be alive.
// Hence, we use a Swing application.
public class MusicSampler {

    public MusicSampler() {

        //validate();

        /*this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Test Sound Clip");
        this.setSize(300, 200);
        this.setVisible(true);*/
    }

    public void start()
    {
        SamplePlayers.getInstance().start();
    }

    public void stop() {
        SamplePlayers.getInstance().stop();
    }

    public static void main(String[] args) {
        MusicSampler musicSampler = new MusicSampler();
        musicSampler.start();
    }
}
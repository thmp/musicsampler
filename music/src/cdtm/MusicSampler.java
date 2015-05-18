package cdtm;

import processing.core.PApplet;
import cdtm.sampler.SamplePlayers;

// To play sound using Clip, the process need to be alive.
// Hence, we use a Swing application.
public class MusicSampler extends PApplet{
	private static final long serialVersionUID = 1L;
	int[] colors = {0xfffffad6, 0xfffed34f, 0xffda9e35, 0xffbd4832};
	
    public void start()
    {
        SamplePlayers.getInstance().start();
    }

    public void stop() {
        SamplePlayers.getInstance().stop();
    }
    
	@Override
	public void draw() {
		drawChannelFields();
	}

	void drawChannelFields() {
		fill(0x20105a63);
		noStroke();
		rect(0,0,width,height);
		
		for(int i = 0; i < 8; i++) {
			this.drawChannel(i);
		}
	}
	
	void drawChannel(int channel){
		if(getVolumeForChannel(channel) < 50) {
			return;
		}
		
		pushMatrix();
	    translate((channel%4) * (displayWidth/4) + (displayWidth/8), 
			     (channel/4) * (displayHeight/2) + (displayHeight/4));
	    
	    int circleResolution = frameCount % 8 + 3;
	    float radius = map(getVolumeForChannel(channel), 50, 100, 10, displayWidth/8);
	    float angle = TWO_PI/circleResolution;
	    
	    radius = random(1) < 0.5 ? -radius : radius;

	    strokeWeight(2);
	    stroke(getRandomStrokeColor());
	    fill(0x000000, 0);
	    beginShape();
	    for (int i=0; i<=circleResolution; i++){
	      float x = 0 + cos(angle*i) * radius;
	      float y = 0 + sin(angle*i) * radius;
	      vertex(x, y);
	    }
	    endShape();
	    popMatrix();
	}

	int getVolumeForChannel(int channel) {
		return SamplePlayers.getInstance().volume[channel];
	}
	
	int getRandomStrokeColor() {
		return colors[(int)random(4)];
	}
	
	@Override
	public void setup() {
		size(displayWidth, displayHeight);
		frameRate(25);
		smooth();
		background(0xff105a63);
		start();
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] {  "--present", MusicSampler.class.getName() });
	}
}
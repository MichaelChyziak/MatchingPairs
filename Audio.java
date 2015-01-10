import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


//Taken from "https://www.youtube.com/watch?v=ar0hTsb9sxM"
public class Audio implements GameConstants{

	private Clip clip;
	
	public Audio(String s) {
		try {
			System.out.println(getClass().getResourceAsStream("NewLife.wav"));
		    AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(s));
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		if (clip == null) {
			return;
		}
		stop();
		clip.setFramePosition(0);
		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volume.setValue(SOUND_LEVEL);
		clip.start();
	}
	
	public void stop() {
		if (clip.isRunning()) {
			clip.stop();
		}
	}
	
	public void close() {
		stop();
		clip.close();
	}
	
	public void loop() {
		if (clip == null) {
			return;
		}
		stop();
		clip.setFramePosition(0);
		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volume.setValue(BGM_LEVEL);
		clip.loop(clip.LOOP_CONTINUOUSLY);
	}

}

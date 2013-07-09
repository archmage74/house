package net.archmage.house;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

public class BlendControl implements OnClickListener {

	private static final String SERVER = "192.168.0.13";
	
	private static final int PORT = 8080;
	
	private static final String REQUEST_LED_ON = "/central/gpio/on";

	private static final String REQUEST_LED_OFF = "/central/gpio/off";
	
	private RestRequestService rest = new RestRequestService(SERVER, PORT);
	
	private SeekBar blendSeekBar;
	
	private View blendControlPanel;
	
	public BlendControl(View blendControlPanel, SeekBar blendSeekBar) {
		super();
		this.blendSeekBar = blendSeekBar;
		this.blendControlPanel = blendControlPanel;
	}

	@Override
	public void onClick(View sendButton) {
		int value = blendSeekBar.getProgress();
		
		if (value < 50) {
			rest.rest(REQUEST_LED_OFF);
		} else {
			rest.rest(REQUEST_LED_ON);
		}
		
		blendControlPanel.setVisibility(View.INVISIBLE);
	}

}

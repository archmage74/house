package net.archmage.house;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE ="net.archmage.firstapp.MESSAGE"; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SeekBar blendSeekBar = (SeekBar) findViewById(R.id.blend_seek_bar);
        EditText blendEditValue = (EditText) findViewById(R.id.blend_edit_text);
        Button blendSendValueButton = (Button) findViewById(R.id.blend_send_value_button);
        
        AdjustableValueMediator valueMediator = new AdjustableValueMediator(blendSeekBar, blendEditValue);
        blendSeekBar.setOnSeekBarChangeListener(valueMediator);
        blendEditValue.addTextChangedListener(valueMediator);

        View blendControlPanel = findViewById(R.id.blend_control_panel);
        MyGLSurfaceView glView = (MyGLSurfaceView) findViewById(R.id.gl_view);
        glView.setBlendControlPanel(blendControlPanel);

        BlendControl blendControl = new BlendControl(blendControlPanel, blendSeekBar);
        blendSendValueButton.setOnClickListener(blendControl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    public void sendMessage() {
    	
    }
    
}

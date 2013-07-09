package net.archmage.house;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AdjustableValueMediator implements OnSeekBarChangeListener, TextWatcher {

	private SeekBar valueSeekBar;
	
	private EditText valueEditText;
	
	public AdjustableValueMediator(SeekBar valueSeekBar, EditText valueEditText) {
		super();
		this.valueSeekBar = valueSeekBar;
		this.valueEditText = valueEditText;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			valueEditText.setText("" + progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		int value = valueSeekBar.getProgress();
		valueEditText.setText("" + value);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int value = valueSeekBar.getProgress();
		valueEditText.setText("" + value);
	}

	@Override
	public void afterTextChanged(Editable s) {
		int value = 0;
		try {
			value = Integer.parseInt(s.toString());
		} catch (NumberFormatException e) {
			value = 0;
		}
		if (value > 100) {
			value = 100;
		} else if (value < 0) {
			value = 0;
		}
		valueSeekBar.setProgress(value);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
	}

}

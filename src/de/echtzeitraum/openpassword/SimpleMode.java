package de.echtzeitraum.openpassword;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SimpleMode implements ViewMode {

	private MainView parent = null;
	private Generator generator = null;

	private SeekBar difficultyBar;
	private TextView difficultyState;

	public SimpleMode (MainView object, Generator object2) {
		this.parent = object;
		this.generator = object2;
		this.generator.setInclude("");
		this.generator.setExclude("");
		this.generator.setLowerChars(true);
	}

	/**
	 * Generates password and place it into the password field.
	 */
	public void generatePassword () {
		final EditText pwField = (EditText) this.parent.findViewById(R.id.password);

   		/* Handler; updates view after password was generated */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				setControls();
				pwField.setText(MainView.password);
			}
		};

		/* Start new thread for generating the password */
		new Thread(new Runnable() {
			public void run() {
				MainView.password = generator.getPassword();
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	public void getControls () {
		this.difficultyBar = (SeekBar) this.parent.findViewById(R.id.difficulty_bar);
		this.difficultyState = (TextView) this.parent.findViewById(R.id.difficulty_state);

		this.difficultyBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				getState();
			}
			public void onStartTrackingTouch(SeekBar arg0) { }
			public void onStopTrackingTouch(SeekBar arg0) { }
		});
	}

	protected void getState () {
		this.generator.setUpperChars(false);
		this.generator.setNumbers(true);
		this.generator.setPunctuation(false);
		switch (this.difficultyBar.getProgress()) {
		case 1:
			// Easy
			this.difficultyState.setText(this.parent.getResources().getString(R.string.difficulty_1));
			this.difficultyState.setBackgroundColor((int) Long.parseLong("FFB95603",16));
			this.generator.setLength(7);
			break;
		case 2:
			// Normal
			this.difficultyState.setText(this.parent.getResources().getString(R.string.difficulty_2));
			this.difficultyState.setBackgroundColor((int) Long.parseLong("FFE3AD08",16));
			this.generator.setLength(8);
			break;
		case 3:
			// Hard
			this.difficultyState.setText(this.parent.getResources().getString(R.string.difficulty_3));
			this.difficultyState.setBackgroundColor((int) Long.parseLong("FF719E03",16));
			this.generator.setLength(8);
			this.generator.setUpperChars(true);
			this.generator.setNumbers(true);
			break;
		case 4:
			// Very hard
			this.difficultyState.setText(this.parent.getResources().getString(R.string.difficulty_4));
			this.difficultyState.setBackgroundColor((int) Long.parseLong("FF009000",16));
			this.generator.setLength(10);
			this.generator.setUpperChars(true);
			this.generator.setPunctuation(true);
			break;
		default:
			// Very easy
			this.difficultyState.setText(this.parent.getResources().getString(R.string.difficulty_0));
			this.difficultyState.setBackgroundColor((int) Long.parseLong("FF900000",16));
			this.generator.setLength(6);
			this.generator.setNumbers(false);
			break;
		}
	}

	public int getLayoutId() {
		return R.layout.simple;
	}

	/**
	 * Saves control states.
	 */
	public void saveControls() {
		SharedPreferences settings = this.parent.getSharedPreferences(MainView.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("difficulty", this.difficultyBar.getProgress());
		editor.commit();
	}

	/**
	 * Load preferences and set the controls.
	 */
	public void setControls() {
		SharedPreferences settings = this.parent.getSharedPreferences(MainView.PREFS_NAME, 0);
		this.difficultyBar.setProgress(settings.getInt("difficulty", 2));
		getState();
	}

	public int getMenuId() {
		return R.menu.simple;
	}

}

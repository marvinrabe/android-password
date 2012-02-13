package de.echtzeitraum.openpassword;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AdvancedFragment extends Fragment implements FragmentForms {

	private EditText edit_length;
	private CheckBox edit_upper;
	private CheckBox edit_lower;
	private CheckBox edit_numbers;
	private CheckBox edit_punctuation;
	private EditText edit_include;
	private EditText edit_exclude;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    if (container == null)
	        return null;
	    View v = inflater.inflate(R.layout.advanced, container, false);
	    return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		this.getControls();
	    this.setControls();
	}

	@Override
	public void onPause() {
		this.saveControls();
		super.onPause();
	}

	public void generatePassword () {
		final EditText pwField = (EditText) getActivity().findViewById(R.id.password);
		this.applyControls();
		if(MainView.generator.isOk()) {
			/* Disable button */
			final Button button = (Button) getActivity().findViewById(R.id.ok);
			button.setEnabled(false);

			/* Disable screen rotation */
            Display display = ((WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getOrientation() == 0) {
            	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
            	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

			/* Show progress dialog */
			final ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Generating...");
			dialog.setCancelable(false);
	   		dialog.show();
	
	   		/* Handler; updates view after password was generated */
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					dialog.dismiss();
					button.setEnabled(true);
					pwField.setText(MainView.password);
	
					/* Checks if easter egg was found*/
					if(isEasterEgg()) {
						/* Easter egg found */
						Toast.makeText(getActivity().getApplicationContext(), "Easter Egg!", Toast.LENGTH_SHORT).show();
						getView().findViewById(R.id.ScrollView).setBackgroundColor((int) Long.parseLong("FF"+MainView.password,16)); // changes background color
					} else {
						/* Reset controls if easter egg was found */
						getView().findViewById(R.id.ScrollView).setBackgroundColor(android.R.color.transparent);
					}

					/* Enable screen rotation */
					getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}
			};
	
			/* Start new thread for generating the password */
			new Thread(new Runnable() {
				public void run() {
					MainView.password = MainView.generator.getPassword();
					handler.sendEmptyMessage(0);
				}
			}).start();
		} else {
			pwField.setText("");
		}
	}
	
	public void getControls () {
		this.edit_length = (EditText) getView().findViewById(R.id.length);
		this.edit_upper = (CheckBox) getView().findViewById(R.id.checkbox1);
		this.edit_lower = (CheckBox) getView().findViewById(R.id.checkbox2);
		this.edit_numbers = (CheckBox) getView().findViewById(R.id.checkbox3);
		this.edit_punctuation = (CheckBox) getView().findViewById(R.id.checkbox4);
		this.edit_include = (EditText) getView().findViewById(R.id.include);
		this.edit_exclude = (EditText) getView().findViewById(R.id.exclude);
	}

	/**
	 * Apply control states.
	 */
	public void applyControls() {
		int length = new Integer(this.edit_length.getText().toString());
		if(length < 0) {
			length = 0;
			this.edit_length.setText("0");
		} else if (length > 5120) {
			length = 5120;
			this.edit_length.setText("5120");
		}
		MainView.generator.setLength((int) length);
		MainView.generator.setUpperChars(this.edit_upper.isChecked());
		MainView.generator.setLowerChars(this.edit_lower.isChecked());
		MainView.generator.setNumbers(this.edit_numbers.isChecked());
		MainView.generator.setPunctuation(this.edit_punctuation.isChecked());
		MainView.generator.setInclude(this.edit_include.getText().toString());
		MainView.generator.setExclude(this.edit_exclude.getText().toString());
	}

	/**
	 * Saves control states.
	 */
	public void saveControls() {
		SharedPreferences.Editor editor = MainView.settings.edit();
	
		int length = new Integer(this.edit_length.getText().toString());
		if(length < 0) {
			length = 0;
		} else if (length > 5120) {
			length = 5120;
		}
		editor.putInt("length", (int) length);
		editor.putBoolean("upper", this.edit_upper.isChecked());
		editor.putBoolean("lower", this.edit_lower.isChecked());
		editor.putBoolean("numbers", this.edit_numbers.isChecked());
		editor.putBoolean("punctuation", this.edit_punctuation.isChecked());
		editor.putString("include", this.edit_include.getText().toString());
		editor.putString("exclude", this.edit_exclude.getText().toString());
		editor.commit();
	}
	
	/**
	 * Load preferences and set the controls.
	 */
	public void setControls() {	
		this.edit_length.setText(new Integer(MainView.settings.getInt("length", 8)).toString());
		this.edit_upper.setChecked(MainView.settings.getBoolean("upper", true));
		this.edit_lower.setChecked(MainView.settings.getBoolean("lower", true));
		this.edit_numbers.setChecked(MainView.settings.getBoolean("numbers", true));
		this.edit_punctuation.setChecked(MainView.settings.getBoolean("punctuation", false));
		this.edit_include.setText(MainView.settings.getString("include", ""));
		this.edit_exclude.setText(MainView.settings.getString("exclude", ""));
		this.applyControls();
	}
	
	/**
	 * Checks if user found the easter egg.
	 * @return boolean
	 */
	protected boolean isEasterEgg() {
		if( !this.edit_upper.isChecked()  && this.edit_numbers.isChecked() && !this.edit_lower.isChecked() && !this.edit_punctuation.isChecked() ) {
			if(this.edit_length.getText().toString().equals("6")) {
				String include = this.edit_include.getText().toString();
				if(include.contains("A") && include.contains("B") &&
					include.contains("C") && include.contains("D") &&
					include.contains("E") && include.contains("F")
					&& this.edit_exclude.getText().toString().equals("")) {
					return true;
				}
			}
		}
		return false;
	}

}

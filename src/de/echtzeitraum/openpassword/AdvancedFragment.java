package de.echtzeitraum.openpassword;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AdvancedFragment extends SherlockFragment implements FragmentForms {

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
	    return inflater.inflate(R.layout.advanced, container, false);
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
		final EditText pwField = (EditText) getSherlockActivity().findViewById(R.id.password);
		this.applyControls();
		if(MainView.passwordGenerator.isOk()) {
			// Disable button and show progress icon
			final Button button = (Button) getSherlockActivity().findViewById(R.id.ok);
			button.setEnabled(false);
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
	
	   		/* Handler; updates view after password was generated */
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					pwField.setText(msg.getData().getString("password"));
					
					// Checks if easter egg was found
					int color = getResources().getColor(android.R.color.transparent);
					if(isEasterEgg()) {
						// Easter egg found
						Toast.makeText(getSherlockActivity().getApplicationContext(), "Easter Egg!", Toast.LENGTH_SHORT).show();
						color = (int) Long.parseLong("FF"+msg.getData().getString("password"),16); // changes background color
					}
					getView().findViewById(R.id.ScrollView).setBackgroundColor(color);
					
					// Hide progress icon and enable button
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
					button.setEnabled(true);
				}
			};
	
			/* Start new thread for generating the password */
			new Thread(new Runnable() {
				public void run() {
					Bundle data = new Bundle();
					data.putString("password", MainView.passwordGenerator.getPassword());
					Message msg = new Message();
					msg.setData(data);
					handler.sendMessage(msg);
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
		int length = 0;
		if(!this.edit_length.getText().toString().equals("")) {
			length = Integer.valueOf(this.edit_length.getText().toString());
			if(length < 0) {
				length = 0;
				this.edit_length.setText("0");
			} else if (length > 5120) {
				length = 5120;
				this.edit_length.setText("5120");
			}
		} else {
			this.edit_length.setText("0");
		}
		MainView.passwordGenerator.length = length;
		MainView.passwordGenerator.setUpperChars(this.edit_upper.isChecked());
		MainView.passwordGenerator.setLowerChars(this.edit_lower.isChecked());
		MainView.passwordGenerator.setNumbers(this.edit_numbers.isChecked());
		MainView.passwordGenerator.setPunctuation(this.edit_punctuation.isChecked());
		MainView.passwordGenerator.setInclude(this.edit_include.getText().toString());
		MainView.passwordGenerator.setExclude(this.edit_exclude.getText().toString());
	}

	/**
	 * Saves control states.
	 */
	public void saveControls() {
		SharedPreferences.Editor editor = MainView.settings.edit();
		int length = 0;
		if(!this.edit_length.getText().toString().equals("")) {
			length = Integer.valueOf(this.edit_length.getText().toString());
			if(length < 0) {
				length = 0;
			} else if (length > 5120) {
				length = 5120;
			}
		}
		editor.putInt("length", length);
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
		this.edit_length.setText(Integer.valueOf(MainView.settings.getInt("length", 8)).toString());
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

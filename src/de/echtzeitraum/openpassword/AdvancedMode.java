package de.echtzeitraum.openpassword;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AdvancedMode implements ViewMode {

	private MainView parent = null;
	private Generator generator = null;

	private EditText edit_length;
	private CheckBox edit_upper;
	private CheckBox edit_lower;
	private CheckBox edit_numbers;
	private CheckBox edit_punctuation;
	private EditText edit_include;
	private EditText edit_exclude;

	public AdvancedMode (MainView object, Generator object2) {
		this.parent = object;
		this.generator = object2;
	}

	/**
	 * Generates password and place it into the password field.
	 */
	public void generatePassword () {
		final EditText pwField = (EditText) this.parent.findViewById(R.id.password);
		if(this.generator.isOk()) {
			/* Disable button */
			final Button button = (Button) this.parent.findViewById(R.id.ok);
			button.setEnabled(false);
	
			/* Show progress dialog */
			final ProgressDialog dialog = new ProgressDialog(this.parent);
			dialog.setMessage("Generating...");
			dialog.setCancelable(false);
	   		dialog.show();
	
	   		/* Handler; updates view after password was generated */
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					setControls();
					dialog.dismiss();
					button.setEnabled(true);
					pwField.setText(MainView.password);
	
					/* Checks if easter egg was found*/
					if(isEasterEgg()) {
						/* Easter egg found */
						button.setText("Easter Egg!");
						parent.findViewById(R.id.ScrollView).setBackgroundColor((int) Long.parseLong("FF"+MainView.password,16)); // changes background color
					} else {
						/* Reset controls if easter egg was found */
						if(button.getText().toString().startsWith("E")) {
							button.setText(parent.getResources().getString(R.string.button_generate));
							parent.findViewById(R.id.ScrollView).setBackgroundColor((int) Long.parseLong("FF000000",16));
						}
					}
	
				}
			};
	
			/* Start new thread for generating the password */
			new Thread(new Runnable() {
				public void run() {
					MainView.password = generator.getPassword();
					handler.sendEmptyMessage(0);
				}
			}).start();
		} else {
			pwField.setText("");
		}
	}

	public void getControls () {
		this.edit_length = (EditText) this.parent.findViewById(R.id.length);
		this.edit_upper = (CheckBox) this.parent.findViewById(R.id.checkbox1);
		this.edit_lower = (CheckBox) this.parent.findViewById(R.id.checkbox2);
		this.edit_numbers = (CheckBox) this.parent.findViewById(R.id.checkbox3);
		this.edit_punctuation = (CheckBox) this.parent.findViewById(R.id.checkbox4);
		this.edit_include = (EditText) this.parent.findViewById(R.id.include);
		this.edit_exclude = (EditText) this.parent.findViewById(R.id.exclude);
	}

	public int getLayoutId() {
		return R.layout.advanced;
	}

	/**
	 * Saves control states.
	 */
	public void saveControls() {
		SharedPreferences settings = this.parent.getSharedPreferences(MainView.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		int length = new Integer(this.edit_length.getText().toString());
		if(length < 0) {
			length = 0;
		} else if (length > 5120) {
			length = 5120;
		}
		this.generator.setLength((int) length);
		editor.putInt("length", (int) length);

		this.generator.setUpperChars(this.edit_upper.isChecked());
		editor.putBoolean("upper", this.edit_upper.isChecked());

		this.generator.setLowerChars(this.edit_lower.isChecked());
		editor.putBoolean("lower", this.edit_lower.isChecked());
		
		this.generator.setNumbers(this.edit_numbers.isChecked());
		editor.putBoolean("numbers", this.edit_numbers.isChecked());
		
		this.generator.setPunctuation(this.edit_punctuation.isChecked());
		editor.putBoolean("punctuation", this.edit_punctuation.isChecked());

		this.generator.setInclude(this.edit_include.getText().toString());
		editor.putString("include", this.edit_include.getText().toString());

		this.generator.setExclude(this.edit_exclude.getText().toString());
		editor.putString("exclude", this.edit_exclude.getText().toString());
		editor.commit();
	}

	/**
	 * Load preferences and set the controls.
	 */
	public void setControls() {
		SharedPreferences settings = this.parent.getSharedPreferences(MainView.PREFS_NAME, 0);

		/* Length */
		this.edit_length.setText(new Integer(settings.getInt("length", 8)).toString());

		/* Uppercase */
		this.edit_upper.setChecked(settings.getBoolean("upper", true));

		/* Lowercase */
		this.edit_lower.setChecked(settings.getBoolean("lower", true));

		/* Numbers */
		this.edit_numbers.setChecked(settings.getBoolean("numbers", true));

		/* Punctuation */
		this.edit_punctuation.setChecked(settings.getBoolean("punctuation", false));

		/* Include */
		this.edit_include.setText(settings.getString("include", ""));

		/* Exclude */
		this.edit_exclude.setText(settings.getString("exclude", ""));
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

	public int getMenuId() {
		return R.menu.advanced;
	}

}

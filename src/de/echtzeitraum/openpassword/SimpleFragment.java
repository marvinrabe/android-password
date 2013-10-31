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
import android.widget.EditText;
import android.widget.RadioGroup;

public class SimpleFragment extends SherlockFragment implements FragmentForms {

	private RadioGroup difficultyState;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    if (container == null)
	        return null;
	    View v = inflater.inflate(R.layout.simple, container, false);
	    return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.getControls();
	    this.setControls();
	    ((MainView) getActivity()).mAdapter.add(this);
	}

	@Override
	public void onPause() {
		this.saveControls();
		super.onPause();
	}

	public void generatePassword () {
		final EditText pwField = (EditText) getSherlockActivity().findViewById(R.id.password);
		this.applyControls();

		// Disable button and show progress icon
		final Button button = (Button) getSherlockActivity().findViewById(R.id.ok);
		button.setEnabled(false);
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);

   		// Handler; updates view after password was generated
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				pwField.setText(msg.getData().getString("password"));

				// Hide progress icon and enable button
				getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
				button.setEnabled(true);
			}
		};

		// Start new thread for generating the password
		new Thread(new Runnable() {
			public void run() {
				Bundle data = new Bundle();
				data.putString("password", MainView.passwordGenerator.getPassword());
				Message msg = new Message();
				msg.setData(data);
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void getControls () {
		this.difficultyState = (RadioGroup) getView().findViewById(R.id.difficulty);
	}

	public void applyControls() {
		this.applyControls(this.difficultyState.getCheckedRadioButtonId());
	}
	
	/**
	 * Apply control states.
	 */
	public void applyControls(int checkBoxId) {
		MainView.passwordGenerator.setLowerChars(true);
		MainView.passwordGenerator.setUpperChars(false);
		MainView.passwordGenerator.setNumbers(true);
		MainView.passwordGenerator.setPunctuation(false);
		switch (getCheckboxPosition(checkBoxId)) {
		case 1:
			// Easy
			MainView.passwordGenerator.length = 7;
			break;
		case 2:
			// Normal
			MainView.passwordGenerator.length = 8;
			break;
		case 3:
			// Hard
			MainView.passwordGenerator.length = 12;
			MainView.passwordGenerator.setUpperChars(true);
			MainView.passwordGenerator.setNumbers(true);
			break;
		case 4:
			// Very hard
			MainView.passwordGenerator.length = 16;
			MainView.passwordGenerator.setUpperChars(true);
			MainView.passwordGenerator.setPunctuation(true);
			break;
		default:
			// Very easy
			MainView.passwordGenerator.length = 6;
			MainView.passwordGenerator.setNumbers(false);
			break;
		}
	}
	
	/**
	 * Saves control states.
	 */
	public void saveControls() {
		SharedPreferences.Editor editor = MainView.settings.edit();
		editor.putInt("difficulty", getCheckboxPosition(this.difficultyState.getCheckedRadioButtonId()));
		editor.commit();
	}

	/**
	 * Load preferences and set the controls.
	 */
	public void setControls() {
		this.difficultyState.check(getCheckboxId(MainView.settings.getInt("difficulty", 2)));
		this.applyControls();
	}

	protected int getCheckboxId(int position) {
		switch (position) {
		case 1:
			return R.id.difficulty_1;
		case 2:
			return R.id.difficulty_2;
		case 3:
			return R.id.difficulty_3;
		case 4:
			return R.id.difficulty_4;
		default:
			return R.id.difficulty_0;
		}
	}

	protected int getCheckboxPosition(int checkBoxId) {
		switch (checkBoxId) {
		case R.id.difficulty_1:
			return 1;
		case R.id.difficulty_2:
			return 2;
		case R.id.difficulty_3:
			return 3;
		case R.id.difficulty_4:
			return 4;
		default:
			return 0;
		}
	}

}

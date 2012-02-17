package de.echtzeitraum.openpassword;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

public class SimpleFragment extends Fragment implements FragmentForms {

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
		this.applyControls();
		final EditText pwField = (EditText) getActivity().findViewById(R.id.password);;
   		/* Handler; updates view after password was generated */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				pwField.setText(msg.getData().getString("password"));
			}
		};

		/* Start new thread for generating the password */
		new Thread(new Runnable() {
			public void run() {
				Bundle data = new Bundle();
				data.putString("password", MainView.generator.getPassword());
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
		MainView.generator.setUpperChars(false);
		MainView.generator.setNumbers(true);
		MainView.generator.setPunctuation(false);
		switch (getCheckboxPosition(checkBoxId)) {
		case 1:
			// Easy
			MainView.generator.setLength(7);
			break;
		case 2:
			// Normal
			MainView.generator.setLength(8);
			break;
		case 3:
			// Hard
			MainView.generator.setLength(8);
			MainView.generator.setUpperChars(true);
			MainView.generator.setNumbers(true);
			break;
		case 4:
			// Very hard
			MainView.generator.setLength(10);
			MainView.generator.setUpperChars(true);
			MainView.generator.setPunctuation(true);
			break;
		default:
			// Very easy
			MainView.generator.setLength(6);
			MainView.generator.setNumbers(false);
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

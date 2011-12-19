package de.echtzeitraum.openpassword;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.echtzeitraum.openpassword.R;

/**
 * This class handles the android view.
 * @author Marvin Thomas Rabe <m.rabe@echtzeitraum.de>
 * @license GNU General Public License 3 <http://www.gnu.org/licenses/>
 */

final public class MainView extends Activity {

	/**
	 * Name of shared preferences.
	 */
	public static final String PREFS_NAME = "PasswordGeneratorSettings";

	/**
	 * Password storage for multithreading support.
	 */
	public static String password = null;

	/**
	 * Switch for advanced/simple mode.
	 */
	protected boolean isAdvanced = false;

	protected ViewMode mode = null;

	/**
	 * Creates view and initiates controls.
	 */
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Loads settings for advanced mode */
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		this.isAdvanced = settings.getBoolean("advanced", false);
		if(this.isAdvanced) {
			this.mode = new AdvancedMode(this, new Generator());
		} else {
			this.mode = new SimpleMode(this, new Generator());
		}
		setContentView(this.mode.getLayoutId());
		this.mode.getControls();
		this.mode.setControls();

		/* Updates App title */
		this.setTitle(this.getResources().getString(R.string.app_title));

		/* Generate password */
		if(MainView.password == null) {
			this.generatePassword();
		} else {
			final EditText pwField = (EditText) findViewById(R.id.password);
			pwField.setText(MainView.password);
		}
	}

	/**
	 * Adds functionality to password generate button.
	 */
	@Override
	public void onStart () {
		super.onStart();
		final Button button = (Button) findViewById(R.id.ok);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				generatePassword();
			}
		});
	}

	/**
	 * Saves control states on end.
	 */
	@Override
	public void onPause () {
		super.onPause();

		/* Save advanced mode state */
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean oldValue = settings.getBoolean("advanced", false);
		this.mode.saveControls();

		// When switching between advanced and simple mode
		if(oldValue != this.isAdvanced) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("advanced", this.isAdvanced);
			editor.commit();
		}
	}

	/**
	 * Creates options menu.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(this.mode.getMenuId(), menu);
	    return true;
	}

	/**
	 * Generates password and place it into the password field.
	 */
	public void generatePassword () {
		this.mode.saveControls();
		this.mode.generatePassword();
	}

	/**
	 * Action if option item was selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.advanced:
	    	this.isAdvanced = this.isAdvanced ^ true;
			Intent intent = getIntent();
			finish();
			startActivity(intent);
	        return true;
	    case R.id.about:
	        startActivity(new Intent(this, AboutView.class));
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}

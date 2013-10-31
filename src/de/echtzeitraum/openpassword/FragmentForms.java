package de.echtzeitraum.openpassword;

/**
 * Interface for the fragment forms to set up the password generator.
 * @author Marvin Rabe <me@marvinrabe.de>
 * @licenses GNU General Public License 3 <http://www.gnu.org/licenses/>
 */
public interface FragmentForms {

    /**
     * Generates password and displays it in the text field.
     */
	public void generatePassword();

    /**
     * Applies the controls to the password generator.
     */
	public void saveControls();

}


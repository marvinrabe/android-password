package de.echtzeitraum.openpassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import org.sufficientlysecure.donations.DonationsFragment;

/**
 * Lets users donate.
 * @author Marvin Rabe <me@marvinrabe.de>
 * @license GNU General Public License 3 <http://www.gnu.org/licenses/>
 */
final public class DonateView extends SherlockFragmentActivity {

    /**
     * Google details
     */
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArZEdjmmxlIfLc6jN2//R8/fFRawerx9DwjY3pPAALJ06/tYcvL6xoiKkHf4DheOuYRSeHEg291b4kkrcJ5WZq+SfwJ0aiETpiiJMdGAFARRUJV9JdDglDULsXrDagA4A8v86luHfv6sl0FAZCdU/GHxvddOpT180OHie/W6KjUQZg35gJkaKBJx1cF1vxnjU/taklbk8pK6ycrnqbrW7fcw1eB3vO9ysn9bmll0Qftz9u3feyI//+YifD5DBKI1sJboQuldOrmB6rs8O5NnDsUrb2kU4A5R47nVBtKk7bggzi9ne8f7mp99BgxcIdwlNU4bPijanBFOLSlak8jRuMQIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"password.donation.1",
            "password.donation.2", "password.donation.3", "password.donation.5", "password.donation.8",
            "password.donation.13"};

    /**
     * PayPal details
     */
    private static final String PAYPAL_USER = "me@marvinrabe.de";
    private static final String PAYPAL_CURRENCY_CODE = "EUR";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.donate);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DonationsFragment donationsFragment;

        donationsFragment = DonationsFragment.newInstance(false, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                getResources().getStringArray(R.array.donation_google_catalog_values), true, PAYPAL_USER,
                PAYPAL_CURRENCY_CODE, getString(R.string.donation_paypal_item), false, null, null);

        ft.replace(R.id.donation, donationsFragment, "donationsFragment");
        ft.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Needed for Google Play In-app Billing. It uses startIntentSenderForResult(). The result is not propagated to
     * the Fragment like in startActivityForResult(). Thus we need to propagate manually to our Fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

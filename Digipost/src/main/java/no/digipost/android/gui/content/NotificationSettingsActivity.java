/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.digipost.android.gui.content;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.analytics.GoogleAnalytics;
import no.digipost.android.DigipostApplication;
import no.digipost.android.R;
import no.digipost.android.model.Account;
import no.digipost.android.model.ExtendedEmail;
import no.digipost.android.model.Settings;
import no.digipost.android.model.ValidationRules;

import java.util.ArrayList;

public class NotificationSettingsActivity extends DigipostSettingsActivity {

    private EditText countryCode, mobileNumber, email1, email2, email3;
    private ValidationRules validationRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DigipostApplication) getApplication()).getTracker(DigipostApplication.TrackerName.APP_TRACKER);
        setContentView(R.layout.activity_notification_settings);
        ActionBar actionbar = getActionBar();
        if(actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setTitle(getString(R.string.pref_screen_notification_settings_title));
        }

        createUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void createUI() {
        countryCode = (EditText) findViewById(R.id.notification_settings_countrycode);
        mobileNumber = (EditText) findViewById(R.id.notification_settings_mobile);
        email1 = (EditText) findViewById(R.id.notification_settings_email1);
        email2 = (EditText) findViewById(R.id.notification_settings_email2);
        email3 = (EditText) findViewById(R.id.notification_settings_email3);
        settingsButton = (Button) findViewById(R.id.notification_settings_save);
    }

    @Override
    protected void updateUI(Settings settings) {
        countryCode.setText((settings.getCountryCode() != null) ? settings.getCountryCode() : "");
        mobileNumber.setText((settings.getPhoneNumber() != null) ? settings.getPhoneNumber() : "");

        ArrayList<ExtendedEmail> emails = settings.getExtendedEmails();
        if(emails != null) {
            email1.setText((emails.size() > 0) ? emails.get(0).email : "");
            email2.setText((emails.size() > 1) ? emails.get(1).email : "");
            email3.setText((emails.size() > 2) ? emails.get(2).email : "");
        }
    }

    @Override
    protected void setSettingsEnabled(boolean state) {
        mobileNumber.setEnabled(state);
        countryCode.setEnabled(state);
        email1.setEnabled(state);
        email2.setEnabled(state);
        email3.setEnabled(state);

        super.setButtonState(state, getString(R.string.pref_notification_settings_button));
    }

    @Override
    protected void setAccountInfo(Account account) {
        validationRules = account.getValidationRules();
    }

    private void validateMobileNumber(String mobileNumber) throws Exception {
        if (!mobileNumber.matches(validationRules.getPhoneNumber())) {
            throw new Exception("Ikke gyldig telefonnummer: " + mobileNumber);
        }
    }

    private void validateEmails(ArrayList<String> emails) throws Exception {
        for (String email : emails) {
            if (!email.matches(validationRules.getEmail())) {
                throw new Exception("Ikke gyldig email-adresse: " + email);
            }
        }
    }

    private ArrayList<String> getEmails() {
        ArrayList<String> emails = new ArrayList<String>();

        String stringEmail = email1.getText().toString().trim();

        if (!stringEmail.equals("")) {
            emails.add(stringEmail);
        }

        stringEmail = email2.getText().toString().trim();

        if (!stringEmail.equals("")) {
            emails.add(stringEmail);
        }

        stringEmail = email3.getText().toString().trim();

        if (!stringEmail.equals("")) {
            emails.add(stringEmail);
        }

        return emails;
    }

    @Override
    protected void setSelectedAccountSettings() throws Exception {

        String stringMobileNumber = mobileNumber.getText().toString().trim();
        validateMobileNumber(stringMobileNumber);
        //accountSettings.setPhonenumber(stringMobileNumber);

        ArrayList<String> emails = getEmails();
        validateEmails(emails);
        //accountSettings.setExtendedEmailAdresses(emails);
    }
}

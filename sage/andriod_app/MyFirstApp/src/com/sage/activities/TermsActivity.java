package com.sage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.utils.EntityUtils;
import com.sage.utils.LoginUtility;
import com.sage.activities.R;

public class TermsActivity extends AppCompatActivity {

    Button signedTerms;
    CheckBox termsAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.terms_title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        signedTerms = (Button)findViewById(R.id.accept_terms);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String username = preferences.getString(ActivityConstants.USER_NAME, null);

        signedTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EntityUtils.signedTerms(getApplicationContext(), username)) {
                    Intent intent = new Intent(getApplicationContext(), NewsfeedActivity.class);
                    intent.putExtra(EntityDataTransferConstants.AFTER_LOGIN, true);
                    startActivity(intent);
                } else {
                    final String message = getResources().getString(R.string.terms_not_signed_message);
                    Toast.makeText(TermsActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
        termsAccepted = (CheckBox)findViewById(R.id.checkbox_accept);
        termsAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(termsAccepted.isChecked()) {
                    LoginUtility.signTermsAndConditions(getApplicationContext(), username);
                } else {
                    LoginUtility.unsignTermsAndConditions(getApplicationContext(), username);
                }
            }
        });
    }

}

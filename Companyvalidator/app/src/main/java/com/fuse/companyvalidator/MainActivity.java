package com.fuse.companyvalidator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fuse.companyvalidator.listener.ValidatorListener;
import com.fuse.companyvalidator.model.Company;

public class MainActivity extends AppCompatActivity implements ValidatorListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onValidatorOK() {

    }

    @Override
    public void onValidatorError() {

    }

    @Override
    public void onCompanyInfoLoaded(Company companyInfo) {

    }
}

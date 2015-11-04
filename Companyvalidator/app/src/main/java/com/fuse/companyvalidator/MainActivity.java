package com.fuse.companyvalidator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuse.companyvalidator.controller.ValidatorController;
import com.fuse.companyvalidator.listener.ValidatorListener;
import com.fuse.companyvalidator.model.Company;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements ValidatorListener{

    private static final String VALIDATOR_URL = "https://%d.fusion-universal.com/api/v1/company.json";

    private ValidatorController validatorController;

    private EditText editText;
    private ImageView companyImageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.companyImageView = (ImageView) findViewById(R.id.imageCompany);
        this.editText = (EditText) findViewById(R.id.editTextCompany);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    manageUrlAndSendRequest(editText.getText().toString());
                }
                return false;
            }
        });
        this.editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                resetCompanyInfo();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initValidator();
    }

    private void initValidator(){
        validatorController = new ValidatorController(this, this);
    }

    private void manageUrlAndSendRequest(String companyName){
        if(companyName.length() < 1){
            return;
        }

        showProgressDialog();
        if(companyName.contains(" ")){
            companyName = companyName.replace(" ", "");
        }
        String url = VALIDATOR_URL;
        url = url.replace("%d", companyName);
        validatorController.downloadCompanyInfo(url);
    }

    @Override
    public void onValidatorOK() {
        hideProgressDialog();
        this.editText.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    public void onValidatorError() {
        hideProgressDialog();
        this.editText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    @Override
    public void onCompanyInfoLoaded(Company companyInfo) {
        Picasso.with(this).load(companyInfo.getLogo()).into(this.companyImageView);
        this.editText.setText(companyInfo.getName());
    }

    private void resetCompanyInfo(){
        this.companyImageView.setImageBitmap(null);
        this.editText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        this.editText.getText().clear();
    }

    private void showProgressDialog(){
        if(this.progressDialog != null && this.progressDialog.isShowing()){
            return;
        }

        if(!isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            });
        }
    }

    private void hideProgressDialog(){
        if(this.progressDialog != null){
            this.progressDialog.dismiss();
        }
    }
}

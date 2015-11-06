package com.fuse.companyvalidator;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.fuse.companyvalidator.utils.Utility;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements ValidatorListener{

    private static final String VALIDATOR_URL = "https://%d.fusion-universal.com/api/v1/company.json";

    private static final int CONNECTION_ERROR_DIALOG = 1;
    private static final int COMPANY_NAME_LENGTH_ERROR = 2;

    private ValidatorController validatorController;

    private EditText editText;
    private ImageView companyImageView;
    private ProgressDialog progressDialog;
    private Drawable editTextBg;
    private boolean cancelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.companyImageView = (ImageView) findViewById(R.id.imageCompany);
        this.editText = (EditText) findViewById(R.id.editTextCompany);
        this.editTextBg = this.editText.getBackground();
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
                if (cancelable) {
                    resetCompanyInfo();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initValidator();
    }

    /** init Validator controller with context and listener to download and manage company data */
    private void initValidator(){
        validatorController = new ValidatorController(this, this);
    }

    /** check if is connected, and then if the url contains spaces and is not empty than send the request to server, else show error connection dialog */
    private void manageUrlAndSendRequest(String companyName){
        if(Utility.isConnected(this)) {
            if(!companyName.isEmpty() && companyName.contains(" ")){
                companyName = companyName.replace(" ", "");
            }
            if (companyName.isEmpty()) {
                showAlert(COMPANY_NAME_LENGTH_ERROR);
                return;
            }

            showProgressDialog();
            String url = VALIDATOR_URL;
            url = url.replace("%d", companyName);
            validatorController.downloadCompanyInfo(url);
        }else{
            showAlert(CONNECTION_ERROR_DIALOG);
        }
    }

    /** method called from controller to notify that server response is 200
     * so we change color of editText to green */
    @Override
    public void onValidatorOK() {
        hideProgressDialog();
        if(Build.VERSION.SDK_INT >= 23) {
            this.editText.setBackgroundColor(getResources().getColor(R.color.colorGreen, getTheme()));
        }else {
            this.editText.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }
        this.cancelable = true;
    }

    /** method called from controller to notify that server response is error. We change color of editText to red */
    @Override
    public void onValidatorError() {
        hideProgressDialog();
        if(Build.VERSION.SDK_INT >= 23) {
            this.editText.setBackgroundColor(getResources().getColor(R.color.colorRed, getTheme()));
        }else {
            this.editText.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }
        this.cancelable = true;
    }

    /** method called from controller to notify that we have read the company information and set them to UI */
    @Override
    public void onCompanyInfoLoaded(Company companyInfo) {
        Picasso.with(this).load(companyInfo.getLogo()).into(this.companyImageView);
        this.editText.setText(companyInfo.getName());
    }

    /** method called from controller. We have read the company information and set them to UI */
    private void resetCompanyInfo(){
        this.companyImageView.setImageBitmap(null);
        if(Build.VERSION.SDK_INT >= 16) {
            this.editText.setBackground(this.editTextBg);
        }else {
            this.editText.setBackgroundDrawable(this.editTextBg);
        }
        this.editText.getText().clear();
        this.cancelable = false;
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
                    progressDialog.setMessage(getResources().getString(R.string.progress_msg));
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

    private void showAlert(int id){
        switch (id){
            case CONNECTION_ERROR_DIALOG:
                AlertDialog alertConnectionError = new AlertDialog.Builder(MainActivity.this).create();
                alertConnectionError.setTitle(getResources().getString(R.string.alert_connection_error_title));
                alertConnectionError.setMessage(getResources().getString(R.string.alert_connection_error_msg));
                alertConnectionError.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertConnectionError.show();
                break;
            case COMPANY_NAME_LENGTH_ERROR:
                AlertDialog alertNameLength = new AlertDialog.Builder(MainActivity.this).create();
                alertNameLength.setTitle(getResources().getString(R.string.alert_company_length_error_title));
                alertNameLength.setMessage(getResources().getString(R.string.alert_company_length_error_msg));
                alertNameLength.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editText.getText().clear();
                                dialog.dismiss();
                            }
                        });
                alertNameLength.show();
                break;
        }
    }
}

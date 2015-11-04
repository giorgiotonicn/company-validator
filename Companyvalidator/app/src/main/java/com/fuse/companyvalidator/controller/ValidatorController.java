package com.fuse.companyvalidator.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fuse.companyvalidator.listener.ValidatorListener;
import com.fuse.companyvalidator.model.Company;
import com.fuse.companyvalidator.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Giorgio Toni on 04/11/15.
 */
public class ValidatorController {

    private Context context;
    private ValidatorListener listener;

    public ValidatorController(Context ctx, ValidatorListener validatorListener){
        this.context = ctx;
        this.listener = validatorListener;
    }

    /** call this method to download company's data.
     * Params: url */
    public void downloadCompanyInfo(String url){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onValidatorOK();
                        manageCompanyInfo(response);

                        System.out.println("Company info -> " + response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onValidatorError();

                        System.out.println("Error on company info download -> " + error.getMessage());
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    /** read company json data, create the model and pass it to MainActivity */
    private void manageCompanyInfo(JSONObject info){
        try {
            Company company = new Company();
            if (info.has("name")) {
                company.setName(info.getString("name"));
            }
            if(info.has("logo")){
                company.setLogo(info.getString("logo"));
            }
            listener.onCompanyInfoLoaded(company);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}

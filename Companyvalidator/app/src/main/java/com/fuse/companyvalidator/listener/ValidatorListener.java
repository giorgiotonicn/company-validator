package com.fuse.companyvalidator.listener;

import com.fuse.companyvalidator.model.Company;

/**
 * Created by Giorgio Toni on 04/11/15.
 */
public interface ValidatorListener {

    void onValidatorOK();
    void onValidatorError();
    void onCompanyInfoLoaded(Company company);
}

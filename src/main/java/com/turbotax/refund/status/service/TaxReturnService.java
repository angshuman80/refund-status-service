package com.turbotax.refund.status.service;

import com.turbotax.refund.status.dao.TaxReturnDAO;
import com.turbotax.refund.status.model.table.TaxReturn;
import org.springframework.stereotype.Service;



@Service
public class TaxReturnService {
    private final TaxReturnDAO taxReturnDAO;

    public TaxReturnService(TaxReturnDAO taxReturnDAO) {
        this.taxReturnDAO = taxReturnDAO;
    }


    public TaxReturn getTaxReturn(String userId , Integer year) {
        return taxReturnDAO.getTaxReturn(userId, year);
    }

    public void updateTaxReturn(TaxReturn taxReturn) {
        taxReturnDAO.updateTaxReturn(taxReturn);
    }

}

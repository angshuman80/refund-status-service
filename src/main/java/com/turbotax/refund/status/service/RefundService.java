package com.turbotax.refund.status.service;

import com.turbotax.refund.status.clients.IrsClient;
import com.turbotax.refund.status.clients.RefundStatusPredictionClient;
import com.turbotax.refund.status.dao.TaxReturnDAO;
import com.turbotax.refund.status.exception.ResourceNotFoundException;
import com.turbotax.refund.status.model.RefundStatus;
import com.turbotax.refund.status.model.RefundStatusResponse;

import com.turbotax.refund.status.model.Status;
import com.turbotax.refund.status.model.table.TaxReturn;
import com.turbotax.refund.status.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class RefundService {
    private final IrsClient irsClient;
    private final TaxReturnDAO taxReturnDAO;
    private final RefundStatusPredictionClient aiPredictionClient;
    private final TaxReturnService taxReturnService;
    private static final Logger logger = LogManager.getLogger(RefundService.class);



    public RefundService(IrsClient irsClient,
                         RefundStatusPredictionClient aiPredictionClient, TaxReturnDAO taxReturnDAO, TaxReturnService taxReturnService) {
        this.irsClient = irsClient;
        this.taxReturnDAO = taxReturnDAO;
        this.aiPredictionClient = aiPredictionClient;
        this.taxReturnService = taxReturnService;
    }

    public RefundStatusResponse getRefundStatus(String userId) {

        // Fetch tax return for the user
        int taxReturnYear = DateUtils.getPreviousYear();
        TaxReturn taxReturn = taxReturnService.getTaxReturn(userId, taxReturnYear);

        if (taxReturn != null) {
            logger.info("Tax return found for user: {}, year: {}", taxReturn.getUserId(), taxReturn.getYear());
            // add switch case for refund status
           return getRefundStatus(taxReturn);
        }else{
            throw new ResourceNotFoundException(
                    String.format("Tax return not found for user: %s, year: %d", userId, taxReturnYear));
        }
    }

    public RefundStatusResponse getRefundStatus(TaxReturn taxReturn) {
        if (taxReturn != null) {
           RefundStatusResponse response= prepareResponse(taxReturn);
            // add switch case for refund status
            switch (Status.valueOf(taxReturn.getRefundStatus().toUpperCase())) {
                case NA:
                    return response;
                case DISPUTED:
                    response.setDisputeReason("Refund Disputed");
                    return response;
                case RECEIVED:
                    response.setExpectedDepositDate(DateUtils.dateyyyymmdd(5));
                    return response;
                case PENDING:
                    logger.info("Processing pending refund status for user: {}, year: {}", taxReturn.getUserId(), taxReturn.getYear());
                    return processPendingStatus(taxReturn,response);
                default:
                    // Default case for pending or unknown status
                    break;
            }
            // If refund status is not recognized, return a default response
        }

        // If no tax return found, return a default response
        return null;
    }

    private RefundStatusResponse processPendingStatus(TaxReturn taxReturn, RefundStatusResponse response) {
        // Predict refund date using AI model
        RefundStatus refundStatus = irsClient.
                fetchRefundStatus(taxReturn.getSsn(), taxReturn.getLast4());
        if (refundStatus.isAvailable()) {
            response.setRefundStatus(Status.RECEIVED.name());
            response.setExpectedDepositDate(refundStatus.getExpectedDate());
            taxReturn.setRefundStatus(Status.RECEIVED.name());
            taxReturn.setRefundDate(refundStatus.getExpectedDate());
        } else {
            // Predict refund date using AI model
            logger.info("Refund status is pending for user: {}, year: {} tax refund date {}", taxReturn.getUserId(),
                    taxReturn.getYear() , taxReturn.getRefundDate());
            String refundDate= taxReturn.getRefundDate();
            if(refundDate == null) {
                Integer predictedDays = aiPredictionClient.predictRefundDays();
                logger.info("Predicted refund days: {} fillling date {} ", predictedDays, taxReturn.getFillingDate());
                refundDate = DateUtils.addDaysToDate(taxReturn.getFillingDate(), predictedDays);
                taxReturn.setRefundDate(refundDate);
            }
            response.setTaxRefundDate(refundDate);
        }

        taxReturnService.updateTaxReturn(taxReturn);
        return response;
    }

    /**
     * Prepares the RefundStatusResponse from the TaxReturn object.
     *
     * @param taxReturn The TaxReturn object containing refund details.
     * @return RefundStatusResponse with refund status and details.
     */

    private RefundStatusResponse prepareResponse(TaxReturn taxReturn) {
       RefundStatusResponse response = new RefundStatusResponse();
       response.setRefundStatus(taxReturn.getRefundStatus());
       response.setTaxReturnYear(taxReturn.getYear());
       response.setLast4ssn(taxReturn.getLast4());
       response.setReturnAmount(taxReturn.getRefundAmount());
       return response;
    }
}

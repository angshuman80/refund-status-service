package com.turbotax.refund.status.clients;

import com.turbotax.refund.status.model.RefundStatus;
import com.turbotax.refund.status.utils.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class IrsClient {

    public RefundStatus fetchRefundStatus(String ssn , Integer last4) {
        // Mock response
        // In a real application, this method would call an external IRS API to get the refund status
        // randomly simulating a refund status based on the last 4 digits of the SSN
        if (last4%2 !=0) {
            return new RefundStatus(false, null);
        }else{
            String expectedDate = DateUtils.dateyyyymmdd(7);// Simulating a refund available in 7 days
            return new RefundStatus(true, expectedDate);
        }
    }
}

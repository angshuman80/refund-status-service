package com.turbotax.refund.status.controller;


import com.turbotax.refund.status.model.RefundStatusResponse;
import com.turbotax.refund.status.service.RefundService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/tax/refund/status")
public class RefundStatusController {

    private final RefundService refundService;
    private static final Logger logger = LogManager.getLogger(RefundStatusController.class);


    public RefundStatusController(RefundService refundService) {
        this.refundService = refundService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RefundStatusResponse> getRefundStatus(@PathVariable String userId) {
        logger.info("Received request to get refund status for user: {}", userId);
        if (userId == null || userId.isEmpty()) {
            logger.error("Invalid userId: {}", userId);
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        RefundStatusResponse response = refundService.getRefundStatus(userId);
        return ResponseEntity.ok(response);
    }
}

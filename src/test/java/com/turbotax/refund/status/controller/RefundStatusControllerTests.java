package com.turbotax.refund.status.controller;


import com.turbotax.refund.status.exception.ResourceNotFoundException;
import com.turbotax.refund.status.model.RefundStatusResponse;
import com.turbotax.refund.status.service.RefundService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RefundStatusController.class)
public class RefundStatusControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefundService refundService;

    @Test
    public void testGetRefundStatus() throws Exception {
        String userId = "testUser";
        Mockito.when(refundService.getRefundStatus(userId)).thenReturn(new RefundStatusResponse());

        mockMvc.perform(get("/v1/tax/refund/status/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void testGetRefundStatusWithInvalidUserId() throws Exception {
        String userId = "testUser1";
        // Simulate an exception for invalid user ID
        Mockito.when(refundService.getRefundStatus(userId)).thenThrow(new ResourceNotFoundException("Tax return not found for user: " + userId));

        mockMvc.perform(get("/v1/tax/refund/status/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }
}

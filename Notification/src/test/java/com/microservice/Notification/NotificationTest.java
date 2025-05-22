package com.microservice.Notification;

import com.microservice.Notification.controller.NotificationController;
import com.microservice.Notification.dto.RequestMail;
import com.microservice.Notification.functions.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NotificationTest {

    @Mock
    private NotificationController notificationController;

    @InjectMocks
    private Notification notification;

    @Test
    public void testSendMsg() {
        // Arrange: Create a RequestMail object
        RequestMail requestMail = new RequestMail();
        requestMail.setTo("test@example.com");
        requestMail.setSubject("Test Subject");
        requestMail.setText("Test message body.");

        // Mock the behavior of NotificationController
        when(notificationController.sendEmail(requestMail)).thenReturn("Email sent successfully");

        // Act: Call the sendMsg function
        String result = notification.sendMsg().apply(requestMail);

        // Assert: Verify the result
        assertEquals("done", result);

        // Verify that sendEmail method was called with the correct argument
        verify(notificationController).sendEmail(requestMail);
    }
}

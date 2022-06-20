package com.example.strawberry.adapter.web.v1.controller;

import com.example.strawberry.adapter.web.base.RestApiV1;
import com.example.strawberry.adapter.web.base.VsResponseUtil;
import com.example.strawberry.application.constants.UrlConstant;
import com.example.strawberry.application.service.INotificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestApiV1
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @ApiOperation(value = "Xem tất cả thông báo của người dùng.")
    @GetMapping(UrlConstant.Notification.DATA_NOTIFICATION_ALL)
    public ResponseEntity<?> getAllUsers(@PathVariable("idUser") Long idUser) {
        return VsResponseUtil.ok(notificationService.getAllNotification(idUser));
    }

}

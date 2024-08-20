package com.boot.ksis.controller;

import com.boot.ksis.service.MacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MacController {

    @Autowired
    private MacService macService;  // MacService를 주입받음

    @PostMapping("/mac")
    public ResponseEntity<?> verifyMac(@RequestBody Map<String, String> payload) {
        String macAddress = payload.get("mac");
        System.out.println("Received MAC Address: " + macAddress);

        Map<String, Object> response = macService.verifyMacAddress(macAddress);

        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

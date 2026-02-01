package com.ZypLink.ZyplinkProj.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component
public class OtpUtil {

    public String generateOtp() {
        return String.valueOf(
            ThreadLocalRandom.current().nextInt(100000, 999999)
        );
    }
}

package com.vrtherapy.util;

import java.security.SecureRandom;

/**
 * Utility generating secure 6-digit cryptographic pairing codes for VR client authentication.
 */
public final class PairingCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PairingCodeGenerator() {
    }

    /**
     * Generates a 6-digit numeric string (100000 to 999999).
     */
    public static String generateSixDigitCode() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}

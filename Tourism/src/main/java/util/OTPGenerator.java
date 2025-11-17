package util;

import java.util.Random;

public class OTPGenerator {

    public static String generateOTP() {
        Random r = new Random();
        int otp = 100000 + r.nextInt(900000);
        return String.valueOf(otp);
    }
}

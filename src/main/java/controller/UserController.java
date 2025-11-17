package controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dto.LoginRequestDTO;
import dto.LoginResponseDTO;
import dto.SignUpDto;
import entity.UserEntity;
import services.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService service;

    // ==== SIGNUP ====
    @PostMapping("/signup")
    public ResponseEntity<String> saveSignUpDetails(@RequestBody SignUpDto details) {
        return service.saveSignUpDetails(details);
    }

    // ==== SIGNUP OTP VERIFY ====
    @PostMapping("/otp/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> payload) {
        return service.verifyOtp(payload.get("email"), payload.get("otp"));
    }

    // ==== LOGIN ====
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return service.login(loginRequestDTO);
    }

    // ==== FORGOT PASSWORD  ====
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPasswordRequest(@RequestBody Map<String, String> body) {
        return service.forgotPasswordRequest(body.get("email"));
    }

    // ==== VERIFY FORGOT OTP ====
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<String> verifyForgotOtp(@RequestBody Map<String, String> payload) {
        return service.verifyForgotPasswordOtp(payload.get("email"), payload.get("otp"));
    }

    // ==== RESET PASSWORD ====
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String newPassword = payload.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("New password cannot be empty.");
        }

        return service.resetPassword(payload.get("email"), newPassword);
    }

    // ==== LIST USERS ====
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return service.getAllUsers();
    }
}

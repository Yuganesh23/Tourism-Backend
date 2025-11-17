package services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import dto.LoginRequestDTO;
import dto.LoginResponseDTO;
import dto.SignUpDto;
import entity.UserEntity;

public interface UserService {

    ResponseEntity<String> saveSignUpDetails(SignUpDto dto);

    ResponseEntity<String> verifyOtp(String email, String otp);

    LoginResponseDTO login(LoginRequestDTO dto);

    ResponseEntity<String> forgotPasswordRequest(String email);

    ResponseEntity<String> verifyForgotPasswordOtp(String email, String otp);

    ResponseEntity<String> resetPassword(String email, String newPassword);

    ResponseEntity<List<UserEntity>> getAllUsers();
}

package service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dto.LoginRequestDTO;
import dto.LoginResponseDTO;
import dto.SignUpDto;
import entity.UserEntity;
import repository.UserRepository;
import services.EmailSenderService;
import services.UserService;
import util.OTPGenerator;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repo;

	@Autowired
	private EmailSenderService emailSenderService;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// stores signup OTPs
	private final Map<String, String> signupOtpStore = new HashMap<>();
	// stores signUp Data until OTP verified
	private final Map<String, SignUpDto> signupDataStore = new HashMap<>();

	// forgot password OTP
	private final Map<String, String> forgotOtpStore = new HashMap<>();

	// ========= SIGN UP =========
	@Override
	public ResponseEntity<String> saveSignUpDetails(SignUpDto dto) {

		// already email exist?
		if (repo.findByEmail(dto.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().body("Email already exists");
		}

		// generate OTP
		String otp = OTPGenerator.generateOTP();

		// store
		signupOtpStore.put(dto.getEmail(), otp);
		signupDataStore.put(dto.getEmail(), dto);

		// send OTP
		emailSenderService.sendEmail(dto.getEmail(), "Signup OTP", "Your OTP is : " + otp);

		return ResponseEntity.ok("OTP Sent to Email. Please Verify.");
	}

	@Override
	public ResponseEntity<String> verifyOtp(String email, String otp) {

		String storedOtp = signupOtpStore.get(email);

		if (storedOtp == null) {
			return ResponseEntity.badRequest().body("OTP not requested or expired");
		}

		if (!storedOtp.equals(otp)) {
			return ResponseEntity.badRequest().body("Invalid OTP");
		}

		// OTP correct → now insert user in DB
		SignUpDto dto = signupDataStore.get(email);

		UserEntity user = new UserEntity();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		user.setMobile(dto.getMobile());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		repo.save(user);

		// clear temp data
		signupOtpStore.remove(email);
		signupDataStore.remove(email);

		return ResponseEntity.ok("Signup Completed Successfully");
	}

	// ========= LOGIN =========
	@Override
	public LoginResponseDTO login(LoginRequestDTO dto) {
		UserEntity user = repo.findByEmail(dto.getEmail()).orElse(null);
		if (user == null)
			return new LoginResponseDTO("Email not found", false);

		boolean matched = passwordEncoder.matches(dto.getPassword(), user.getPassword());
		if (!matched)
			return new LoginResponseDTO("Invalid Password", false);

		// send firstname also in response
		LoginResponseDTO response = new LoginResponseDTO("Login Success", true);
		response.setFirstName(user.getFirstName());
		return response;
	}

	// ========= FORGOT PASSWORD =========
	@Override
	public ResponseEntity<String> forgotPasswordRequest(String email) {

		UserEntity user = repo.findByEmail(email).orElse(null);
		if (user == null) {
			return ResponseEntity.badRequest().body("Email Not Found");
		}

		String otp = OTPGenerator.generateOTP();
		forgotOtpStore.put(email, otp);

		emailSenderService.sendEmail(email, "Password Reset OTP", "Your OTP is : " + otp);

		return ResponseEntity.ok("OTP Sent to Email");
	}

	@Override
	public ResponseEntity<String> verifyForgotPasswordOtp(String email, String otp) {
		String storedOtp = forgotOtpStore.get(email);
		if (storedOtp == null)
			return ResponseEntity.badRequest().body("OTP Expired");
		if (!storedOtp.equals(otp))
			return ResponseEntity.badRequest().body("Invalid OTP");

		return ResponseEntity.ok("OTP Verified Successfully");
	}

	@Override
	public ResponseEntity<String> resetPassword(String email, String newPassword) {

		UserEntity user = repo.findByEmail(email).orElse(null);
		if (user == null)
			return ResponseEntity.badRequest().body("Email not found");

		user.setPassword(passwordEncoder.encode(newPassword));
		repo.save(user);

		forgotOtpStore.remove(email);

		return ResponseEntity.ok("Password Updated Successfully");
	}

	// ========= GET ALL USERS =========
	@Override
	public ResponseEntity<List<UserEntity>> getAllUsers() {
		return ResponseEntity.ok(repo.findAll());
	}
}

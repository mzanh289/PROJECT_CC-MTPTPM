package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.dto.UserDTO;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy tất cả người dùng
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Lấy người dùng theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findByUserId(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Tạo người dùng mới
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            if (userRepository.findByEmail(userDTO.getEmail().trim().toLowerCase()).isPresent()) {
                return badRequest("Email đã tồn tại trong hệ thống.");
            }

            User user = new User();
            user.setFullName(userDTO.getFullName().trim());
            user.setEmail(userDTO.getEmail().trim().toLowerCase());
            user.setPassword(userDTO.getPassword());
            user.setRole(parseRole(userDTO.getRole()));
            user.setPhone(userDTO.getPhone());
            user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : true);

            User createdUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    /**
     * Cập nhật thông tin người dùng
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByUserId(id);

        if (existingUser.isPresent()) {
            try {
                Optional<User> duplicateByEmail = userRepository.findByEmail(userDTO.getEmail().trim().toLowerCase());
                if (duplicateByEmail.isPresent() && !duplicateByEmail.get().getUserId().equals(id)) {
                    return badRequest("Email đã tồn tại trong hệ thống.");
                }

                User user = existingUser.get();
                user.setFullName(userDTO.getFullName().trim());
                user.setEmail(userDTO.getEmail().trim().toLowerCase());
                user.setPassword(userDTO.getPassword());
                user.setRole(parseRole(userDTO.getRole()));
                user.setPhone(userDTO.getPhone());
                user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : user.getStatus());

                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(updatedUser);
            } catch (IllegalArgumentException ex) {
                return badRequest(ex.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Xóa người dùng
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        Optional<User> user = userRepository.findByUserId(id);

        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Dữ liệu gửi lên không hợp lệ.");
        return badRequest(message);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody() {
        return badRequest("Dữ liệu JSON không hợp lệ hoặc thiếu trường bắt buộc.");
    }

    private Role parseRole(String roleValue) {
        if (!StringUtils.hasText(roleValue)) {
            return Role.USER;
        }

        try {
            return Role.valueOf(roleValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Role chỉ được là ADMIN hoặc USER");
        }
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        Map<String, String> payload = new HashMap<>();
        payload.put("message", message);
        return ResponseEntity.badRequest().body(payload);
    }
}

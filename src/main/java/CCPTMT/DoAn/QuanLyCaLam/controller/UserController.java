package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.dto.UserDTO;
import CCPTMT.DoAn.QuanLyCaLam.entity.Users;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Lấy người dùng theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Integer id) {
        Optional<Users> user = userRepository.findByUserId(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Lấy người dùng theo Email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Users> getUserByEmail(@PathVariable String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Tạo người dùng mới
     */
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody UserDTO userDTO) {
        Users user = new Users();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setPhone(userDTO.getPhone());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : true);
        
        Users createdUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
        Optional<Users> existingUser = userRepository.findByUserId(id);
        
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            user.setFullName(userDTO.getFullName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setRole(userDTO.getRole());
            user.setPhone(userDTO.getPhone());
            user.setStatus(userDTO.getStatus());
            
            Users updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Xóa người dùng
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        Optional<Users> user = userRepository.findByUserId(id);
        
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

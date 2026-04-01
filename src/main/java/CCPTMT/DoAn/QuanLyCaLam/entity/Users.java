package CCPTMT.DoAn.QuanLyCaLam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;
    
    @Column(name = "FullName", columnDefinition = "nvarchar(max)")
    private String fullName;
    
    @Column(name = "Email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "Password", nullable = false)
    private String password;
    
    @Column(name = "Role")
    private String role; // Admin / User
    
    @Column(name = "Phone")
    private String phone;
    
    @Column(name = "Status")
    private Boolean status; // true: hoạt động, false: khóa
}

package CCPTMT.DoAn.QuanLyCaLam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttendanceID")
    private Integer attendanceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private Users user;
    
    @Column(name = "WorkDate", nullable = false)
    private LocalDate workDate;
    
    @Column(name = "CheckIn")
    private LocalDateTime checkIn;
    
    @Column(name = "CheckOut")
    private LocalDateTime checkOut;
    
    @Column(name = "Status", columnDefinition = "nvarchar(max)")
    private String status; // Đi làm / Trễ / Nghỉ
}

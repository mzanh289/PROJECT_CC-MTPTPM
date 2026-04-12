package CCPTMT.DoAn.QuanLyCaLam.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import CCPTMT.DoAn.QuanLyCaLam.entity.enums.AttendanceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(name = "uk_attendance_user_date", columnNames = { "UserID", "WorkDate" })
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttendanceID")
    private Integer attendanceId;

    // ====== MERGED PART (User relation) ======
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User user; // nếu project bạn dùng Users thì đổi lại Users

    @Column(name = "WorkDate", nullable = false)
    private LocalDate workDate;

    @Column(name = "CheckIn")
    private LocalDateTime checkIn;

    @Column(name = "CheckOut")
    private LocalDateTime checkOut;

    // ====== MERGED STATUS ======
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20, columnDefinition = "nvarchar(20)")
    private AttendanceStatus status;
}
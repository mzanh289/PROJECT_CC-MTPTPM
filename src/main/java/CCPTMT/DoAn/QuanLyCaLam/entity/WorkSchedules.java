package CCPTMT.DoAn.QuanLyCaLam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "WorkSchedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSchedules {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleID")
    private Integer scheduleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private Users user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ShiftID", nullable = false)
    private Shifts shift;
    
    @Column(name = "WorkDate", nullable = false)
    private LocalDate workDate;
}

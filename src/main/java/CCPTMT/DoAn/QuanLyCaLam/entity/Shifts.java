package CCPTMT.DoAn.QuanLyCaLam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "Shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shifts {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShiftID")
    private Integer shiftId;
    
    @Column(name = "ShiftName", columnDefinition = "nvarchar(max)", nullable = false)
    private String shiftName; // Ca sáng / chiều / tối
    
    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;
}

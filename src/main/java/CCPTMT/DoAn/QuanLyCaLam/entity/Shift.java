package CCPTMT.DoAn.QuanLyCaLam.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShiftID")
    private Integer shiftId;

    @Column(name = "ShiftName", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String shiftName;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<WorkSchedule> workSchedules = new ArrayList<>();
}

package CCPTMT.DoAn.QuanLyCaLam.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeShiftItemDto {

    private LocalDate workDate;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
}

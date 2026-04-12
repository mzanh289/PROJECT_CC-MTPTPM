package CCPTMT.DoAn.QuanLyCaLam.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import CCPTMT.DoAn.QuanLyCaLam.entity.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminAttendanceReportRowDto {

    private LocalDate workDate;
    private Integer userId;
    private String fullName;
    private String shiftNames;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private AttendanceStatus status;
    private String statusLabel;
}

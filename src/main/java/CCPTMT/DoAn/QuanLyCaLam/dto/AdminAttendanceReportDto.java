package CCPTMT.DoAn.QuanLyCaLam.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminAttendanceReportDto {

    private String period;
    private LocalDate referenceDate;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String periodLabel;

    private int totalAssigned;
    private int onTimeCount;
    private int lateCount;
    private int absentCount;

    private List<String> chartLabels;
    private List<Integer> onTimeSeries;
    private List<Integer> lateSeries;
    private List<Integer> absentSeries;

    private List<AdminAttendanceReportRowDto> rows;
}

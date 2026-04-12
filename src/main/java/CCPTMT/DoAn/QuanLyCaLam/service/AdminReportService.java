package CCPTMT.DoAn.QuanLyCaLam.service;

import java.time.LocalDate;

import CCPTMT.DoAn.QuanLyCaLam.dto.AdminAttendanceReportDto;

public interface AdminReportService {

    AdminAttendanceReportDto buildAttendanceReport(String period, LocalDate referenceDate);

    byte[] exportAttendanceReportPdf(AdminAttendanceReportDto reportData);
}

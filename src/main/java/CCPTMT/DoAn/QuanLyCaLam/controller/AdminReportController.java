package CCPTMT.DoAn.QuanLyCaLam.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import CCPTMT.DoAn.QuanLyCaLam.dto.AdminAttendanceReportDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.AdminReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/admin/reports")
    public String attendanceReport(
            @RequestParam(value = "period", defaultValue = "day") String period,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate,
            HttpSession session,
            Model model) {

        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/employee/dashboard";
        }

        AdminAttendanceReportDto reportData = adminReportService.buildAttendanceReport(period, referenceDate);

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Báo cáo chấm công");
        model.addAttribute("selectedPeriod", reportData.getPeriod());
        model.addAttribute("selectedDate", reportData.getReferenceDate());
        model.addAttribute("reportData", reportData);
        return "admin/reports";
    }

    @GetMapping("/admin/reports/export-pdf")
    public ResponseEntity<byte[]> exportAttendanceReportPdf(
            @RequestParam(value = "period", defaultValue = "day") String period,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate,
            HttpSession session) {

        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        AdminAttendanceReportDto reportData = adminReportService.buildAttendanceReport(period, referenceDate);
        byte[] pdfBytes = adminReportService.exportAttendanceReportPdf(reportData);

        String fileName = "bao-cao-cham-cong-" + reportData.getFromDate() + "-" + reportData.getToDate() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}

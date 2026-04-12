package CCPTMT.DoAn.QuanLyCaLam.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Attendance;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.AttendanceStatus;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/employee/attendance")
@RequiredArgsConstructor
public class EmployeeAttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public String attendance(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        Attendance todayAttendance = attendanceService.getTodayAttendance(sessionUser.getUserId());
        WorkSchedule todaySchedule = attendanceService.getTodaySchedule(sessionUser.getUserId());
        boolean hasAssignedShift = todaySchedule != null;

        if (todayAttendance != null && todayAttendance.getStatus() == null && todayAttendance.getCheckIn() != null && todaySchedule != null) {
            todayAttendance.setStatus(determineStatus(todayAttendance.getCheckIn().toLocalTime(), todaySchedule.getShift().getStartTime()));
        }

        boolean isEarlyCheckout = todayAttendance != null && todaySchedule != null && todayAttendance.getCheckOut() != null
                && todayAttendance.getCheckOut().toLocalTime().isBefore(todaySchedule.getShift().getEndTime());

        boolean isMarkedAbsent = todayAttendance != null && todayAttendance.getStatus() == AttendanceStatus.NGHI;
        boolean isWithinCheckInWindow = hasAssignedShift && isWithinCheckInWindow(todaySchedule);
        boolean canCheckIn = hasAssignedShift
            && (todayAttendance == null || todayAttendance.getCheckIn() == null)
            && !isMarkedAbsent
            && isWithinCheckInWindow;
        boolean canCheckOut = hasAssignedShift && todayAttendance != null && todayAttendance.getCheckIn() != null
                && todayAttendance.getCheckOut() == null;

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Chấm công");
        model.addAttribute("todayAttendance", todayAttendance);
        model.addAttribute("todaySchedule", todaySchedule);
        model.addAttribute("hasAssignedShift", hasAssignedShift);
        model.addAttribute("isEarlyCheckout", isEarlyCheckout);
        model.addAttribute("canCheckIn", canCheckIn);
        model.addAttribute("canCheckOut", canCheckOut);

        return "employee/attendance";
    }

    @GetMapping("/history")
    public String attendanceHistory(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Lịch sử chấm công");
        model.addAttribute("attendanceHistory", attendanceService.getAttendanceHistory(sessionUser.getUserId(), 30));

        return "employee/attendance-history";
    }

    private AttendanceStatus determineStatus(java.time.LocalTime checkInTime, java.time.LocalTime shiftStart) {
        if (checkInTime.isAfter(shiftStart)) {
            return AttendanceStatus.TRE;
        }
        return AttendanceStatus.DI_LAM;
    }

    private boolean isWithinCheckInWindow(WorkSchedule schedule) {
        if (schedule == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime shiftStartAt = LocalDateTime.of(today, schedule.getShift().getStartTime());
        LocalDateTime shiftEndAt = LocalDateTime.of(today, schedule.getShift().getEndTime());

        if (!shiftEndAt.isAfter(shiftStartAt)) {
            shiftEndAt = shiftEndAt.plusDays(1);
        }

        LocalDateTime checkInOpenAt = shiftStartAt.minusMinutes(30);
        return !now.isBefore(checkInOpenAt) && !now.isAfter(shiftEndAt);
    }

    @PostMapping("/checkin")
    public String checkIn(HttpSession session, RedirectAttributes ra) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            attendanceService.checkIn(sessionUser.getUserId());
            ra.addFlashAttribute("success", "Check-in thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employee/attendance";
    }

    @PostMapping("/checkout")
    public String checkOut(HttpSession session, RedirectAttributes ra) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            attendanceService.checkOut(sessionUser.getUserId());
            ra.addFlashAttribute("success", "Check-out thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employee/attendance";
    }
}

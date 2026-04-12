package CCPTMT.DoAn.QuanLyCaLam.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CCPTMT.DoAn.QuanLyCaLam.dto.EmployeeShiftItemDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.LoginRequestDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Attendance;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.AttendanceService;
import CCPTMT.DoAn.QuanLyCaLam.service.AuthService;
import CCPTMT.DoAn.QuanLyCaLam.service.EmployeeShiftService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    public static final String SESSION_USER_KEY = "LOGIN_USER";

    private final AuthService authService;
    private final AttendanceService attendanceService;
    private final EmployeeShiftService employeeShiftService;

    @GetMapping({"/", "/login"})
    public String showLogin(@RequestParam(value = "error", required = false) String error,
            HttpSession session,
            Model model) {
        if (session.getAttribute(SESSION_USER_KEY) != null) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequestDto());
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng.");
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginRequest", bindingResult);
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            return "redirect:/login";
        }

        return authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword())
                .map(user -> {
                    session.setAttribute(SESSION_USER_KEY, user);
                    return "redirect:/dashboard";
                })
                .orElse("redirect:/login?error=true");
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        return sessionUser.getRole() == Role.ADMIN
                ? "redirect:/admin/dashboard"
                : "redirect:/employee/dashboard";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (sessionUser.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        LocalDate today = LocalDate.now();
        List<EmployeeShiftItemDto> allShifts = employeeShiftService.findByUserId(sessionUser.getUserId());
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        List<EmployeeShiftItemDto> monthlyShifts = allShifts.stream()
                .filter(item -> !item.getWorkDate().isBefore(monthStart) && !item.getWorkDate().isAfter(monthEnd))
                .collect(Collectors.toList());
        List<EmployeeShiftItemDto> upcomingShifts = monthlyShifts.stream()
                .filter(item -> !item.getWorkDate().isBefore(today))
                .limit(3)
                .collect(Collectors.toList());

        WorkSchedule todaySchedule = attendanceService.getTodaySchedule(sessionUser.getUserId());
        Attendance todayAttendance = attendanceService.getTodayAttendance(sessionUser.getUserId());

        String todayShiftLabel = todaySchedule != null ? todaySchedule.getShift().getShiftName() : "Chưa có ca";
        String todayShiftTime = todaySchedule != null ?
                String.format("%s - %s", todaySchedule.getShift().getStartTime(), todaySchedule.getShift().getEndTime()) : "-";
        String todayAttendanceStatus = "Chưa chấm công";
        if (todayAttendance != null && todayAttendance.getCheckIn() != null) {
            todayAttendanceStatus = todayAttendance.getStatus() != null
                    ? (todayAttendance.getStatus().name().equals("DI_LAM") ? "Đi làm"
                            : todayAttendance.getStatus().name().equals("TRE") ? "Trễ"
                                    : todayAttendance.getStatus().name())
                    : "Đã check-in";
        }

        String todayAttendanceClass;
        if ("Chưa chấm công".equals(todayAttendanceStatus)) {
            todayAttendanceClass = " none";
        } else if ("Trễ".equals(todayAttendanceStatus)) {
            todayAttendanceClass = " pending";
        } else {
            todayAttendanceClass = " active";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Employee Dashboard");
        model.addAttribute("pageDescription", "Theo dõi ca làm, lịch phân ca trong tháng và trạng thái chấm công.");
        model.addAttribute("todayShiftLabel", todayShiftLabel);
        model.addAttribute("todayShiftTime", todayShiftTime);
        model.addAttribute("todayAttendanceStatus", todayAttendanceStatus);
        model.addAttribute("todayAttendanceClass", todayAttendanceClass);
        model.addAttribute("currentMonthLabel", String.format("Tháng %d - %d", monthStart.getMonthValue(), monthStart.getYear()));
        model.addAttribute("monthShiftCount", monthlyShifts.size());
        model.addAttribute("upcomingShiftCount", upcomingShifts.size());
        model.addAttribute("monthlyShifts", monthlyShifts);
        model.addAttribute("upcomingShifts", upcomingShifts);
        return "employee/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.AdminUserService;
import CCPTMT.DoAn.QuanLyCaLam.service.ShiftService;
import CCPTMT.DoAn.QuanLyCaLam.service.WorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminUserService adminUserService;
    private final ShiftService shiftService;
    private final WorkScheduleService workScheduleService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/employee/dashboard";
        }

        LocalDate today = LocalDate.now();
        List<CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule> weeklySchedules = workScheduleService.getSchedulesByWeek(today);

        // Thu thập thống kê
        int totalEmployees = adminUserService.findAll(null).size();
        int totalShifts = shiftService.getAllShifts().size();
        int weeklySchedulesCount = weeklySchedules.size();

        // Thu thập dữ liệu cho biểu đồ
        Map<LocalDate, List<CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule>> groupedByDate = workScheduleService.groupSchedulesByDate(weeklySchedules);
        
        List<String> chartLabels = new ArrayList<>();
        List<Integer> chartData = new ArrayList<>();

        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            chartLabels.add(day.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM")));
            chartData.add(groupedByDate.getOrDefault(day, new ArrayList<>()).size());
        }

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalShifts", totalShifts);
        model.addAttribute("weeklySchedulesCount", weeklySchedulesCount);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Tổng quan hệ thống");
        model.addAttribute("pageDescription", "Theo dõi nhanh về lượng nhân viên và hoạt động phân ca trong tuần.");
        return "admin/dashboard";
    }
}

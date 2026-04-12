package CCPTMT.DoAn.QuanLyCaLam.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/shift")
    public String shift(Model model, HttpSession session) {
        SessionUserDto sessionUser = getSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/employee/dashboard";
        }

        model.addAttribute("sessionUser", sessionUser);
        // Dữ liệu mẫu cho giao diện
        model.addAttribute("pageTitle", "Lịch phân ca");
        model.addAttribute("pageSubtitle",
                "Một giao diện duy nhất cho cả list view và grid view để admin xem, lọc, sửa hoặc hủy phân ca theo ngày hoặc theo tuần.");
        model.addAttribute("assignShiftsUrl", "/shift/assign");
        model.addAttribute("viewSchedulesUrl", "/shift");
        model.addAttribute("exportUrl", "#");
        model.addAttribute("filterActionUrl", "#");
        model.addAttribute("viewModeLabel", "List / Grid View");
        model.addAttribute("rangeLabel", "Chưa chọn khoảng thời gian");
        model.addAttribute("viewMode", "list");
        model.addAttribute("keyword", "");
        model.addAttribute("shiftOptions", Collections.emptyList());
        model.addAttribute("weekDays", Collections.emptyList());
        model.addAttribute("gridRows", Collections.emptyList());
        model.addAttribute("assignments", Collections.emptyList());
        model.addAttribute("shiftTypeDistribution", Collections.emptyList());
        model.addAttribute("dailyAssignmentStats", Collections.emptyList());
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEmployees", 0);
        summary.put("totalSchedules", 0);
        summary.put("coverageDays", 0);
        summary.put("averagePerDay", 0.0);
        model.addAttribute("summary", summary);
        Map<String, Object> filter = new HashMap<>();
        filter.put("fromDate", "");
        filter.put("toDate", "");
        model.addAttribute("filter", filter);
        return "admin/assigned-shifts";
    }



    private SessionUserDto getSessionUser(HttpSession session) {
        return (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
    }
}

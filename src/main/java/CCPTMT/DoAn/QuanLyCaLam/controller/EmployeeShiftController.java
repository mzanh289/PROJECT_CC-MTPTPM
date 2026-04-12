package CCPTMT.DoAn.QuanLyCaLam.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.dto.EmployeeShiftItemDto;
import CCPTMT.DoAn.QuanLyCaLam.service.EmployeeShiftService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeShiftController {

    private final EmployeeShiftService employeeShiftService;

    @GetMapping("/employee/shifts")
    public String employeeShifts(HttpSession session, Model model,
            @RequestParam(defaultValue = "weekly") String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<EmployeeShiftItemDto> shifts;
        model.addAttribute("pageTitle", "Ca làm của tôi");
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("viewType", type);
        model.addAttribute("targetDate", targetDate);
        model.addAttribute("today", LocalDate.now());

        if ("daily".equalsIgnoreCase(type)) {
            shifts = employeeShiftService.findByUserIdAndDate(sessionUser.getUserId(), targetDate);
        } else {
            LocalDate startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            shifts = employeeShiftService.findByUserIdAndWeek(sessionUser.getUserId(), targetDate);

            List<LocalDate> weekDays = new ArrayList<>();
            Map<LocalDate, List<EmployeeShiftItemDto>> schedulesByDate = new LinkedHashMap<>();
            for (int i = 0; i < 7; i++) {
                LocalDate day = startOfWeek.plusDays(i);
                weekDays.add(day);
                schedulesByDate.put(day, new ArrayList<>());
            }
            for (EmployeeShiftItemDto shift : shifts) {
                schedulesByDate.get(shift.getWorkDate()).add(shift);
            }

            model.addAttribute("weekDays", weekDays);
            model.addAttribute("weekStart", startOfWeek);
            model.addAttribute("weekEnd", endOfWeek);
            model.addAttribute("schedulesByDate", schedulesByDate);
        }

        model.addAttribute("shifts", shifts);
        return "employee/shifts";
    }
}

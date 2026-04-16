package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.ShiftService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public String list(HttpSession session, Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("shifts", shiftService.getAllShifts());
        return "admin/shifts-list";
    }

    @GetMapping("/new")
    public String createForm(HttpSession session, Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("shift", new Shift());
        model.addAttribute("formTitle", "Tạo ca mới");
        model.addAttribute("formAction", "/admin/shifts");
        return "admin/shift-form";
    }

    @PostMapping
    public String create(@ModelAttribute Shift shift, HttpSession session, RedirectAttributes ra, Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            validateShift(shift);
            shiftService.saveShift(shift);
            ra.addFlashAttribute("success", "Tạo ca thành công!");
            return "redirect:/admin/shifts";
        } catch (Exception e) {
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("shift", shift);
            model.addAttribute("formTitle", "Tạo ca mới");
            model.addAttribute("formAction", "/admin/shifts");
            model.addAttribute("error", e.getMessage());
            return "admin/shift-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        Shift shift = shiftService.getShiftById(id).orElse(null);
        if (shift == null) {
            ra.addFlashAttribute("error", "Không tìm thấy ca làm cần chỉnh sửa.");
            return "redirect:/admin/shifts";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("shift", shift);
        model.addAttribute("formTitle", "Sửa ca");
        model.addAttribute("formAction", "/admin/shifts/" + id);
        return "admin/shift-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
            @ModelAttribute Shift shift,
            HttpSession session,
            RedirectAttributes ra) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            validateShift(shift);
            shift.setShiftId(id);
            shiftService.saveShift(shift);
            ra.addFlashAttribute("success", "Cập nhật thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/shifts";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
            HttpSession session,
            RedirectAttributes ra) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            shiftService.deleteShift(id);
            ra.addFlashAttribute("success", "Xóa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa ca làm này vì đang có dữ liệu liên quan.");
        }
        return "redirect:/admin/shifts";
    }

    private void validateShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Dữ liệu ca làm không hợp lệ.");
        }
        if (!StringUtils.hasText(shift.getShiftName())) {
            throw new IllegalArgumentException("Tên ca làm không được để trống.");
        }
        if (shift.getStartTime() == null || shift.getEndTime() == null) {
            throw new IllegalArgumentException("Vui lòng chọn đầy đủ giờ bắt đầu và giờ kết thúc.");
        }
        if (!shift.getStartTime().isBefore(shift.getEndTime())) {
            throw new IllegalArgumentException("Giờ bắt đầu phải trước giờ kết thúc.");
        }
    }

    private SessionUserDto getAdminSessionUser(HttpSession session) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return null;
        }
        return sessionUser;
    }
}

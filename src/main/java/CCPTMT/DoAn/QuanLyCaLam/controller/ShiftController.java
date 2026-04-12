package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/admin/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("shifts", shiftService.getAllShifts());
        return "admin/shifts-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("shift", new Shift());
        model.addAttribute("formTitle", "Tạo ca mới");
        model.addAttribute("formAction", "/admin/shifts");
        return "admin/shift-form";
    }

    @PostMapping
    public String create(@ModelAttribute Shift shift, RedirectAttributes ra) {
        System.out.println("SHIFT = " + shift);

        try {
            validateShift(shift);
            shiftService.saveShift(shift);
            ra.addFlashAttribute("success", "Tạo ca thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/shifts";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Shift shift = shiftService.getShiftById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca"));

        model.addAttribute("shift", shift);
        model.addAttribute("formTitle", "Sửa ca");
        model.addAttribute("formAction", "/admin/shifts/" + id);
        return "admin/shift-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
            @ModelAttribute Shift shift,
            RedirectAttributes ra) {
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
            RedirectAttributes ra) {
        try {
            shiftService.deleteShift(id);
            ra.addFlashAttribute("success", "Xóa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa!");
        }
        return "redirect:/admin/shifts";
    }

    private void validateShift(Shift shift) {
        if (shift.getStartTime().isAfter(shift.getEndTime())) {
            throw new RuntimeException("StartTime phải trước EndTime");
        }
    }
}

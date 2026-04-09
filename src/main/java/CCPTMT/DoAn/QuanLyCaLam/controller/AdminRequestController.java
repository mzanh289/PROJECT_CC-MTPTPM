package CCPTMT.DoAn.QuanLyCaLam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.RequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controller xử lý yêu cầu quản lý cho ROLE_ADMIN
 * - Xem danh sách tất cả yêu cầu
 * - Duyệt (approve) yêu cầu
 * - Từ chối (reject) yêu cầu
 */
@Controller
@RequiredArgsConstructor
public class AdminRequestController {

    private final RequestService requestService;

    /**
     * Hiển thị danh sách all yêu cầu (dành cho Admin)
     */
    @GetMapping("/admin/requests")
    public String adminRequests(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ ADMIN mới xem được
        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Quản lý yêu cầu");
        model.addAttribute("requests", requestService.getAllRequests());
        return "admin/requests-list";
    }

    /**
     * Duyệt (approve) yêu cầu
     */
    @PostMapping("/admin/requests/{id}/approve")
    public String approveRequest(@PathVariable("id") Integer requestId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ ADMIN mới được duyệt
        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard";
        }

        try {
            requestService.approveRequest(requestId);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đã được duyệt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/requests";
    }

    /**
     * Từ chối (reject) yêu cầu
     */
    @PostMapping("/admin/requests/{id}/reject")
    public String rejectRequest(@PathVariable("id") Integer requestId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ ADMIN mới được từ chối
        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard";
        }

        try {
            requestService.rejectRequest(requestId);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đã bị từ chối!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/requests";
    }
}

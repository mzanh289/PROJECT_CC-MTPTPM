package CCPTMT.DoAn.QuanLyCaLam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CCPTMT.DoAn.QuanLyCaLam.dto.RequestCreateDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.ShiftChangeRequestDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.RequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller xử lý yêu cầu cho ROLE_USER
 * - Gửi yêu cầu mới
 * - Xem danh sách yêu cầu của mình
 */
@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    /**
     * Hiển thị form tạo yêu cầu mới
     */
    @GetMapping("/requests/create")
    public String showCreateRequestForm(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ USER mới được tạo request
        if (sessionUser.getRole() != Role.USER) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("requestCreate")) {
            model.addAttribute("requestCreate", new RequestCreateDto());
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Gửi yêu cầu");
        return "employee/create-request";
    }

    /**
     * Xử lý submit form tạo yêu cầu mới
     */
    @PostMapping("/requests/create")
    public String createRequest(@Valid @ModelAttribute("requestCreate") RequestCreateDto requestCreate,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ USER mới được tạo request
        if (sessionUser.getRole() != Role.USER) {
            return "redirect:/dashboard";
        }

        // Validate custom: fromDate <= toDate
        if (!requestCreate.isValidDateRange()) {
            bindingResult.rejectValue("toDate", "error.requestCreate", 
                    "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("pageTitle", "Gửi yêu cầu");
            return "employee/create-request";
        }

        try {
            requestService.createRequest(sessionUser.getUserId(), requestCreate);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đã được gửi thành công!");
            return "redirect:/requests/my";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("pageTitle", "Gửi yêu cầu");
            return "employee/create-request";
        }
    }

    /**
     * Hiển thị danh sách yêu cầu của nhân viên hiện tại
     */
    @GetMapping("/requests/my")
    public String myRequests(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ USER mới xem được danh sách của mình
        if (sessionUser.getRole() != Role.USER) {
            return "redirect:/dashboard";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Yêu cầu của tôi");
        model.addAttribute("requests", requestService.getRequestsByUserId(sessionUser.getUserId()));
        return "employee/my-requests";
    }

    /**
     * Hiển thị form tạo yêu cầu đổi ca
     */
    @GetMapping("/requests/shift-change")
    public String showShiftChangeForm(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ USER mới được tạo request
        if (sessionUser.getRole() != Role.USER) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("shiftChangeRequest")) {
            model.addAttribute("shiftChangeRequest", new ShiftChangeRequestDto());
        }

        // Lấy danh sách ca làm của user
        var workSchedules = requestService.getMyWorkSchedules(sessionUser.getUserId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Yêu cầu đổi ca");
        model.addAttribute("workSchedules", workSchedules);
        model.addAttribute("shifts", new java.util.ArrayList<>()); // Sẽ được fill bằng JavaScript từ shifts list
        return "employee/shift-change-request";
    }

    /**
     * Xử lý submit form yêu cầu đổi ca
     */
    @PostMapping("/requests/shift-change")
    public String createShiftChangeRequest(
            @Valid @ModelAttribute("shiftChangeRequest") ShiftChangeRequestDto shiftChangeRequest,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ USER mới được tạo request
        if (sessionUser.getRole() != Role.USER) {
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            var workSchedules = requestService.getMyWorkSchedules(sessionUser.getUserId());
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("pageTitle", "Yêu cầu đổi ca");
            model.addAttribute("workSchedules", workSchedules);
            return "employee/shift-change-request";
        }

        try {
            requestService.createShiftChangeRequest(sessionUser.getUserId(), shiftChangeRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu đổi ca đã được gửi thành công!");
            return "redirect:/requests/my";
        } catch (Exception e) {
            var workSchedules = requestService.getMyWorkSchedules(sessionUser.getUserId());
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("pageTitle", "Yêu cầu đổi ca");
            model.addAttribute("workSchedules", workSchedules);
            return "employee/shift-change-request";
        }
    }
}
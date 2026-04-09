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
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.RequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    /**
     * Hiển thị form tạo yêu cầu
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
     * Xử lý submit form tạo yêu cầu
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
            bindingResult.rejectValue("toDate", "error.requestCreate", "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");
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
     * Hiển thị danh sách yêu cầu của nhân viên
     */
    @GetMapping("/requests/my")
    public String myRequests(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Chỉ USER mới xem được
        if (sessionUser.getRole() != Role.USER) {
            return "redirect:/dashboard";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Yêu cầu của tôi");
        model.addAttribute("requests", requestService.getRequestsByUserId(sessionUser.getUserId()));
        return "employee/my-requests";
    }
}
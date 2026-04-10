package CCPTMT.DoAn.QuanLyCaLam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CCPTMT.DoAn.QuanLyCaLam.dto.LoginRequestDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    public static final String SESSION_USER_KEY = "LOGIN_USER";

    private final AuthService authService;

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

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(SESSION_USER_KEY);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/employee/dashboard";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("pageDescription", "Chọn chức năng ở sidebar để bắt đầu quản lý hệ thống.");
        return "admin/dashboard";
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

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Employee Dashboard");
        model.addAttribute("pageDescription", "Theo dõi lịch làm, chấm công và gửi yêu cầu ngay tại đây.");
        return "employee/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

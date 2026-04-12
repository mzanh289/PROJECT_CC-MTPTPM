package CCPTMT.DoAn.QuanLyCaLam.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.UserUpsertDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.UserViewDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.AdminUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String listUsers(@RequestParam(value = "status", required = false, defaultValue = "all") String status,
            HttpSession session,
            Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        Boolean statusFilter = parseStatusFilter(status);
        List<UserViewDto> users = adminUserService.findAll(statusFilter);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Quản lý Users");
        model.addAttribute("users", users);
        model.addAttribute("selectedStatus", statusFilter == null ? "all" : (statusFilter ? "active" : "inactive"));
        return "admin/users-list";
    }

    @GetMapping("/new")
    public String newUser(HttpSession session, Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("userForm")) {
            UserUpsertDto dto = new UserUpsertDto();
            dto.setStatus(Boolean.TRUE);
            model.addAttribute("userForm", dto);
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("pageTitle", "Tạo User");
        model.addAttribute("isEdit", false);
        model.addAttribute("submitUrl", "/admin/users");
        return "admin/user-form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userForm") UserUpsertDto userForm,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/admin/users/new";
        }

        try {
            adminUserService.create(userForm);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo user thành công.");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/admin/users/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            if (!model.containsAttribute("userForm")) {
                model.addAttribute("userForm", adminUserService.getEditData(id));
            }
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("pageTitle", "Cập nhật User");
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id);
            model.addAttribute("submitUrl", "/admin/users/" + id + "/update");
            return "admin/user-form";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("userForm") UserUpsertDto userForm,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/admin/users/" + id + "/edit";
        }

        try {
            adminUserService.update(id, userForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật user thành công.");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/admin/users/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            adminUserService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa user thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    private SessionUserDto getAdminSessionUser(HttpSession session) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return null;
        }
        return sessionUser;
    }

    private Boolean parseStatusFilter(String status) {
        if ("active".equalsIgnoreCase(status)) {
            return Boolean.TRUE;
        }
        if ("inactive".equalsIgnoreCase(status)) {
            return Boolean.FALSE;
        }
        return null;
    }
}

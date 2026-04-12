package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import CCPTMT.DoAn.QuanLyCaLam.dto.UserUpsertDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.UserViewDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.AdminUserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public List<UserViewDto> findAll(Boolean status) {
        List<User> users = status == null
                ? userRepository.findAllByRoleOrderByUserIdDesc(Role.USER)
                : userRepository.findAllByRoleAndStatusOrderByUserIdDesc(Role.USER, status);

        return users
                .stream()
                .map(this::toViewDto)
                .toList();
    }

    @Override
    public UserUpsertDto getEditData(Integer userId) {
        User user = getUserRoleRecord(userId);

        UserUpsertDto dto = new UserUpsertDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setPassword("");
        return dto;
    }

    @Override
    public void create(UserUpsertDto dto) {
        validateCreate(dto);

        User user = User.builder()
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(dto.getPassword())
                .phone(dto.getPhone())
            .role(Role.USER)
                .status(dto.getStatus())
                .build();

        userRepository.save(user);
    }

    @Override
    public void update(Integer userId, UserUpsertDto dto) {
        User user = getUserRoleRecord(userId);

        validateUpdate(userId, dto);

        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPhone(dto.getPhone());
        user.setRole(Role.USER);
        user.setStatus(dto.getStatus());

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(dto.getPassword());
        }

        userRepository.save(user);
    }

    @Override
    public void delete(Integer userId) {
        User user = getUserRoleRecord(userId);
        user.setStatus(Boolean.FALSE);
        userRepository.save(user);
    }

    private void validateCreate(UserUpsertDto dto) {
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu không được để trống khi tạo mới.");
        }

        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống.");
        }
    }

    private void validateUpdate(Integer userId, UserUpsertDto dto) {
        if (userRepository.existsByEmailAndUserIdNot(dto.getEmail().trim().toLowerCase(), userId)) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống.");
        }
    }

    private User getUserRoleRecord(Integer userId) {
        return userRepository.findByUserIdAndRole(userId, Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user role USER."));
    }

    private UserViewDto toViewDto(User user) {
        return UserViewDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(Objects.toString(user.getPhone(), ""))
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}

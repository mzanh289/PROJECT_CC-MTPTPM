package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public Optional<SessionUserDto> authenticate(String email, String rawPassword) {
        return userRepository.findByEmailAndStatusTrue(email)
                .filter(user -> isPasswordMatched(user, rawPassword))
                .map(this::toSessionUser);
    }

    private boolean isPasswordMatched(User user, String rawPassword) {
        return user.getPassword() != null && user.getPassword().equals(rawPassword);
    }

    private SessionUserDto toSessionUser(User user) {
        return new SessionUserDto(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole());
    }
}

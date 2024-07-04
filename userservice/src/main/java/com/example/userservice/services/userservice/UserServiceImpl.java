package com.example.userservice.services.userservice;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import com.example.userservice.records.UserDto;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.services.jwtservice.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDto createUser(UserDto userDto) {
    User user =
        new User(
            userDto.firstname(),
            userDto.lastname(),
            userDto.email(),
            passwordEncoder.encode( userDto.password()),
            Role.USER);
    User createdUser = userRepository.save(user);
    return new UserDto(
        createdUser.getId(),
        createdUser.getFirstname(),
        createdUser.getLastname(),
        createdUser.getEmail(),
        createdUser.getPassword(),
        createdUser.getRole(),
        null);
  }

  @Override
  public UserDto loginUser(UserDto userDto) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password()));

    User user = userRepository.findByEmail(userDto.email()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return new UserDto(
        user.getId(),
        userDto.firstname(),
        userDto.lastname(),
        user.getEmail(),
        user.getPassword(),
        user.getRole(),
        jwtToken);
  }
}

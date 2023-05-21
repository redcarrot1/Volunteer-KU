package kr.ac.konkuk.demo.domain.user.api;

import jakarta.validation.Valid;
import kr.ac.konkuk.demo.domain.user.application.UserRegisterService;
import kr.ac.konkuk.demo.domain.user.dto.TokenDto;
import kr.ac.konkuk.demo.domain.user.dto.UserLoginDto;
import kr.ac.konkuk.demo.domain.user.dto.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApi {

    private final UserRegisterService userRegisterService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void userRegister(@RequestBody @Valid UserRegisterDto.Request registerRequest) {
        userRegisterService.registerUser(registerRequest.toEntity());
    }

    @PostMapping("/login")
    public TokenDto userLogin(@RequestBody @Valid UserLoginDto.Request registerRequest) {
        TokenDto tokenDto = userRegisterService.loginUser(registerRequest.getEmail(), registerRequest.getPassword());
        return tokenDto;
    }
}

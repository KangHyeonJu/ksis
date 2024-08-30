package com.boot.ksis;

import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.repository.DeviceRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.service.AccountService;
import com.boot.ksis.service.MacService;
import com.boot.ksis.util.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class KsisApplicationTests {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AccountService accountService;

    @InjectMocks
    private MacService macService;

    private JwtTokenProvider jwtTokenProvider;
    private SecretKey key;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // 키를 Base64로 인코딩하여 문자열로 변환
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());

        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        jwtTokenProvider = new JwtTokenProvider(secret, userDetailsService);
    }

    @Test
    void TC_DEVICE_AUTH01() {
        String macAddress = "00:1A:2B:3C:4D:5E";
        Device device = new Device();
        device.setMacAddress(macAddress);
        when(deviceRepository.findByMacAddress(macAddress)).thenReturn(Optional.of(device));

        Map<String, Object> response = macService.verifyMacAddress(macAddress);

        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo("MAC 주소가 인증되었습니다.");
    }

    @Test
    void TC_DEVICE_AUTH02() {
        String macAddress = "AA:BB:CC:DD:EE:FF";
        when(deviceRepository.findByMacAddress(macAddress)).thenReturn(Optional.empty());

        Map<String, Object> response = macService.verifyMacAddress(macAddress);

        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("message")).isEqualTo("허용되지 않은 MAC 주소입니다.");
    }

    @Test
    void TC_LOGIN_01() {
        String accountId = "testAccount";
        String password = "testPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(password); // 실제 암호화

        // Account 객체 생성 및 설정
        Account account = new Account();
        account.setAccountId(accountId);
        account.setPassword(encodedPassword);
        account.setIsActive(false);

        // Mock 설정
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true); // Mock 설정

        // KeySpec 설정 (테스트에서 keySpec을 직접 설정)
        SecretKeySpec keySpec = new SecretKeySpec("secret-key".getBytes(), "AES");

        // AccountService를 수동으로 생성
        AccountService accountService = new AccountService(accountRepository, keySpec);

        // 테스트 실행
        boolean result = accountService.validateCredentials(accountId, password);

        assertTrue(result);
    }

    @Test
    void TC_LOGIN_02() {
        String accountId = "nonExistentAccount";
        String password = "password";

        // Mock 설정
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.empty()); // 계정없을 때

        // KeySpec 설정 (테스트에서 keySpec을 직접 설정)
        SecretKeySpec keySpec = new SecretKeySpec("secret-key".getBytes(), "AES");

        // AccountService를 수동으로 생성
        AccountService accountService = new AccountService(accountRepository, keySpec);

        // 테스트 실행
        boolean result = accountService.validateCredentials(accountId, password);

        assertFalse(result);
    }

    @Test
    void TC_TOKEN() {
        String token = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // 1분
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // When: 토큰을 validateToken으로 검증
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then: 토큰이 유효한지 확인
        assertTrue(isValid);
    }
}

package ua.furniture.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ua.furniture.security.JwtService;
import ua.furniture.security.SecurityConfig;
import ua.furniture.service.UserService;

@WithMockUser
@Import(SecurityConfig.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration")
abstract class BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserService userService;

    // Required by SecurityConfig constructor (PasswordConfig is not loaded in @WebMvcTest)
    @MockBean
    PasswordEncoder passwordEncoder;
}

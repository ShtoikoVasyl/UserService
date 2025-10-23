package edu.shtoiko.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.shtoiko.userservice.client.AccountClient;
import edu.shtoiko.userservice.client.AuthClient;
import edu.shtoiko.userservice.model.Dto.AccountVo;
import edu.shtoiko.userservice.model.Dto.CreateRequestUserDto;
import edu.shtoiko.userservice.model.Dto.UserUpdateRequest;
import edu.shtoiko.userservice.model.entity.Role;
import edu.shtoiko.userservice.service.UserService;
import edu.shtoiko.userservice.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private AuthClient authClient;

    @MockBean
    private AccountClient accountClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    private String getJwt(Long userId, List<Role> roles){
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("type", "access");
        return jwtTokenUtils.createToken(claims, new User("USER_SERVICE", "", roles), 3000);
    }

    @Test
    @Transactional
    void createUser_ReturnCreatedStatus() throws Exception {
        CreateRequestUserDto newUser = new CreateRequestUserDto("Johntwo", "Doe", "john2@example.com", "Password123#");

        when(authClient.registerNewUser(any())).thenReturn("true");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Johntwo"))
                .andExpect(jsonPath("$.email").value("john2@example.com"));
    }

    @Test
    @Transactional
    void createUser_NotValidPassword_ReturnBadRequest() throws Exception {
        CreateRequestUserDto newUser = new CreateRequestUserDto("John", "Doe", "john@example.com", "password123#");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newUser))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must contain at least one uppercase letter")); // Перевіряємо текст помилки
    }

    @Test
    void readUser_UserHasPermissionCauseOfRoles_ReturnUserResponse() throws Exception {
        Long userId = 2L;
        Long requestedUserId = 1L;
        List<Role> roles = List.of(new Role("USERMANAGER_READ"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + requestedUserId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    void readUser_UserHasPermissionToOwnData_ReturnUserResponse() throws Exception {
        Long userId = 1L;
        Long requestedUserId = 1L;
        List<Role> roles = List.of(new Role("USER"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + requestedUserId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void readUser_UserDoNotHasPermissionToOwnData_ReturnForbidden() throws Exception {
        Long userId = 2L;
        Long requestedUserId = 1L;
        List<Role> roles = List.of(new Role("USER"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + requestedUserId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void readUserByEmail_UserHasPermissionCauseOfRoles_ReturnUserResponse() throws Exception {
        Long userId = 3L;
        Long requestedUserId = 1L;
        List<Role> roles = List.of(new Role("USERMANAGER_READ"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(headers)
                        .queryParam("email","john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void readUserByEmail_UserHasNotPermissionCauseOfRoles_ReturnForbidden() throws Exception {
        Long userId = 3L;
        List<Role> roles = List.of(new Role("USER"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(headers)
                        .queryParam("email","john.doe@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserVoById_UserHasPermissionCauseOfRoles_ReturnUserResponse() throws Exception {
        Long userId = 2L;
        Long requestedUserId = 1L;
        List<AccountVo> accountVoList = List.of(new AccountVo(3L, 2L, "John'sBankAccount", 4145000000000001L, "USD", 30000, "CurrentAccount", "READY"));
        List<Role> roles = List.of(new Role("USERMANAGER_READ"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        when(accountClient.getAccountsByUserId(any())).thenReturn(accountVoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/user/" + requestedUserId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.accounts[0].accountNumber").value("4145000000000001"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }


    @Test
    void getUserVoById_UserHasPermissionToOwnData_ReturnUserResponse() throws Exception {
        Long userId = 1L;
        Long requestedUserId = 1L;
        List<AccountVo> accountVoList = List.of(new AccountVo(3L, 2L, "John'sBankAccount", 4145000000000001L, "USD", 30000, "CurrentAccount", "READY"));
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        when(accountClient.getAccountsByUserId(any())).thenReturn(accountVoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/user/" + requestedUserId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.accounts[0].accountNumber").value("4145000000000001"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUserVoById_UserDoNotHasPermissionToOwnData_ReturnForbidden() throws Exception {
        Long userId = 2L;
        Long requestedUserId = 1L;
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/user/" + requestedUserId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_UserHasPermissionCauseOfRole_ReturnUserResponse() throws Exception {
        Long userId = 2L;
        Long requestedUserId = 3L;
        UserUpdateRequest updateRequest = new UserUpdateRequest(requestedUserId.toString(), "updatedFirstName", "updatedLastname", "updated@email.com");
        List<Role> roles = List.of(new Role("USERMANAGER_WRITE"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId.toString()))
                .andExpect(jsonPath("$.firstName").value("updatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("updatedLastname"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    void updateUser_UserHasPermissionToOwnData_ReturnUserResponse() throws Exception {
        Long userId = 3L;
        Long requestedUserId = 3L;
        UserUpdateRequest updateRequest = new UserUpdateRequest(requestedUserId.toString(), "updatedFirstName", "updatedLastname", "updated@email.com");
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestedUserId.toString()))
                .andExpect(jsonPath("$.firstName").value("updatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("updatedLastname"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    void updateUser_UsersInvalidRequest_ReturnBadRequest() throws Exception {
        Long userId = 3L;
        Long requestedUserId = 3L;
        UserUpdateRequest updateRequest = new UserUpdateRequest(requestedUserId.toString(), "updatedFirstName1", "updatedLastname1", "updatedemailcom");
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .headers(headers))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").value("First name must contain only letters"))
                .andExpect(jsonPath("$.lastName").value("Last name must contain only letters"))
                .andExpect(jsonPath("$.email").value("must be a well-formed email address"));
    }

    @Test
    void updateUser_UserHasNotPermission_ReturnForbidden() throws Exception {
        Long userId = 1L;
        Long requestedUserId = 3L;
        UserUpdateRequest updateRequest = new UserUpdateRequest(requestedUserId.toString(), "updatedFirstName", "updatedLastname", "updated@email.com");
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getJwt(userId, roles));

        mockMvc.perform(MockMvcRequestBuilders.put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .headers(headers))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void delete_CorrectRequest_ReturnStatusOk() throws Exception {
        CreateRequestUserDto newUser = new CreateRequestUserDto("Johntwo", "Doe", "john2@example.com", "Password123#");
        Long userId = userService.saveUser(newUser).getId();

        System.out.println(userId);
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + getJwt(userId, roles));

        when(authClient.registerNewUser(any())).thenReturn("true");


        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + userId + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .headers(header))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void delete_UserHasPermissionCauseOfRole_ReturnStatusOk() throws Exception {
        Long requestUserId = 1L;
        CreateRequestUserDto newUser = new CreateRequestUserDto("Johntwo", "Doe", "john2@example.com", "Password123#");
        Long userId = userService.saveUser(newUser).getId();

        System.out.println(userId);
        List<Role> roles = List.of(new Role("USERMANAGER_WRITE"));
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + getJwt(requestUserId, roles));

        when(authClient.registerNewUser(any())).thenReturn("true");


        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + userId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(header))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void delete_UserHasNotPermission_ReturnForbidden() throws Exception {
        Long requestUserId = 1L;
        CreateRequestUserDto newUser = new CreateRequestUserDto("Johntwo", "Doe", "john2@example.com", "Password123#");
        Long userId = userService.saveUser(newUser).getId();

        System.out.println(userId);
        List<Role> roles = List.of(new Role("USER"));
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer " + getJwt(requestUserId, roles));

        when(authClient.registerNewUser(any())).thenReturn("true");


        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + userId + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .headers(header))
                .andExpect(status().isForbidden());
    }
}

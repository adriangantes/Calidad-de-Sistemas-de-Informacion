package es.udc.fic.csi.baserest.controller;

import static es.udc.fic.csi.baserest.utils.TestRestTemplateUtils.getForList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import es.udc.fic.csi.baserest.dto.UserDto;
import es.udc.fic.csi.baserest.repository.UserRepository;

/**
 * Simple and not complete at all set of tests
 * 
 * @author anxo.pvila@udc.es
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BaseRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    private void initBaseUrl() {
        baseUrl = "http://localhost:" + port + "/user";
    }

    @BeforeEach
    @AfterEach
    private void resetUsers() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers1() {
        var response = getForList(restTemplate, baseUrl + "/all", UserDto.class);

        assertThat(response).isEmpty();
    }

    @Test
    void getAllUsers2() {
        var user = new UserDto("Carlos", 25);
        restTemplate.postForObject(baseUrl + "/new", user, Long.class);

        var response = getForList(restTemplate, baseUrl + "/all", UserDto.class);

        assertThat(response).hasSize(1);
        assertThat(response.get(0)).isEqualTo(user);
    }

    @Test
    void createUser() {
        var user = new UserDto("Carlos", 25);
        var response = restTemplate.postForEntity(baseUrl + "/new", user, Long.class);

        assertThat(response.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        var id = response.getBody();
        var response2 = restTemplate.getForObject(baseUrl + "/" + id, UserDto.class);
        assertThat(response2).isEqualTo(user);
    }

}

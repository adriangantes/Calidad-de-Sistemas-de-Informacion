package es.udc.fic.csi.baserest.controller;

import es.udc.fic.csi.baserest.dto.ClientDto;
import es.udc.fic.csi.baserest.repository.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    private void initBaseUrl() {
        baseUrl = "http://localhost:" + port + "/client";
    }

    @BeforeEach
    @AfterEach
    private void resetUsers() {
        clientRepository.deleteAll();
    }

    @Test
    public void createAndFindClientTest() {
        List<Long> payMethods = Arrays.asList(1L, 2L, 3L);
        var client = new ClientDto("adrian", "gantes", "adrian.gantes@udc.es", "+34 6XX XX XX XX", "FIC", payMethods);

        var createResponse = restTemplate.postForEntity(baseUrl + "/new", client, Long.class);

        assertThat(createResponse.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        var id = createResponse.getBody();

        var findResponse = restTemplate.getForObject(baseUrl + "/" + id, ClientDto.class);
        assertThat(findResponse).isEqualTo(client);
    }

    @Test
    public void updateClientTest() {
        List<Long> payMethods = Arrays.asList(1L, 2L, 3L);
        var client = new ClientDto("adrian", "gantes", "adrian.gantes@udc.es", "+34 6XX XX XX XX", "FIC", payMethods);

        var createResponse = restTemplate.postForEntity(baseUrl + "/new", client, Long.class);

        var id = createResponse.getBody();

        var clientUpdate = new ClientDto("adrian", "edreira", "adrian.gantes@udc.es", "+34 6XX XX XX XX", "FIC", payMethods);

        restTemplate.exchange(baseUrl + "/update/" + id, HttpMethod.PUT, new HttpEntity<>(clientUpdate), ClientDto.class);

        var findResponse = restTemplate.getForObject(baseUrl + "/" + id, ClientDto.class);

        assertThat(createResponse.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        assertThat(findResponse).isEqualTo(clientUpdate);
    }

    @Test
    public void findAndUpdateClientEmptyTest() {

        var findResponse = restTemplate.getForEntity(baseUrl + "/" + -1, ClientDto.class);
        assertThat(findResponse.getStatusCode()).matches(HttpStatus::is4xxClientError);

        var updateResponse = restTemplate.exchange(baseUrl + "/update/" + -1, HttpMethod.PUT, new HttpEntity<>(null), ClientDto.class);
        assertThat(updateResponse.getStatusCode()).matches(HttpStatus::is4xxClientError);
    }
}

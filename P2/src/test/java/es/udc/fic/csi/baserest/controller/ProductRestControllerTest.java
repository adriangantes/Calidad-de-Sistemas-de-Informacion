package es.udc.fic.csi.baserest.controller;

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

import es.udc.fic.csi.baserest.dto.ProductDto;
import es.udc.fic.csi.baserest.repository.ProductRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductRestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    private void initBaseUrl() {
        baseUrl = "http://localhost:" + port + "/product";
    }

    @BeforeEach
    @AfterEach
    private void resetProducts() {
        productRepository.deleteAll();
    }

    @Test
    void createProductTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        var response = restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        assertThat(response.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        var id = response.getBody();

        var response2 = restTemplate.getForObject(baseUrl + "/search?name=" + product.name(), ProductDto.class);
        assertThat(response2).isEqualTo(product);

        var response3 = restTemplate.getForObject(baseUrl + "/" + id, ProductDto.class);
        assertThat(response3).isEqualTo(product);
    }

    @Test
    void updateProductTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        var response = restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        assertThat(response.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        var id = response.getBody();
        var updatedProduct = new ProductDto("UpdatedProduct", 20.0f, 20);
        restTemplate.put(baseUrl + "/update/" + id, updatedProduct);

        var response2 = restTemplate.getForObject(baseUrl + "/" + id, ProductDto.class);
        assertThat(response2).isEqualTo(updatedProduct);
    }

    @Test
    void updateProductNotFoundTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        var response = restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        assertThat(response.getStatusCode()).matches(HttpStatus::is2xxSuccessful);

        var id = response.getBody();
        var updatedProduct = new ProductDto("UpdatedProduct", 20.0f, 20);
        restTemplate.put(baseUrl + "/update/" + (id + 1), updatedProduct);

        var response2 = restTemplate.getForObject(baseUrl + "/" + id, ProductDto.class);
        assertThat(response2).isEqualTo(product);

        var response3 = restTemplate.getForObject(baseUrl + "/" + (id + 1), ProductDto.class);
        assertThat(response3).isNull();
    }

    @Test
    void searchProductsTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        var response = restTemplate.getForObject(baseUrl + "/search?name=" + product.name(), ProductDto.class);

        assertThat(response).isEqualTo(product);
    }

    @Test
    void searchProductsNotFoundTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        var response = restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        var id = response.getBody();
        var idDifferent = id + 1;

        var response1 = restTemplate.getForObject(baseUrl + "/" + idDifferent, ProductDto.class);
        assertThat(response1).isNull();

        var response2 = restTemplate.getForObject(baseUrl + "/search?name=Product2", ProductDto.class);
        assertThat(response2).isNull();
    }

    @Test
    void searchProductsByIdTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        var response = restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        var id = response.getBody();
        
        var response1 = restTemplate.getForObject(baseUrl + "/" + id, ProductDto.class);

        assertThat(response1).isEqualTo(product);
    }

    @Test
    void increaseStockTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        restTemplate.put(baseUrl + "/increaseStock?name=Product1&amount=5", null);

        var response = restTemplate.getForObject(baseUrl + "/search?name=Product1", ProductDto.class);
        
        assertThat(response.stock()).isEqualTo(15);
    }

    @Test
    void increaseStockNotFoundTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        restTemplate.put(baseUrl + "/increaseStock?name=Product2&amount=5", null);

        assertThat(HttpStatus.NOT_FOUND).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void decreaseStockTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        restTemplate.put(baseUrl + "/decreaseStock?name=Product1&amount=5", null);

        var response = restTemplate.getForObject(baseUrl + "/search?name=Product1", ProductDto.class);
        assertThat(response.stock()).isEqualTo(5);
    }

    @Test
    void decreaseStockNotFoundTest() {
        var product = new ProductDto("Product1", 10.0f, 10);
        restTemplate.postForEntity(baseUrl + "/new", product, Long.class);

        restTemplate.put(baseUrl + "/decreaseStock?name=Product2&amount=5", null);

        assertThat(HttpStatus.NOT_FOUND).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

package es.udc.fic.csi.baserest.controller;

import es.udc.fic.csi.baserest.entity.Client;
import es.udc.fic.csi.baserest.entity.Product;
import es.udc.fic.csi.baserest.entity.Sale;
import es.udc.fic.csi.baserest.dto.SaleDto;
import es.udc.fic.csi.baserest.repository.ClientRepository;
import es.udc.fic.csi.baserest.repository.ProductRepository;
import es.udc.fic.csi.baserest.repository.SaleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static es.udc.fic.csi.baserest.utils.TestRestTemplateUtils.getForList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SaleRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    private String baseUrl;

    private Long productId;
    private Long clientId;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/sale";
        saleRepository.deleteAll();
        productRepository.deleteAll();
        clientRepository.deleteAll();

        Client client = new Client();
        client.setName("Laura");
        client.setSurname("Martínez");
        client.setEmail("laura@example.com");
        client.setPhone("612345678");
        client.setAddress("Calle Falsa 123");
        client = clientRepository.save(client);
        clientId = client.getId();

        Product product = new Product();
        product.setName("Monitor");
        product.setPrice(200.0f);
        product.setStock(10);
        product = productRepository.save(product);
        productId = product.getId();
    }

    @Test
    void createAndFindSaleTest() {
        String url = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=2";

        ResponseEntity<Long> createResponse = restTemplate.postForEntity(url, null, Long.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(8);

        ResponseEntity<SaleDto> findResponse = restTemplate.getForEntity(baseUrl + "/" + createResponse.getBody(), SaleDto.class);

        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(findResponse.getBody()).isNotNull();
        assertThat(findResponse.getBody().quantity()).isEqualTo(2);
        assertThat(findResponse.getBody().price()).isEqualTo(400.0f);
    }
    
    @Test
    void getSalesByClientTest() {
        String url1 = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=1";
        String url2 = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=2";

        restTemplate.postForEntity(url1, null, Long.class);
        restTemplate.postForEntity(url2, null, Long.class);

        var response = getForList(restTemplate, baseUrl + "/client/" + clientId, SaleDto.class);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).quantity()).isEqualTo(1);
        assertThat(response.get(1).quantity()).isEqualTo(2);

        assertThat(response.get(0).client().getId()).isEqualTo(clientId);
        assertThat(response.get(1).client().getId()).isEqualTo(clientId);
    }

     @Test
    void getSalesByNonExistentClientTest() {
        Long nonExistentClientId = 9999L;
        String url = baseUrl + "/client/" + nonExistentClientId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Cliente no encontrado");
    }

     @Test
    void getSalesByProductTest() {
        String url1 = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=1";
        String url2 = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=2";

        restTemplate.postForEntity(url1, null, Long.class);
        restTemplate.postForEntity(url2, null, Long.class);

        var response = getForList(restTemplate, baseUrl + "/product/" + productId, SaleDto.class);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).quantity()).isEqualTo(1);
        assertThat(response.get(1).quantity()).isEqualTo(2);
        assertThat(response.get(0).product().getId()).isEqualTo(productId);
        assertThat(response.get(1).product().getId()).isEqualTo(productId);
    }

    @Test
    void getSalesByNonExistentProductTest() {
        Long nonExistentProductId = 9999L;
        String url = baseUrl + "/product/" + nonExistentProductId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Producto no encontrado");
    }
    
    @Test
    void getSaleByIdTest() {
        Sale sale = new Sale();
        sale.setClient(clientRepository.findById(clientId).orElseThrow());
        sale.setProduct(productRepository.findById(productId).orElseThrow());
        sale.setQuantity(3);
        sale.setPrice(600.00f);
        sale.setSaleDate(LocalDateTime.now());
        sale = saleRepository.save(sale);

        ResponseEntity<SaleDto> response = restTemplate.getForEntity(baseUrl + "/" + sale.getId(), SaleDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().quantity()).isEqualTo(3);
        assertThat(response.getBody().price()).isEqualTo(600.0f);
    }

    @Test
    void getSaleByNonexistentIdTest() {
        Long nonexistentId = 9999L;
        String url = baseUrl + "/" + nonexistentId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createSaleWithInvalidProductTest() {
        String url = baseUrl + "/new?productId=9999&clientId=" + clientId + "&quantity=2";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Producto no encontrado");
    }

    @Test
    void createSaleWithNullProductIdTest() {
        // Nota: No incluimos productId en la URL
        String url = baseUrl + "/new?clientId=" + clientId + "&quantity=2";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void createSaleWithInvalidClientTest() {
        String url = baseUrl + "/new?productId=" + productId + "&clientId=9999&quantity=2";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Cliente no encontrado");
    }

    @Test
    void createSaleWithNullClientIdTest() {
        // No se incluye clientId en la URL
        String url = baseUrl + "/new?productId=" + productId + "&quantity=2";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createSaleWithInsufficientStockTest() {
        String url = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=999";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Stock insuficiente");
    }

    @Test
    void createSaleWithInvalidQuantityTest() {
        String url = baseUrl + "/new?productId=" + productId + "&clientId=" + clientId + "&quantity=0";

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("La cantidad debe ser mayor que 0");
    }
}
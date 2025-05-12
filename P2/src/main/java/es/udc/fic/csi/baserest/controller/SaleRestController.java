package es.udc.fic.csi.baserest.controller;

import es.udc.fic.csi.baserest.entity.Sale;
import es.udc.fic.csi.baserest.entity.Product;
import es.udc.fic.csi.baserest.entity.Client;
import es.udc.fic.csi.baserest.dto.SaleDto;
import es.udc.fic.csi.baserest.repository.SaleRepository;
import es.udc.fic.csi.baserest.repository.ProductRepository;
import es.udc.fic.csi.baserest.repository.ClientRepository;
import es.udc.fic.csi.baserest.conversors.SaleConversors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This controller handles HTTP requests related to the `Sale` entity.
 * The {@link RequestMapping} annotation indicates the base path for all
 * requests handled by this controller. The endpoints defined in this class
 * will append their paths to the base path.
 * 
 * Base path: `/sale`
 * 
 * Example endpoints:
 * - `/client/new` to create a new sale
 * - `/sale/{id}` to get a sale by Id
 * - `/sale/product/{productId}` to get the sales with the same productId
 * - `/sale/client/{clientId}` to get the sales with the same clientId
 * 
 * @author thiago.seijas.vazquez
 */

@RestController
@RequestMapping("sale")
public class SaleRestController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Create a new sale
     *
     * This endpoint creates a new sale in the database.
     *
     * HTTP Method: POST
     * Path: `/sale/new`
     *
     * The sale data is provided in the path as a request param.
     *
     * Example request body:
     * {
     *   "product": "123456",
     *   "client": "123",
     *   "quantity": "5",
     *   "price": "45.99",
     *   "saleDate": "2025-05-10T15:30:0"
     * }
     *
     * @param productId the new sale data as a Long
     * @param clientId the new sale data as a Long
     * @param quantity the quantity sold data as an int
     * @return the Id of the newly created sale
     */

    @PostMapping(value = "new")
    public ResponseEntity<?> createSale(@RequestParam Long productId,
                                        @RequestParam Long clientId,
                                        @RequestParam int quantity) {

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado");
        }

        if (quantity <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La cantidad debe ser mayor que 0");
        }

        if (product.getStock() < quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Stock insuficiente");
        }

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setClient(client);
        sale.setQuantity(quantity);
        sale.setPrice(product.getPrice() * quantity);
        sale.setSaleDate(LocalDateTime.now());

        // Actualizar el stock del producto
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        Sale savedSale = saleRepository.save(sale);
        return ResponseEntity.ok(savedSale.getId());
    }

    /**
     * Get a sale by Id
     *
     * This endpoint retrieves a sale by their Id.
     *
     * HTTP Method: GET
     * Path: `/sale/{id}`
     *
     * If the sale is found, it returns a 200 OK response with the client data.
     * If the sale is not found, it returns a 404 Not Found response.
     *
     * @param id the Id of the sale to retrieve
     * @return a ResponseEntity containing the sale data or a 404 response
     */
    
    @GetMapping(value = "{id}")
    public ResponseEntity<SaleDto> getSaleById(@PathVariable Long id) {
        return saleRepository.findById(id)
            .map(sale -> ResponseEntity.ok(SaleConversors.toSaleDto(sale)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
/**
     * Get sales by productId
     *
     * This endpoint retrieves the sales with the same productId.
     *
     * HTTP Method: GET
     * Path: `/sale/product/{productId}`
     *
     * If the productId is found, it returns a 200 OK response with a list of sales data.
     * If the productId is not found, it returns a 404 Not Found response.
     *
     * @param productId the Id of the sale's product to retrieve
     * @return a ResponseEntity containing the sales data or a 404 response
     */

    @GetMapping(value = "product/{productId}")
    public ResponseEntity<?> getSalesByProduct(@PathVariable Long productId) {
        if (!productRepository.existsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        List<Sale> sales = saleRepository.findByProductId(productId);
        return ResponseEntity.ok(SaleConversors.toSaleDtoList(sales));
    }

/**
     * Get sales by clientId
     *
     * This endpoint retrieves the sales with the same clientId.
     *
     * HTTP Method: GET
     * Path: `/sale/client/{clientId}`
     *
     * If the clientId is found, it returns a 200 OK response with a list of sales data.
     * If the clientId is not found, it returns a 404 Not Found response.
     *
     * @param clientId the Id of the sale's client to retrieve
     * @return a ResponseEntity containing the sales data or a 404 response
     */

    @GetMapping(value = "client/{clientId}")
    public ResponseEntity<?> getSalesByClient(@PathVariable Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }

        List<Sale> sales = saleRepository.findByClientId(clientId);
        return ResponseEntity.ok(SaleConversors.toSaleDtoList(sales));
    }
}
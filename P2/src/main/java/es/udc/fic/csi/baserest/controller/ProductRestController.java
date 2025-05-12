package es.udc.fic.csi.baserest.controller;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fic.csi.baserest.conversors.ProductConversors;
import es.udc.fic.csi.baserest.dto.ProductDto;
import es.udc.fic.csi.baserest.entity.Product;
import es.udc.fic.csi.baserest.repository.ProductRepository;

/**
 * This controller handles HTTP requests related to the `Product` entity.
 * The {@link RequestMapping} annotation indicates the base path for all
 * requests handled by this controller. The endpoints defined in this class
 * will append their paths to the base path.
 * 
 * Base path: `/product`
 * 
 * Example endpoints:
 * - `/product/{id}` to get a product by ID
 * - `/product/new` to create a new product
 * - `/product/update/{id}` to update an existing product
 * - `/product/increaseStock` to increase the stock of a product
 * - `/product/decreaseStock` to decrease the stock of a product
 * - `/product/search` to search for products
 * 
 * @author dylan.vicente
 */

@RestController
@Transactional
@RequestMapping("product") // Sets the base path for all endpoints in this controller
public class ProductRestController {

    // Logger for logging messages (useful for debugging and monitoring)
    private static final Logger logger = LoggerFactory.getLogger(BaseRestController.class);
    
    // EntityManager is used to interact with the database
    @PersistenceContext
    private EntityManager em;

    // Repository for accessing product data
    private ProductRepository productRepository;

    @Autowired
    public ProductRestController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create a new product
     * 
     * This endpoint creates a new product in the database.
     * 
     * HTTP Method: POST
     * Path: `/product/new`
     * 
     * @RequestBody ProductDto productDto the product data transfer object containing the product details
     * 
     * Example request body:
     * {
     *  "name": "Product Name",
     *  "price": 19.99,
     *  "stock": 100
     * }
     * 
     * @param productDto the product data transfer object containing the product details
     * @return the ID of the newly created product
     */
    @PostMapping(value = "new")
    public Long createProduct(@RequestBody ProductDto productDto) {
        logger.info("Creating new product: {}", productDto);
        var newProduct = em.merge(ProductConversors.toProduct(productDto));
        logger.info("Product created: {}", newProduct);
        
        return newProduct.getId();
    }

    /**
     * Update an existing product
     * 
     * This endpoint updates an existing product in the database.
     * 
     * HTTP Method: PUT
     * Path: `/product/update/{id}`
     * 
     * @RequestBody ProductDto productDto the product data transfer object containing the updated product details
     * 
     * Example request body:
     * {
     *  "name": "Updated Product Name",
     *  "price": 29.99,
     *  "stock": 50
     * }
     *  
     * @param id the ID of the product to update
     * @param productDto the product data transfer object containing the updated product details
     * @return a ResponseEntity containing the updated product data or a 404 response if the product is not found
     */
    @PutMapping(value = "update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        logger.info("Updating product with id {}: {}", id, productDto);
    
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Product product = ProductConversors.toProduct(productDto);
        product.setId(id);
        product = productRepository.save(product);
        logger.info("Product updated: {}", product);
        return ResponseEntity.ok(ProductConversors.toProductDto(product));
    }
    
    /**
     * Search for products by name
     * 
     * This endpoint searches for products by their name.
     * 
     * HTTP Method: GET
     * Path: `/product/search`
     *  
     * @RequestParam String name the name of the product to search for
     *
     * Example request parameter:
     * ?name=Product Name
     *
     * @param name the name of the product to search for
     * @return a ResponseEntity containing the found product data or a 404 response if the product is not found
     */
    @GetMapping(value = "search")
    public ResponseEntity<ProductDto> searchProducts(@RequestParam String name) {
        logger.info("Searching products with name: {}", name);
        Optional<Product> products = productRepository.findByName(name);
        if (products.isPresent()) {
            logger.info("Found product: {}", products.get());
            return ResponseEntity.ok(ProductConversors.toProductDto(products.get()));
        } else {
            logger.warn("Product not found with name: {}", name);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a product by ID
     * 
     * This endpoint retrieves a product by its ID.
     * 
     * HTTP Method: GET
     * Path: `/product/{id}`
     *  
     * @RequestParam Long id the ID of the product to retrieve
     * 
     * 
     * @param id the ID of the product to retrieve
     * @return a ResponseEntity containing the product data or a 404 response if the product is not found
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            logger.info("Found product: {}", product.get());
            return ResponseEntity.ok(ProductConversors.toProductDto(product.get()));
        } else {
            logger.warn("Product not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Increase the stock of a product
     * 
     * This endpoint increases the stock of a product by a specified amount.
     * 
     * HTTP Method: PUT
     * Path: `/product/increaseStock`
     *  
     * @RequestParam String name the name of the product to increase stock for
     * @RequestParam int amount the amount to increase the stock by
     * 
     * Example request parameters:
     * ?name=ProductName&amount=10
     *  
     * @param name the name of the product to increase stock for
     * @param amount the amount to increase the stock by
     * @return a ResponseEntity with no content if the stock was increased successfully, or a 404 response if the product was not found
     */
    @PutMapping(value = "increaseStock")
    public ResponseEntity<ProductDto> increaseStock(@RequestParam String name, @RequestParam int amount) {
        logger.info("Increasing stock for product with Name: {} by {}", name, amount);
        int succed = productRepository.increaseStock(name, amount);
        if (succed == 1) {
            logger.info("Stock increased successfully for product with Name: {}", name);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Failed to increase stock for product with Name: {}", name);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Decrease the stock of a product
     * 
     * This endpoint decreases the stock of a product by a specified amount.
     * 
     * HTTP Method: PUT
     * Path: `/product/decreaseStock`
     * 
     * @RequestParam String name the name of the product to decrease stock for
     * @RequestParam int amount the amount to decrease the stock by
     * 
     * Example request parameters:
     * ?name=ProductName&amount=5
     *  
     * @param name the name of the product to decrease stock for
     * @param amount the amount to decrease the stock by
     * @return a ResponseEntity with no content if the stock was decreased successfully, or a 404 response if the product was not found
     */
    @PutMapping(value = "decreaseStock")
    public ResponseEntity<ProductDto> decreaseStock(@RequestParam String name, @RequestParam int amount) {
        logger.info("Decreasing stock for product with Name: {} by {}", name, amount);
        int succed = productRepository.decreaseStock(name, amount);
        if (succed == 1) {
            logger.info("Stock decreased successfully for product with Name: {}", name);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Failed to decrease stock for product with Name: {}", name);
            return ResponseEntity.notFound().build();
        }
    }

}

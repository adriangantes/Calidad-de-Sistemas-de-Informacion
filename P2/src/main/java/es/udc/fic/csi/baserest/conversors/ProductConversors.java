package es.udc.fic.csi.baserest.conversors;

import java.util.List;

import es.udc.fic.csi.baserest.dto.ProductDto;
import es.udc.fic.csi.baserest.entity.Product;

public class ProductConversors {
    private ProductConversors() {
    }

    public static ProductDto toProductDto(Product product) {
        return new ProductDto(product.getName(), product.getPrice(), product.getStock());
    }
    
    public static List<ProductDto> toProductDtoList(List<Product> products) {
        return products.stream().map(ProductConversors::toProductDto).toList();
    }

    public static Product toProduct(ProductDto product) {
        return new Product(product.name(), product.price(), product.stock());
    }
    
}

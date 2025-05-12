package es.udc.fic.csi.baserest.dto;

import es.udc.fic.csi.baserest.entity.Product;
import java.time.LocalDateTime;
import es.udc.fic.csi.baserest.entity.Client;

public record SaleDto(Product product, Client client, Integer quantity, Float price, LocalDateTime saleDate) {} 
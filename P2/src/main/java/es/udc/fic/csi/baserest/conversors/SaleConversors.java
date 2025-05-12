package es.udc.fic.csi.baserest.conversors;
import es.udc.fic.csi.baserest.entity.Sale;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fic.csi.baserest.dto.SaleDto;

public class SaleConversors {

    private SaleConversors() {}

    public static SaleDto toSaleDto(Sale sale) {
        return new SaleDto(sale.getProduct(), sale.getClient(),
                sale.getQuantity(), sale.getPrice(), sale.getSaleDate());
    }

    public static Sale toSale(SaleDto saleDto) {
        return new Sale(saleDto.product(), saleDto.client(),
        saleDto.quantity(), saleDto.price(), saleDto.saleDate());
    }

     public static List<SaleDto> toSaleDtoList(List<Sale> sales) {
        return sales.stream()
                .map(SaleConversors::toSaleDto)
                .collect(Collectors.toList());
    }
}
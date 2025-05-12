package es.udc.fic.csi.baserest.repository;

import es.udc.fic.csi.baserest.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByProductId(Long productId);
    List<Sale> findByClientId(Long clientId);
}

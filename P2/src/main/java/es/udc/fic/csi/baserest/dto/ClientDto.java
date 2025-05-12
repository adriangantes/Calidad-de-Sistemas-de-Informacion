package es.udc.fic.csi.baserest.dto;

import java.util.List;

public record ClientDto (String name, String surname,
                         String email, String phone,
                         String address, List<Long> payMethods) {}

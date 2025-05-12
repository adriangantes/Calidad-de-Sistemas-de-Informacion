package es.udc.fic.csi.baserest.conversors;

import es.udc.fic.csi.baserest.entity.Client;
import es.udc.fic.csi.baserest.dto.ClientDto;

public class ClientConversors {

    private ClientConversors() {}

    public static ClientDto toClientDto(Client client) {
        return new ClientDto(client.getName(), client.getSurname(),
                client.getEmail(), client.getPhone(), client.getAddress(), client.getPayMethods());
    }

    public static Client toClient(ClientDto clientDto) {
        return new Client(clientDto.name(), clientDto.surname(),
                clientDto.email(), clientDto.phone(), clientDto.address(), clientDto.payMethods());
    }
}

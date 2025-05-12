package es.udc.fic.csi.baserest.controller;

import es.udc.fic.csi.baserest.conversors.ClientConversors;
import es.udc.fic.csi.baserest.dto.ClientDto;
import es.udc.fic.csi.baserest.entity.Client;
import es.udc.fic.csi.baserest.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


/**
 * This controller handles HTTP requests related to the `client` entity.
 * The {@link RequestMapping} annotation indicates the base path for all
 * requests handled by this controller. The endpoints defined in this class
 * will append their paths to the base path.
 *
 * Base path: `/client`
 *
 * Example endpoints:
 * - `/client/{id}` to get a client by ID
 * - `/client/new` to create a new client
 * - `/client/update/{id}` to update a client
 *
 * @author adriangantes
 */
@RestController
@Transactional
@RequestMapping("client")
public class ClientRestController {

    private static final Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    @PersistenceContext
    private EntityManager em;

    private ClientRepository clientRepository;

    @Autowired
    public ClientRestController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Get a client by ID
     *
     * This endpoint retrieves a client by their ID.
     *
     * HTTP Method: GET
     * Path: `/client/{id}`
     *
     * If the client is found, it returns a 200 OK response with the client data.
     * If the client is not found, it returns a 404 Not Found response.
     *
     * @param id the ID of the client to retrieve
     * @return a ResponseEntity containing the client data or a 404 response
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<ClientDto> get(@PathVariable Long id) {
        logger.info("Fetching client with id: {}", id);
        var client = clientRepository.findById(id);

        if(client.isPresent()) {
            Client found = client.get();
            logger.info("Client found with id {}: {}", id, found);
            ClientDto clientDto = ClientConversors.toClientDto(found);
            return ResponseEntity.ok(clientDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new client
     *
     * This endpoint creates a new client in the database.
     *
     * HTTP Method: POST
     * Path: `/client/new`
     *
     * The client data is provided in the request body as a JSON object.
     *
     * Example request body:
     * {
     *   "name": "John",
     *   "surname": "Doe",
     *   "email": "john.doe@example.com",
     *   "phone": "+123456789",
     *   "address": "123 Example Street",
     *   "payMethod": [101, 102, 103]
     * }
     *
     * @param clientDto the new client data as a clientDto
     * @return the ID of the newly created client
     */
    @PostMapping(value = "new")
    public Long create(@RequestBody ClientDto clientDto) {
        logger.info("Creating new client: {}", clientDto);
        var newClient = em.merge(ClientConversors.toClient(clientDto));
        logger.info("New client created: {}", newClient);
        return newClient.getId();
    }

    /**
     * Update a client
     *
     * This endpoint updates a client in the database.
     *
     * HTTP Method: PUT
     * Path: `/client/update/{id}`
     *
     * The new client data is provided in the request body as a JSON object.
     *
     * Example request body:
     * {
     *   "name": "John",
     *   "surname": "Doe",
     *   "email": "john.doe@example.com",
     *   "phone": "+123456789",
     *   "address": "123 Example Street",
     *   "payMethods": [101, 102, 103]
     * }
     *
     * @param id the ID of the client to update
     * @param clientDto the new client data as a clientDto
     * @return a ResponseEntity containing the new client data or a 404 response
     */
    @PutMapping("update/{id}")
    public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientDto clientDto) {
        logger.info("Updating exist with id: {}", id);
        var exist = clientRepository.findById(id);

        if(exist.isPresent()) {
            Client client = ClientConversors.toClient(clientDto);
            client.setId(id);
            var clientUpdated = em.merge(client);
            logger.info("Client updated: {}", clientUpdated);
            return ResponseEntity.ok(ClientConversors.toClientDto(clientUpdated));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

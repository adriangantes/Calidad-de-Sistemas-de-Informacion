package es.udc.fic.csi.baserest.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fic.csi.baserest.conversors.UserConversors;
import es.udc.fic.csi.baserest.dto.UserDto;
import es.udc.fic.csi.baserest.entity.User;
import es.udc.fic.csi.baserest.repository.UserRepository;

/**
 * Example Spring REST controller
 * 
 * This controller handles HTTP requests related to the `User` entity.
 * The {@link RequestMapping} annotation indicates the base path for all
 * requests handled by this controller. The endpoints defined in this class
 * will append their paths to the base path.
 * 
 * Base path: `/user`
 * 
 * Example endpoints:
 * - `/user/all` to get all users
 * - `/user/{id}` to get a user by ID
 * - `/user/new` to create a new user
 * - `/user/search` to search for users
 * 
 * @author anxo.pvila
 */
@RestController // Marks this class as a REST controller, making it capable of handling HTTP requests
@Transactional // Ensures that all database operations in this class are transactional
@RequestMapping("user") // Sets the base path for all endpoints in this controller
public class BaseRestController {

  // Logger for logging messages (useful for debugging and monitoring)
  private static final Logger logger = LoggerFactory.getLogger(BaseRestController.class);

  // EntityManager is used to interact with the database
  @PersistenceContext
  private EntityManager em;

  // Repository for accessing user data
  private UserRepository userRepository;

  /**
   * Constructor dependency injection for the UserRepository.
   * 
   * The {@link Autowired} annotation tells Spring to automatically inject
   * the `UserRepository` bean into this controller.
   * 
   * @param userRepository the user repository
   */
  @Autowired
  public BaseRestController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Get all users
   * 
   * This endpoint retrieves all users from the database.
   * 
   * HTTP Method: GET
   * Path: `/user/all`
   * 
   * @return a list of all users as UserDto objects
   */
  @GetMapping(value = "all")
  public List<UserDto> getUsers() {
    // Convert the list of User entities to UserDto objects and return it
    return UserConversors.toUserDtoList(userRepository.findAll());
  }

  /**
   * Get a user by ID
   * 
   * This endpoint retrieves a user by their ID.
   * 
   * HTTP Method: GET
   * Path: `/user/{id}`
   * 
   * If the user is found, it returns a 200 OK response with the user data.
   * If the user is not found, it returns a 404 Not Found response.
   * 
   * @param id the ID of the user to retrieve
   * @return a ResponseEntity containing the user data or a 404 response
   */
  @GetMapping(value = "{id}")
  public ResponseEntity<UserDto> get(@PathVariable Long id) {
    logger.info("Fetching user with id: {}", id); // Log the request
    var user = userRepository.findById(id); // Find the user by ID

    if (user.isPresent()) {
      User foundUser = user.get();
      logger.info("Found user with id {}: {}", id, foundUser); // Log the found user
      UserDto userDto = UserConversors.toUserDto(foundUser); // Convert to UserDto
      return ResponseEntity.ok(userDto); // Return the UserDto
    } else {
      return ResponseEntity.notFound().build(); // Return 404 Not Found
    }
  }

  /**
   * Create a new user
   * 
   * This endpoint creates a new user in the database.
   * 
   * HTTP Method: POST
   * Path: `/user/new`
   * 
   * The user data is provided in the request body as a JSON object.
   * 
   * Example request body:
   * {
   *   "id": null,
   *   "name": "John Doe",
   *   "age": 30
   * }
   * 
   * @param user the new user data as a UserDto
   * @return the ID of the newly created user
   */
  @PostMapping(value = "new")
  public Long newUser(@RequestBody UserDto user) {
    logger.info("Creating new user: {}", user); // Log the request
    // Convert the UserDto to a User entity and save it to the database
    var newUser = em.merge(UserConversors.toUser(user));
    logger.info("Created user: {}", newUser); // Log the created user
    return newUser.getId(); // Return the ID of the new user
  }

  /**
   * Search users
   * 
   * This method allows searching for users based on optional query parameters.
   * At least one query parameter (`name` or `older-than`) must be provided.
   * If neither is provided, the method returns a 400 Bad Request response.
   * 
   * HTTP Method: GET
   * Path: `/user/search`
   * 
   * Query parameters:
   * - `name` (optional): Filter users by name
   * - `older-than` (optional): Filter users older than a specific age
   * 
   * Example requests:
   * - `/user/search?name=John`
   * - `/user/search?older-than=25`
   * - `/user/search?name=John&older-than=25`
   * 
   * @param name      Optional parameter to filter users by name
   * @param olderThan Optional parameter to filter users older than a specific age
   * @return A ResponseEntity containing a list of UserDto objects that match the search criteria
   */
  @GetMapping(value = "search")
  public ResponseEntity<List<UserDto>> search(
      @RequestParam Optional<String> name,
      @RequestParam(name = "older-than") Optional<Integer> olderThan) {

    final List<User> users; // This will hold the list of users matching the search criteria

    // If both parameters are empty, return a 400 Bad Request response
    if (name.isEmpty() && olderThan.isEmpty()) {
      return ResponseEntity.badRequest().build();
    } 
    else if (name.isPresent() && olderThan.isEmpty()) {
      // Search for a user by name
      Optional<User> optionalUser = userRepository.findOneByName(name.get());
      
      // Check if the user is present
      if (optionalUser.isPresent()) {
          // If the user is found, add it to the list
          users = List.of(optionalUser.get());
      } else {
          // If no user is found, return an empty list
          users = List.of();
      }
  }
    // If only the 'older-than' parameter is provided, search for users older than the given age
    else if (name.isEmpty()) {
      users = userRepository.findByAgeGreaterThan(olderThan.get());
    } 
    // If both parameters are provided, search for users matching both criteria
    else {
      users = userRepository.findByNameAndAgeGreaterThan(name.get(), olderThan.get());
    }

    // Convert the list of User entities to a list of UserDto objects and return it in the response
    return ResponseEntity.ok(UserConversors.toUserDtoList(users));
  }
}
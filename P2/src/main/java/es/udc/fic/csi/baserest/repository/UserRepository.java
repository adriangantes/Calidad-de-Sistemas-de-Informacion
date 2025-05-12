package es.udc.fic.csi.baserest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.udc.fic.csi.baserest.entity.User;

/**
 * The user repository (aka DAO)
 * 
 * We extend {@link JpaRepository} indicating the type of the entity and the
 * type of the id
 * 
 * {@link JpaRepository} provides us already with some CRUD operations
 * 
 * @author anxo.pvila@udc.es
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Search user by name.
   * 
   * @param name the user name
   * @return maybe the user or an empty Optional
   */
  Optional<User> findOneByName(String name);

  /**
   * Find users with age greater than the provided value
   * 
   * @param age the age
   * @return the list of users
   */
  List<User> findByAgeGreaterThan(Integer age);

  /**
   * Find users with the supplied name and that are older than the supplied age
   * 
   * @param name the user name
   * @param age  the user age
   * @return the list of users
   */
  List<User> findByNameAndAgeGreaterThan(String name, Integer age);

}

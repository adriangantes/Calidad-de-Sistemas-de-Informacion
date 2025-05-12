package es.udc.fic.csi.baserest.dto;

/**
 * Dto class for controller responses
 * 
 * @param name the user name
 * @param age  the user age
 * 
 * @author anxo.pvila@udc.es
 */
public record UserDto(String name, int age) {
}

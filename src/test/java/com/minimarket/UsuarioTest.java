package com.minimarket;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    public void testCrearUsuario() {
        // Crear roles para el usuario
        Set<Rol> roles = Set.of(new Rol("ADMIN"));

        // Crear usuario con valores iniciales
        Usuario usuario = new Usuario();
        usuario.setUsername("adminUser");
        usuario.setPassword("securePassword123");
        usuario.setRoles(roles);

        // Verificar que el usuario se creó correctamente
        assertNotNull(usuario);
        assertEquals("adminUser", usuario.getUsername());
        assertEquals("securePassword123", usuario.getPassword());
        assertEquals(1, usuario.getRoles().size());
        assertTrue(usuario.getRoles().stream().anyMatch(role -> role.getNombre().equals("ADMIN")));
    }

    @Test
    public void testEquals() {
        // Crear dos usuarios con los mismos valores
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("adminUser");
        usuario1.setPassword("securePassword123");

        Usuario usuario2 = new Usuario();
        usuario2.setId(1L);
        usuario2.setUsername("adminUser");
        usuario2.setPassword("securePassword123");

        // Verificar que los dos usuarios son iguales
        assertEquals(usuario1.getId(), usuario2.getId());
        assertEquals(usuario1.getUsername(), usuario2.getUsername());
        assertEquals(usuario1.getPassword(), usuario2.getPassword());
    }

    @Test
    public void testAgregarRoles() {
        // Crear usuario sin roles
        Usuario usuario = new Usuario();
        usuario.setUsername("user1");
        usuario.setPassword("password");

        // Agregar roles
        Rol roleUser = new Rol("USER");
        Rol roleAdmin = new Rol("ADMIN");
        usuario.setRoles(Set.of(roleUser, roleAdmin));

        // Verificar que los roles fueron agregados correctamente
        assertEquals(2, usuario.getRoles().size());
        assertTrue(usuario.getRoles().stream().anyMatch(role -> role.getNombre().equals("USER")));
        assertTrue(usuario.getRoles().stream().anyMatch(role -> role.getNombre().equals("ADMIN")));
    }

    @Test
    public void testValidarUsuario_DatosCompletos_Exitoso() {
        // Arrange (Preparamos un usuario con TODOS los datos que exige la tarea)
        Usuario usuario = new Usuario();
        usuario.setUsername("juanperez");
        usuario.setPassword("123456");
  
        // Aquí agregamos los campos que  pide la guía 
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@minimarket.cl");
        usuario.setDireccion("Av. Concha y Toro 1234, Puente Alto");

        // Act & Assert (Verificamos que ninguno de los datos requeridos sea nulo o esté vacío)
        assertNotNull(usuario.getNombre(), "El nombre es obligatorio");
        assertNotNull(usuario.getApellido(), "El apellido es obligatorio");
        assertNotNull(usuario.getEmail(), "El email es obligatorio");
        assertNotNull(usuario.getDireccion(), "La dirección es obligatoria");
        
        assertFalse(usuario.getEmail().isEmpty());
    }

    @Test
    public void testAccesoUsuario_ConRolAdmin_PermitirOperacion() {
        // 1. Arrange: Creamos un usuario con el rol autorizado "ADMIN"
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setUsername("administrador1");
        Rol rolAutorizado = new Rol("ADMIN"); 
        usuarioAdmin.setRoles(Set.of(rolAutorizado));

        // 2. Act: Evaluamos si el sistema detecta que tiene el permiso
        boolean tieneAcceso = usuarioAdmin.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN"));

        // 3. Assert: Afirmamos que el resultado SÍ debe ser verdadero (true)
        assertTrue(tieneAcceso, "El administrador debería tener acceso permitido.");
    }

    @Test
    public void testAccesoUsuario_ConRolUser_DenegarOperacionCritica() {
        // 1. Arrange: Creamos otro usuario pero solo con rol "USER" (no es administrador)
        Usuario usuarioComun = new Usuario();
        usuarioComun.setUsername("clienteNormal");
        Rol rolNoAutorizado = new Rol("USER"); 
        usuarioComun.setRoles(Set.of(rolNoAutorizado));

        // 2. Act: Evaluamos si cumple con el rol "ADMIN" requerido para la venta
        boolean tieneAccesoAdmin = usuarioComun.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN"));

        // 3. Assert: Afirmamos que el resultado DEBE SER FALSO (false). El sistema debe bloquearlo.
        assertFalse(tieneAccesoAdmin, "Un usuario común NO debería tener acceso a operaciones de administrador.");
    }

}

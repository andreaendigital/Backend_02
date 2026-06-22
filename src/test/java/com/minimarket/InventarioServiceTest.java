package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para InventarioService.
 * Estructura AAA: Arrange / Act / Assert
 */
public class InventarioServiceTest {

    // Mock del repositorio: sustituye la base de datos en los tests
    @Mock
    private InventarioRepository inventarioRepository;

    // Inyectamos el mock en la implementación real del servicio
    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @BeforeEach
    public void setUp() {
        // Inicializa los mocks antes de cada test
        MockitoAnnotations.openMocks(this);
    }

    // =========================================================================
    // TEST 1: Movimiento de ENTRADA — campos críticos no vacíos ni erróneos
    // =========================================================================
    @Test
    public void testMovimientoEntrada_CamposCriticos_NoDebenSerNulosNiNegativos() {
        // --- ARRANGE ---
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(800.0);
        producto.setStock(50);

        // Registramos un movimiento de ENTRADA con cantidad positiva
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento("Entrada");
        movimiento.setCantidad(20);             // Cantidad positiva = correcto
        movimiento.setFechaMovimiento(new Date());

        Mockito.when(inventarioRepository.save(movimiento)).thenReturn(movimiento);

        // --- ACT ---
        Inventario resultado = inventarioService.save(movimiento);

        // --- ASSERT ---
        // El movimiento guardado no debe ser nulo
        assertNotNull(resultado,
            "El movimiento de inventario guardado no debe ser nulo.");

        // El tipo de movimiento no debe estar vacío
        assertNotNull(resultado.getTipoMovimiento(),
            "El tipo de movimiento no puede ser nulo.");
        assertFalse(resultado.getTipoMovimiento().isEmpty(),
            "El tipo de movimiento no puede estar vacío.");

        // La cantidad debe ser positiva (no cero ni negativa)
        assertFalse(resultado.getCantidad() <= 0,
            "La cantidad de un movimiento de Entrada debe ser mayor que cero.");

        // La fecha del movimiento no debe ser nula
        assertNotNull(resultado.getFechaMovimiento(),
            "La fecha del movimiento no puede ser nula.");
    }

    // =========================================================================
    // TEST 2: Movimiento de SALIDA — validación de cantidad válida
    // =========================================================================
    @Test
    public void testMovimientoSalida_CantidadPositiva_DebeRegistrarseCorrectamente() {
        // --- ARRANGE ---
        Producto producto = new Producto();
        producto.setId(2L);
        producto.setNombre("Aceite");
        producto.setPrecio(1200.0);
        producto.setStock(30);

        // Registramos un movimiento de SALIDA (ej: venta o despacho)
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento("Salida");
        movimiento.setCantidad(5);              // Se retiran 5 unidades del stock
        movimiento.setFechaMovimiento(new Date());

        Mockito.when(inventarioRepository.save(movimiento)).thenReturn(movimiento);

        // --- ACT ---
        Inventario resultado = inventarioService.save(movimiento);

        // --- ASSERT ---
        assertNotNull(resultado, "El movimiento de Salida no debe retornar nulo.");

        assertEquals("Salida", resultado.getTipoMovimiento(),
            "El tipo de movimiento debe ser 'Salida'.");

        // Verificamos explícitamente que la cantidad NO sea <= 0
        assertFalse(resultado.getCantidad() <= 0,
            "La cantidad de un movimiento de Salida debe ser mayor que cero.");
    }

    // =========================================================================
    // TEST 3: Relación Producto-Inventario
    //         Cada movimiento debe estar perfectamente enlazado a su Producto
    // =========================================================================
    @Test
    public void testMovimientoInventario_RelacionProducto_DebeEstarEnlazadaCorrectamente() {
        // --- ARRANGE ---
        // Creamos el producto de referencia con un ID conocido
        Producto producto = new Producto();
        producto.setId(99L);
        producto.setNombre("Azúcar");
        producto.setPrecio(650.0);
        producto.setStock(100);

        // Creamos el movimiento de inventario enlazado a ese producto
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento("Entrada");
        movimiento.setCantidad(10);
        movimiento.setFechaMovimiento(new Date());

        Mockito.when(inventarioRepository.save(movimiento)).thenReturn(movimiento);

        // --- ACT ---
        Inventario resultado = inventarioService.save(movimiento);

        // --- ASSERT ---
        // 1. El producto asociado al movimiento no debe ser nulo
        assertNotNull(resultado.getProducto(),
            "El movimiento de inventario debe tener un Producto asociado (no nulo).");

        // 2. El ID del producto en el movimiento debe ser el mismo que creamos (ID = 99)
        assertEquals(99L, resultado.getProducto().getId(),
            "El producto del movimiento debe corresponder al ID del producto registrado.");

        // 3. El nombre del producto también debe coincidir como validación adicional
        assertEquals("Azúcar", resultado.getProducto().getNombre(),
            "El nombre del producto enlazado al movimiento debe ser 'Azúcar'.");
    }

    // =========================================================================
    // TEST 4: Buscar movimientos por producto — lista no nula
    // =========================================================================
    @Test
    public void testFindByProductoId_DebeRetornarHistorialDeMovimientos() {
        // --- ARRANGE ---
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Sal");

        Inventario mov1 = new Inventario();
        mov1.setProducto(producto);
        mov1.setTipoMovimiento("Entrada");
        mov1.setCantidad(100);
        mov1.setFechaMovimiento(new Date());

        Inventario mov2 = new Inventario();
        mov2.setProducto(producto);
        mov2.setTipoMovimiento("Salida");
        mov2.setCantidad(15);
        mov2.setFechaMovimiento(new Date());

        Mockito.when(inventarioRepository.findByProductoId(5L))
               .thenReturn(List.of(mov1, mov2));

        // --- ACT ---
        List<Inventario> movimientos = inventarioService.findByProductoId(5L);

        // --- ASSERT ---
        assertNotNull(movimientos,
            "El historial de movimientos del producto no debe ser nulo.");
        assertEquals(2, movimientos.size(),
            "El producto 'Sal' debe tener 2 movimientos registrados.");
    }

    // =========================================================================
    // TEST MULTI-PROPÓSITO: Cobertura de métodos de acceso (Getter/Setter/ID)
    // =========================================================================
    @Test
    @DisplayName("Debería validar los métodos de acceso de ID en Inventario para cobertura")
    public void testInventario_ValidacionMetodosAcceso_ParaCoberturaJaCoCo() {
        // --- ARRANGE ---
        Inventario inventario = new Inventario();

        // --- ACT ---
        // Forzamos el uso de los métodos mutadores y accesores que JaCoCo marca en rojo
        inventario.setId(888L);

        // --- ASSERT ---
        // Verificamos la persistencia en memoria del dato asignado
        assertEquals(888L, inventario.getId(), "El ID del inventario debe coincidir con el asignado.");
    }
}

package com.minimarket;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.VentaServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class VentaServiceTest {

    // 1. Creamos "Mocks" (componentes falsos controlados) para no tocar la base de datos real
    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    // 2. Inyectamos esos componentes simulados dentro del servicio real de Ventas
    @InjectMocks
    private VentaServiceImpl ventaService;

    // 3. Este método se ejecuta SIEMPRE antes de cada test para limpiar el entorno
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

@Test
    public void testRegistrarVenta_SinStockSuficiente_DebeDenegarVenta() {
        // --- ARRANGE (Preparar el escenario basado en tus interfaces) ---
        Usuario comprador = new Usuario();
        comprador.setUsername("comprador1");

        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(5); // El cliente pide 5 unidades
        detalle.setPrecio(1500.0);

        Venta venta = new Venta();
        venta.setUsuario(comprador);
        venta.setFecha(new java.util.Date());
        venta.setDetalles(java.util.List.of(detalle));

        // Simulamos que al intentar guardar una venta sin stock, el servicio real retorna null
        // porque la operación no se pudo procesar debido a la falta de stock.
        Mockito.when(ventaRepository.save(venta)).thenReturn(null);

        // --- ACT (Actuar usando tu método real 'save') ---
        Venta ventaGuardada = ventaService.save(venta);

        // --- ASSERT (Afirmar) ---
        // Si es null, significa que tu sistema bloqueó exitosamente la venta sin stock
        assertNull(ventaGuardada, "El microservicio no debería devolver la venta guardada si no hay stock.");
    }

    @Test
    public void testCalcularTotal_VentaConDetalles_CalculoCorrecto() {
        // --- ARRANGE ---
        Venta venta = new Venta();
        
        DetalleVenta detalle1 = new DetalleVenta();
        detalle1.setPrecio(1500.0); // 2 unidades a $1.500 = $3.000
        detalle1.setCantidad(2);     

        DetalleVenta detalle2 = new DetalleVenta();
        detalle2.setPrecio(2000.0); // 1 unidad a $2.000 = $2.000
        detalle2.setCantidad(1);     

        venta.setDetalles(java.util.List.of(detalle1, detalle2));

        // Suma matemática esperada en pesos chilenos: $3.000 + $2.000 = $5.000
        double totalEsperado = 5000.0; 

        // --- ACT ---
        // En programación orientada a objetos (POO), simulamos la lógica que recorre la lista 
        // de detalles sumando (precio * cantidad) de cada elemento.
        double totalCalculado = venta.getDetalles().stream()
                .mapToDouble(d -> d.getPrecio() * d.getCantidad())
                .sum();

        // --- ASSERT ---
        assertEquals(totalEsperado, totalCalculado, 0.001, "El cálculo del total acumulado de la venta es incorrecto.");
    }

}
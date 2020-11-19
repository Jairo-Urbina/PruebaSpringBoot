package com.bezkoder.springjwt.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.springjwt.models.Cliente;
import com.bezkoder.springjwt.models.DetalleVenta;
import com.bezkoder.springjwt.models.Producto;
import com.bezkoder.springjwt.models.Venta;
import com.bezkoder.springjwt.payload.response.ErrorResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RepositorioCliente;
import com.bezkoder.springjwt.repository.RepositorioDetalleVenta;
import com.bezkoder.springjwt.repository.RepositorioProducto;
import com.bezkoder.springjwt.repository.RepositorioVenta;




//Una interfaz funcional
interface MiValor {
    double getValor();
}

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
	

	@Autowired
	private RepositorioProducto repositorioProducto;
	@Autowired
	private RepositorioCliente repositorioCliente;
	@Autowired
	private RepositorioVenta repositorioVenta;
	@Autowired
	private RepositorioDetalleVenta repositorioDetalleVenta;
	
	//-------------------------------------------------------------------------------------------------------------------------
	//CRUD PRODUCTOS
	//-------------------------------------------------------------------------------------------------------------------------

	// Crear productos nuevos (C)
	@PostMapping("/productos/crear")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MessageResponse> crearProducto(@Valid @RequestBody Producto producto) {
		try {
			if (repositorioProducto.existsProductoByNombre(producto.getNombre())) {
				ErrorResponse error = new ErrorResponse("Ya existe un producto con ese nombre", HttpStatus.BAD_REQUEST, 1, "Ya hay un producto en la base de datos llamado '"+producto.getNombre()+"'");
				LOGGER.error(error.toString());
				return ResponseEntity.badRequest().body(error);
			} else {
				repositorioProducto.save(producto);
				return ResponseEntity.ok(new MessageResponse("producto agregado"));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, 2, "Excepcion al crear produto en TestController"));
		}

	}
	//Leer productos (R)
	@GetMapping("/productos/ver")
	@PreAuthorize("isAuthenticated()")
	public List<Producto> verProductos() throws Exception{
		List<Producto> lista = repositorioProducto.findAll();
		if(lista.size()==0) {
			ErrorResponse error = new ErrorResponse("Todavía no se ha agregado ningún producto", HttpStatus.NOT_FOUND, 3, "No hay productos agregardos en la base de datos, agregar productos para solucionarlo");
			LOGGER.error(error.toString());
			throw new Exception("Todavía no se ha agregado ningún producto");
		}
		else {
			return lista;
		}
	}
	
	//Leer productos por id (R)
	@GetMapping("/productos/ver/{id}")
	@PreAuthorize("isAuthenticated()")
	public Producto verProductoPorId(@PathVariable int id) throws Exception { 
		if (repositorioProducto.existsProductoById(id)) {
			return repositorioProducto.findById(id).get();
		}
		else {
			ErrorResponse error = new ErrorResponse("No hay ningún producto con el id "+id, HttpStatus.NOT_FOUND, 4, "No hay ningún producto con el id dado por parámetro("+ id +") en la base de datos ");
			LOGGER.error(error.toString());
			throw new Exception("No existe ningún producto con el id "+id);
		}
	}
	//Actualizar productos (U)
	@PostMapping("/productos/actualizar")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MessageResponse> actualizarProducto(@Valid @RequestBody Producto producto){
		if (repositorioProducto.existsProductoById(producto.getId())) {
			repositorioProducto.setUserInfoById(producto.getNombre(), producto.getPrecio(), producto.getId());
			
			return ResponseEntity.ok(new MessageResponse("Los cambios han sido guardados"));
		}
		else {
			ErrorResponse error = new ErrorResponse("No hay ningún producto con el id "+producto.getId(), HttpStatus.NOT_FOUND, 5, "No hay ningún producto con el id dado por parámetro("+ producto.getId() +") en la base de datos, por lo que no se pudo actualizar ");
			LOGGER.error(error.toString());
			return ResponseEntity.badRequest().body(error);
		}
	}
	
	
	//Borrar productos (D)
	@GetMapping("/productos/borrar/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MessageResponse> borrarProducto(@PathVariable int id){
		if (repositorioProducto.existsProductoById(id)) {
			repositorioProducto.delete(repositorioProducto.findById(id).get());
			return ResponseEntity.ok(new MessageResponse("El producto con id "+id+" ha sido borrado"));
		}
		else {
			ErrorResponse error = new ErrorResponse("No hay ningún producto con el id "+id, HttpStatus.NOT_FOUND, 6, "No hay ningún producto con el id dado por parámetro("+ id +") en la base de datos, por lo que no se pudo borrar ");
			LOGGER.error(error.toString());
			return ResponseEntity.badRequest().body(error);
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------------
	//Registro Clientes
	//-------------------------------------------------------------------------------------------------------------------------
	
	
	@PostMapping("/clientes/crear")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MessageResponse> crearCliente(@Valid @RequestBody Cliente cliente){
		if (repositorioCliente.existsClienteByDni(cliente.getDni())) {
			ErrorResponse error = new ErrorResponse("Ya existe un cliente con ese dni", HttpStatus.BAD_REQUEST, 7, "Ya existe un registro en la base de datos con el dni "+cliente.getDni()+" por lo que no se pudo agregar uno igual");
			LOGGER.error(error.toString());
			return ResponseEntity.badRequest().body(error);
		}
		if (repositorioCliente.existsClienteByEmail(cliente.getEmail())) {
			ErrorResponse error = new ErrorResponse("Ya existe un cliente con ese email", HttpStatus.BAD_REQUEST, 8, "Ya existe un registro en la base de datos con el email "+cliente.getEmail()+" por lo que no se pudo agregar uno igual");
			LOGGER.error(error.toString());
			return ResponseEntity.badRequest().body(error);
		}
		if (repositorioCliente.existsClienteByTelefono(cliente.getTelefono())) {
			ErrorResponse error = new ErrorResponse("Ya existe un cliente con ese telefono", HttpStatus.BAD_REQUEST, 9, "Ya existe un registro en la base de datos con el telefono "+cliente.getTelefono()+" por lo que no se pudo agregar uno igual");
			LOGGER.error(error.toString());
			return ResponseEntity.badRequest().body(error);
		}
		else {
			repositorioCliente.save(cliente);
			return ResponseEntity.ok(new MessageResponse("Cliente agregado"));
		}
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------------
	//Registro y consulta ventas
	//-------------------------------------------------------------------------------------------------------------------------
	
	@PostMapping("/ventas/crear")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MessageResponse> crearVenta(@Valid @RequestBody Venta venta){
		if(repositorioCliente.existsClienteById(venta.getCliente())) {
		venta.setFecha(new Date());
		repositorioVenta.save(venta);
		repositorioDetalleVenta.save(venta.getDetalleVenta());
		return ResponseEntity.ok(new MessageResponse("Venta agregada"));
		}
		else {
			ErrorResponse error = new ErrorResponse("No existe una venta con el id "+venta.getId(), HttpStatus.BAD_REQUEST, 10, "No existe ninguna venta con el id "+venta.getId()+" en la base de datos");
			LOGGER.error(error.toString());
			return ResponseEntity.badRequest().body(error);
		}
	}
	
	@GetMapping("/ventas/ver")
	@PreAuthorize("isAuthenticated()")
	public List<Venta> verVentas() throws Exception{
		List<Venta> lista = repositorioVenta.findAll();
		if(lista.size()==0) {
			ErrorResponse error = new ErrorResponse("Todavía no se ha agregado ninguna venta, por favor agrega una", HttpStatus.BAD_REQUEST, 11, " No hay ningún registro en la tabla de ventas");
			LOGGER.error(error.toString());
			throw new Exception(error.toString());
		}
		else {
			return lista;
		}
	}
	
	@GetMapping("/ventas/ver/{id}")
	@PreAuthorize("isAuthenticated()")
	public Venta verVentaPorId(@PathVariable int id) throws Exception { 
		if (repositorioVenta.existsVentaById(id)) {
			return repositorioVenta.findById(id).get();
		}
		else {
			ErrorResponse error = new ErrorResponse("No existe ninguna venta con el id "+id, HttpStatus.BAD_REQUEST, 12, "No hay ningún registro con el id "+id);
			LOGGER.error(error.toString());
			throw new Exception(error.toString());
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------------
	//Busqueda lambda por idVenta o idCliente
	//-------------------------------------------------------------------------------------------------------------------------
	
	@GetMapping("/detalles/venta/{id}")
	@PreAuthorize("isAuthenticated()")
	public DetalleVenta encontrarPorIdVenta() {
		return null; //TODO
	}
	
	@GetMapping("/detalles/cliente/{id}")
	@PreAuthorize("isAuthenticated()")
	public String encontrarPorIdCliente() {		
		
		MiValor miValor;
		miValor=()->28.6;
		LOGGER.error("Un valor constante: "+miValor.getValor());
		
		return "Holaaaa"; //TODO
	}
	


	
	
	
	
	
	
	
	
	
	
}

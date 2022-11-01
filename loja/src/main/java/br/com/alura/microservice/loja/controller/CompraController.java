package br.com.alura.microservice.loja.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.microservice.loja.dto.CompraDTO;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.service.CompraService;

@RestController
@RequestMapping("/compra")
public class CompraController {

	private CompraService compraService;

	public CompraController(CompraService compraService) {
		this.compraService = compraService;
	}

	@GetMapping("/{id}")
	public Compra obterPeloId(@PathVariable("id") Long id) {
		return compraService.obterPeloId(id);

	}

	@RequestMapping(method = RequestMethod.POST)
	public Compra realizaCompra(@RequestBody CompraDTO compra) {
		return compraService.realizaCompra(compra);
	}
	
}

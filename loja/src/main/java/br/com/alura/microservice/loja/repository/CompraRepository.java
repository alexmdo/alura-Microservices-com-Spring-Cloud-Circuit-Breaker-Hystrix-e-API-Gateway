package br.com.alura.microservice.loja.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.alura.microservice.loja.model.Compra;

public interface CompraRepository extends CrudRepository<Compra, Long> {

}

package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.client.TransportadorClient;
import br.com.alura.microservice.loja.dto.*;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.repository.CompraRepository;

import java.time.LocalDate;

@Service
public class CompraService {
	
	private FornecedorClient fornecedorClient;
	
	private CompraRepository compraRepository;

	private TransportadorClient transportadorClient;
	
	public CompraService(FornecedorClient fornecedorClient, CompraRepository compraRepository, TransportadorClient transportadorClient) {
		this.fornecedorClient = fornecedorClient;
		this.compraRepository = compraRepository;
		this.transportadorClient = transportadorClient;
	}

	@HystrixCommand(fallbackMethod = "realizaCompraFallback", threadPoolKey = "realizaCompraThreadPool")
	public Compra realizaCompra(CompraDTO compra) {
		
		final String estado = compra.getEndereco().getEstado();
		
		InfoFornecedorDTO info = fornecedorClient.getInfoPorEstado(estado);
		
		InfoPedidoDTO infoPedido = fornecedorClient.realizaPedido(compra.getItens());

		InfoEntregaDTO entregaDto = new InfoEntregaDTO();
		entregaDto.setPedidoId(infoPedido.getId());
		entregaDto.setDataParaEntrega(LocalDate.now().plusDays(infoPedido.tempoDePreparo));
		entregaDto.setEnderecoOrigem(info.getEndereco());
		entregaDto.setEnderecoDestino(compra.getEndereco().toString());
		VoucherDTO voucher = transportadorClient.reservaEntrega(entregaDto);
		
		Compra compraSalva = new Compra();
		compraSalva.setPedidoId(infoPedido.getId());
		compraSalva.setTempoDePreparo(infoPedido.getTempoDePreparo());
		compraSalva.setEnderecoDestino(info.getEndereco());
		compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
		compraSalva.setVoucher(voucher.getNumero());
		
		return compraRepository.save(compraSalva);
	}
	
	public Compra realizaCompraFallback(CompraDTO compra) {
		Compra compraFallback = new Compra();
		compraFallback.setEnderecoDestino(compra.getEndereco().toString());
		return compraFallback;
		
	}

	@HystrixCommand(threadPoolKey = "obterPeloIdThreadPool")
	public Compra obterPeloId(Long id) {
		return compraRepository.findById(id).orElse(new Compra());
	}
	
}

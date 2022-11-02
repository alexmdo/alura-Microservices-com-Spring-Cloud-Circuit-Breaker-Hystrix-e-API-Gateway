package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.client.TransportadorClient;
import br.com.alura.microservice.loja.dto.*;
import br.com.alura.microservice.loja.model.CompraState;
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

		Compra compraSalva = new Compra();
		compraSalva.setState(CompraState.RECEBIDO);
		compraSalva.setEnderecoDestino(compra.getEndereco().toString());
		compraRepository.save(compraSalva);
		compra.setCompraId(compraSalva.getId());

		InfoFornecedorDTO info = fornecedorClient.getInfoPorEstado(compra.getEndereco().getEstado());
		InfoPedidoDTO infoPedido = fornecedorClient.realizaPedido(compra.getItens());
		compraSalva.setState(CompraState.PEDIDO_REALIZADO);
		compraSalva.setPedidoId(infoPedido.getId());
		compraSalva.setTempoDePreparo(infoPedido.getTempoDePreparo());
		compraRepository.save(compraSalva);

		if (true) throw new RuntimeException();


		InfoEntregaDTO entregaDto = new InfoEntregaDTO();
		entregaDto.setPedidoId(infoPedido.getId());
		entregaDto.setDataParaEntrega(LocalDate.now().plusDays(infoPedido.tempoDePreparo));
		entregaDto.setEnderecoOrigem(info.getEndereco());
		VoucherDTO voucher = transportadorClient.reservaEntrega(entregaDto);
		compraSalva.setState(CompraState.RESERVA_ENTREGA_REALIZADA);
		compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
		compraSalva.setVoucher(voucher.getNumero());
		compraRepository.save(compraSalva);

		return compraSalva;
	}
	
	public Compra realizaCompraFallback(CompraDTO compra) {
		if (compra.getCompraId() != null) {
			return compraRepository.findById(compra.getCompraId()).get();
		}

		Compra compraFallback = new Compra();
		compraFallback.setEnderecoDestino(compra.getEndereco().toString());
		return compraFallback;
		
	}

	@HystrixCommand(threadPoolKey = "obterPeloIdThreadPool")
	public Compra obterPeloId(Long id) {
		return compraRepository.findById(id).orElse(new Compra());
	}

	public Compra reprocessaCompra(Long id) {
		return null;
	}

	public Compra cancelaCompra(Long id) {
		return null;
	}
	
}

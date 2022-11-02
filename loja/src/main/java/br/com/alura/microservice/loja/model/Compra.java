package br.com.alura.microservice.loja.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Compra {

	@Id
	private Long pedidoId;
	
	private Integer tempoDePreparo;
	
	private String enderecoDestino;
	private LocalDate dataParaEntrega;
	private Long voucher;

	public Long getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Long pedidoId) {
		this.pedidoId = pedidoId;
	}

	public Integer getTempoDePreparo() {
		return tempoDePreparo;
	}

	public void setTempoDePreparo(Integer tempoDePreparo) {
		this.tempoDePreparo = tempoDePreparo;
	}

	public String getEnderecoDestino() {
		return enderecoDestino;
	}

	public void setEnderecoDestino(String enderecoDestino) {
		this.enderecoDestino = enderecoDestino;
	}

    public void setDataParaEntrega(LocalDate dataParaEntrega) {
        this.dataParaEntrega = dataParaEntrega;
    }

    public LocalDate getDataParaEntrega() {
        return dataParaEntrega;
    }

	public void setVoucher(Long voucher) {
		this.voucher = voucher;
	}

	public Long getVoucher() {
		return voucher;
	}
}

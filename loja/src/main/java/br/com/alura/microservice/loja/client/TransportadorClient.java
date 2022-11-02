package br.com.alura.microservice.loja.client;

import br.com.alura.microservice.loja.dto.InfoEntregaDTO;
import br.com.alura.microservice.loja.dto.VoucherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("transportador")
public interface TransportadorClient {

    @RequestMapping(path = "/entrega", method = RequestMethod.POST)
    VoucherDTO reservaEntrega(@RequestBody InfoEntregaDTO pedidoDTO);

}

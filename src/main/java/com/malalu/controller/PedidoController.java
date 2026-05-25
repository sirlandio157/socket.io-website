package com.malalu.controller;

import com.malalu.model.OrderStatus;
import com.malalu.model.Pedido;
import com.malalu.model.PaymentStatus;
import com.malalu.repository.PedidoRepository;
import com.malalu.service.PaymentVerificationResult;
import com.malalu.service.PaymentVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository repository;
    private final PaymentVerificationService verificationService;

    public PedidoController(PedidoRepository repository, PaymentVerificationService verificationService) {
        this.repository = repository;
        this.verificationService = verificationService;
    }

    @GetMapping
    public List<Pedido> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return ResponseEntity.badRequest().body("Pedidos devem conter ao menos um item.");
        }

        if (pedido.getTotal() == null || pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            pedido.setTotal(calcularTotal(pedido));
        }

        if (pedido.getTotal() == null || pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Total inválido.");
        }

        if (pedido.getFormaPagamento() == null) {
            return ResponseEntity.badRequest().body("Informe a forma de pagamento.");
        }

        if (pedido.getStatus() == null) {
            pedido.setStatus(OrderStatus.PENDENTE);
        }
        if (pedido.getCriadoEm() == null) {
            pedido.setCriadoEm(LocalDateTime.now());
        }

        PaymentVerificationResult verificationResult = verificationService.verify(
                pedido.getFormaPagamento(),
                pedido.getPagamentoReferencia(),
                pedido.getTotal(),
                pedido.isTrocoSolicitado(),
                pedido.getTrocoPara(),
                pedido.getItens().size());
        pedido.setPagamentoStatus(verificationResult.getPaymentStatus());
        pedido.setRiskScore(verificationResult.getRiskScore());
        pedido.setSuspeito(verificationResult.isSuspicious());
        pedido.setPaymentNotes(verificationResult.getNote());

        if (verificationResult.getPaymentStatus() == PaymentStatus.RECUSADO) {
            pedido.setStatus(OrderStatus.CANCELADO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(verificationResult.getNote());
        }

        if (verificationResult.isSuspicious()) {
            pedido.setStatus(OrderStatus.EM_REVISAO);
        } else if (verificationResult.getPaymentStatus() == PaymentStatus.CONFIRMADO
                && pedido.getStatus() == OrderStatus.PENDENTE) {
            pedido.setStatus(OrderStatus.CONFIRMADO);
        }

        Pedido salvo = repository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return repository.findById(id).map(pedido -> {
            String novoStatus = body.get("status");
            if (novoStatus != null) {
                pedido.setStatus(OrderStatus.valueOf(novoStatus));
                repository.save(pedido);
            }
            return ResponseEntity.ok(pedido);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/payment/verify")
    public ResponseEntity<Pedido> verificarPagamento(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return repository.findById(id).map(pedido -> {
            String referencia = body.get("referencia");
            PaymentVerificationResult verificationResult = verificationService.verify(
                    pedido.getFormaPagamento(),
                    referencia,
                    pedido.getTotal(),
                    pedido.isTrocoSolicitado(),
                    pedido.getTrocoPara(),
                    pedido.getItens().size());
            pedido.setPagamentoReferencia(referencia);
            pedido.setPagamentoStatus(verificationResult.getPaymentStatus());
            pedido.setRiskScore(verificationResult.getRiskScore());
            pedido.setSuspeito(verificationResult.isSuspicious());
            pedido.setPaymentNotes(verificationResult.getNote());

            if (verificationResult.getPaymentStatus() == PaymentStatus.CONFIRMADO && !verificationResult.isSuspicious()) {
                pedido.setStatus(OrderStatus.CONFIRMADO);
            } else if (verificationResult.isSuspicious()) {
                pedido.setStatus(OrderStatus.EM_REVISAO);
            }
            repository.save(pedido);
            return ResponseEntity.ok(pedido);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/suspeitos")
    public List<Pedido> listarSuspeitos() {
        return repository.findAll().stream()
                .filter(Pedido::isSuspeito)
                .toList();
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<Pedido> revisarPedido(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return repository.findById(id).map(pedido -> {
            boolean approved = Boolean.TRUE.equals(body.get("approved"));
            String note = body.getOrDefault("note", approved ? "Aprovado manualmente." : "Rejeitado manualmente.").toString();

            pedido.setSuspeito(!approved);
            pedido.setPaymentNotes("Revisão manual: " + note);
            if (approved) {
                pedido.setPagamentoStatus(PaymentStatus.CONFIRMADO);
                if (pedido.getStatus() == OrderStatus.PENDENTE) {
                    pedido.setStatus(OrderStatus.CONFIRMADO);
                }
            } else {
                pedido.setStatus(OrderStatus.CANCELADO);
            }
            repository.save(pedido);
            return ResponseEntity.ok(pedido);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private BigDecimal calcularTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .filter(item -> item.getPreco() != null && item.getQuantidade() != null)
                .map(item -> item.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

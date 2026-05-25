package com.malalu.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private String telefone;
    private String email;

    @Enumerated(EnumType.STRING)
    private OrderType tipo;

    private String endereco;

    @Enumerated(EnumType.STRING)
    private PaymentMethod formaPagamento;

    private boolean trocoSolicitado;
    private BigDecimal trocoPara;
    private String pagamentoReferencia;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDENTE;

    @Enumerated(EnumType.STRING)
    private PaymentStatus pagamentoStatus = PaymentStatus.AGUARDANDO;

    private Integer riskScore = 0;
    private boolean suspeito = false;
    private String paymentNotes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_itens", joinColumns = @JoinColumn(name = "pedido_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "itemId", column = @Column(name = "item_id")),
            @AttributeOverride(name = "nome", column = @Column(name = "nome")),
            @AttributeOverride(name = "preco", column = @Column(name = "preco")),
            @AttributeOverride(name = "quantidade", column = @Column(name = "quantidade"))
    })
    private List<PedidoItem> itens = new ArrayList<>();

    private BigDecimal total;
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OrderType getTipo() {
        return tipo;
    }

    public void setTipo(OrderType tipo) {
        this.tipo = tipo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public PaymentMethod getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(PaymentMethod formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public boolean isTrocoSolicitado() {
        return trocoSolicitado;
    }

    public void setTrocoSolicitado(boolean trocoSolicitado) {
        this.trocoSolicitado = trocoSolicitado;
    }

    public BigDecimal getTrocoPara() {
        return trocoPara;
    }

    public void setTrocoPara(BigDecimal trocoPara) {
        this.trocoPara = trocoPara;
    }

    public String getPagamentoReferencia() {
        return pagamentoReferencia;
    }

    public void setPagamentoReferencia(String pagamentoReferencia) {
        this.pagamentoReferencia = pagamentoReferencia;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPagamentoStatus() {
        return pagamentoStatus;
    }

    public void setPagamentoStatus(PaymentStatus pagamentoStatus) {
        this.pagamentoStatus = pagamentoStatus;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public boolean isSuspeito() {
        return suspeito;
    }

    public void setSuspeito(boolean suspeito) {
        this.suspeito = suspeito;
    }

    public String getPaymentNotes() {
        return paymentNotes;
    }

    public void setPaymentNotes(String paymentNotes) {
        this.paymentNotes = paymentNotes;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItem> itens) {
        this.itens = itens != null ? itens : new ArrayList<>();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}

package com.malalu.service;

import com.malalu.model.PaymentMethod;
import com.malalu.model.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentVerificationService {

    public PaymentVerificationResult verify(PaymentMethod paymentMethod, String reference, BigDecimal amount,
                                            boolean trocoSolicitado, BigDecimal trocoPara, int itemCount) {
        if (paymentMethod == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentVerificationResult(PaymentStatus.RECUSADO, 100, true,
                    "Pagamento inválido ou valor não aceito.");
        }

        int riskScore = 0;
        StringBuilder note = new StringBuilder();
        PaymentStatus status;

        if (amount.compareTo(BigDecimal.valueOf(5000)) > 0) {
            riskScore += 80;
            note.append("Valor muito alto; ");
        }
        if (itemCount > 6) {
            riskScore += 15;
            note.append("Muitos itens no pedido; ");
        }
        if (paymentMethod == PaymentMethod.DINHEIRO) {
            status = PaymentStatus.AGUARDANDO;
            if (trocoSolicitado && (trocoPara == null || trocoPara.compareTo(amount) <= 0)) {
                riskScore += 10;
                note.append("Troco solicitado; ");
            }
            if (!trocoSolicitado && amount.compareTo(BigDecimal.valueOf(200)) > 0) {
                riskScore += 20;
                note.append("Dinheiro sem troco para pedido grande; ");
            }
        } else if (paymentMethod == PaymentMethod.PIX) {
            if (reference != null && !reference.isBlank() && reference.matches("(?i)^[A-Z0-9]{6,40}$")) {
                status = PaymentStatus.CONFIRMADO;
                if (amount.compareTo(BigDecimal.valueOf(300)) > 0) {
                    riskScore += 15;
                    note.append("Pix de alto valor; revisão recomendada; ");
                }
            } else {
                return new PaymentVerificationResult(PaymentStatus.RECUSADO, 95, true,
                        "Referência Pix inválida.");
            }
        } else if (paymentMethod == PaymentMethod.CARTAO) {
            if (reference != null && reference.matches("^[0-9]{6,20}$")) {
                status = PaymentStatus.CONFIRMADO;
                if (reference.length() < 8) {
                    riskScore += 10;
                    note.append("Referência de cartão curta; verificar; ");
                }
            } else {
                return new PaymentVerificationResult(PaymentStatus.RECUSADO, 98, true,
                        "Referência de cartão inválida.");
            }
        } else {
            return new PaymentVerificationResult(PaymentStatus.RECUSADO, 100, true,
                    "Forma de pagamento desconhecida.");
        }

        if (riskScore >= 70) {
            return new PaymentVerificationResult(status, riskScore, true,
                    note.length() > 0 ? note.toString() : "Pedido suspeito.");
        }
        if (riskScore >= 40) {
            return new PaymentVerificationResult(status, riskScore, true,
                    note.length() > 0 ? note.toString() : "Verificação recomendada.");
        }
        return new PaymentVerificationResult(status, riskScore,
                riskScore > 0,
                note.length() > 0 ? note.toString() : "Pagamento aceito.");
    }
}

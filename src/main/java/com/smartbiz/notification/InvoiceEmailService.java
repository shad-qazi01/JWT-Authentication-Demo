package com.smartbiz.notification;

import com.smartbiz.client.ClientEntity;
import com.smartbiz.client.ClientRepository;
import com.smartbiz.document.InvoicePdfService;
import com.smartbiz.invoice.InvoiceEntity;
import com.smartbiz.invoice.InvoiceRepository;
import com.smartbiz.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceEmailService {

    @Autowired(required = false)
    private InvoiceRepository invoiceRepo;
    @Autowired(required = false)
    private  ClientRepository clientRepo;
    @Autowired(required = false)
    private  InvoicePdfService pdfService;
    @Autowired(required = false)
    private  EmailService emailService;

    public void send(Long invoiceId, UserEntity user) {

        InvoiceEntity inv = invoiceRepo
                .findByIdAndCreatedBy(invoiceId, user.getId())
                .orElseThrow();

        ClientEntity client = clientRepo
                .findByIdAndOwnerId(inv.getClientId(), user.getId())
                .orElseThrow();

        byte[] pdf = pdfService.generate(invoiceId, user);

        emailService.sendWithAttachment(
                client.getEmail(),
                "Your Invoice from SmartBiz",
                "Please find your invoice attached.",
                pdf,
                "invoice-" + inv.getId() + ".pdf"
        );
    }
}

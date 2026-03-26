package com.smartbiz.invoice;

import com.smartbiz.document.InvoicePdfService;
import com.smartbiz.invoice.dto.InvoiceRequest;
import com.smartbiz.invoice.dto.InvoiceResponse;
import com.smartbiz.notification.InvoiceEmailService;
import com.smartbiz.user.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    final InvoiceService invoiceService;
    final InvoicePdfService invoicePdfService;
    final InvoiceEmailService invoiceEmailService;

    @PostMapping
    public InvoiceResponse create(
            @RequestBody @Valid InvoiceRequest req,
            @AuthenticationPrincipal UserEntity user
    ) {
        return invoiceService.create(req, user);
    }

    @GetMapping
    public List<InvoiceResponse> myInvoices(
            @AuthenticationPrincipal UserEntity user
    ) {
        return invoiceService.myInvoices(user);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> download(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user) {

        byte[] pdf = invoicePdfService.generate(id, user);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> emailInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user) {

        invoiceEmailService.send(id, user);
        return ResponseEntity.ok("Invoice sent");
    }
}

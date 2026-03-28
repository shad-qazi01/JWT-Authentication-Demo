package com.smartbiz.invoice;

import com.smartbiz.client.ClientRepository;
import com.smartbiz.invoice.dto.InvoiceRequest;
import com.smartbiz.invoice.dto.InvoiceResponse;
import com.smartbiz.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class InvoiceService {

    @Autowired(required = false)
    private InvoiceRepository invoiceRepo;
    @Autowired(required = false)
    private ClientRepository clientRepo;

    public InvoiceResponse create(InvoiceRequest req, UserEntity user) {

        // verify client belongs to this user
        clientRepo.findByIdAndOwnerId(req.clientId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Invalid client"));

        double total = req.amount() + (req.amount() * req.tax() / 100);

        InvoiceEntity inv = new InvoiceEntity();
        inv.setClientId(req.clientId());
        inv.setAmount(req.amount());
        inv.setTax(req.tax());
        inv.setTotal(total);
        inv.setStatus(InvoiceStatus.UNPAID);
        inv.setDueDate(req.dueDate());
        inv.setCreatedBy(user.getId());

        return map(invoiceRepo.save(inv));
    }

    public List<InvoiceResponse> myInvoices(UserEntity user) {
        return invoiceRepo.findByCreatedBy(user.getId())
                .stream().map(this::map).toList();
    }

    private InvoiceResponse map(InvoiceEntity i) {
        return new InvoiceResponse(
                i.getId(),
                i.getClientId(),
                i.getAmount(),
                i.getTax(),
                i.getTotal(),
                i.getStatus(),
                i.getDueDate()
        );
    }
}

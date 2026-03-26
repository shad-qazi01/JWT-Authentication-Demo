package com.smartbiz.invoice;

import com.smartbiz.client.ClientEntity;
import com.smartbiz.client.ClientRepository;
import com.smartbiz.invoice.dto.InvoiceRequest;
import com.smartbiz.invoice.dto.InvoiceResponse;
import com.smartbiz.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepo;

    @Mock
    private ClientRepository clientRepo;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void shouldCreateInvoiceForOwnClient() {

        UserEntity user = UserEntity.builder()
                .id(10L)
                .build();

        InvoiceRequest req = new InvoiceRequest(
                5L,   // clientId
                1000, // amount
                18,   // tax
                LocalDate.now()
        );

        ClientEntity client = new ClientEntity();
        client.setId(5L);
        client.setOwnerId(10L);

        when(clientRepo.findByIdAndOwnerId(5L, 10L))
                .thenReturn(Optional.of(client));

        when(invoiceRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        InvoiceResponse response = invoiceService.create(req, user);

        assertEquals(1180, response.total());   // 1000 + 18%
        assertEquals(InvoiceStatus.UNPAID, response.status());
    }


    @Test
    void shouldRejectInvoiceForForeignClient() {

        UserEntity user = UserEntity.builder().id(10L).build();

        when(clientRepo.findByIdAndOwnerId(5L, 10L))
                .thenReturn(Optional.empty());

        InvoiceRequest req = new InvoiceRequest(5L, 1000, 10, LocalDate.now());

        assertThrows(RuntimeException.class,
                () -> invoiceService.create(req, user));
    }


}

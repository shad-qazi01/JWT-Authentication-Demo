package com.smartbiz.client;

import com.smartbiz.client.dto.ClientRequest;
import com.smartbiz.client.dto.ClientResponse;
import com.smartbiz.user.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ClientResponse create(
            @RequestBody @Valid ClientRequest req,
            @AuthenticationPrincipal UserEntity user
    ) {
        return clientService.create(req, user);
    }

    @GetMapping
    public List<ClientResponse> getMyClients(
            @AuthenticationPrincipal UserEntity user
    ) {
        return clientService.getMyClients(user);
    }
}


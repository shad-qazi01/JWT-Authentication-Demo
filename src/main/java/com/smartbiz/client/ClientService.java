package com.smartbiz.client;

import com.smartbiz.client.dto.ClientRequest;
import com.smartbiz.client.dto.ClientResponse;
import com.smartbiz.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepo;

    public ClientResponse create(ClientRequest req, UserEntity user) {

        ClientEntity client = new ClientEntity();
        client.setName(req.name());
        client.setEmail(req.email());
        client.setPhone(req.phone());
        client.setGst(req.gst());
        client.setOwnerId(user.getId());

        ClientEntity saved = clientRepo.save(client);

        return map(saved);
    }

    public List<ClientResponse> getMyClients(UserEntity user) {
        return clientRepo.findByOwnerId(user.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    private ClientResponse map(ClientEntity c) {
        return new ClientResponse(
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getGst()
        );
    }
}

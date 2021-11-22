package com.hyperledger.fabcar.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CarService {
    private final UserService userService = new UserService();
    private final Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet")); // Load a file system based wallet for managing identities
    private final Path networkConfigPath = Paths.get("..", "network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CarService() throws Exception {
        if (wallet.get("admin") == null) userService.enrollAdmin();
        if (wallet.get("appUser") == null) userService.registerUser();
    }

    public List<Car> getAll() throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("basic");

            return objectMapper.readValue(contract.evaluateTransaction("getAll"), new TypeReference<List<Car>>(){});
        } catch (ContractException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Car getOne(final String id) throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("basic");


            return objectMapper.readValue(contract.evaluateTransaction("getOne", id), Car.class);
        } catch (ContractException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Car create(final Car car) throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("basic");

            contract.submitTransaction("create", car.getId(), car.getBrand(), car.getModel(), car.getColor(), car.getOwner());

            return objectMapper.readValue(contract.evaluateTransaction("getOne", car.getId()), Car.class);
        } catch (ContractException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Car update(final Car car) throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("basic");

            contract.submitTransaction("create", car.getId(), car.getBrand(), car.getModel(), car.getColor(), car.getOwner());

            return objectMapper.readValue(contract.evaluateTransaction("update", car.getId()), Car.class);
        } catch (ContractException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(final String id) throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("basic");

            contract.evaluateTransaction("delete", id);
        } catch (ContractException e) {
            e.printStackTrace();
        }
    }

}

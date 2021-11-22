/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcar;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(name = "basic", info = @Info(
                title = "FabCar contract",
                description = "Simple car contract",
                version = "0.0.1",
                contact = @Contact(
                        email = "ImGelu@gmail.com",
                        name = "Gelu Ungur",
                        url = "https://gelu.io")))
@Default
public final class CarContract implements ContractInterface {

    private final Genson genson = new Genson();
    private enum FabCarErrors { CAR_NOT_FOUND, CAR_ALREADY_EXISTS }

    @Transaction()
    public String getAll(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Car> queryResults = new ArrayList<Car>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Car car = genson.deserialize(result.getStringValue(), Car.class);
            queryResults.add(car);
        }

        return genson.serialize(queryResults);
    }

    @Transaction()
    public Car getOne(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String carState = stub.getStringState(id);

        if (carState == null || carState.isEmpty()) {
            String errorMessage = String.format("Car %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_NOT_FOUND.toString());
        }

        return genson.deserialize(carState, Car.class);
    }

    @Transaction()
    public Car create(final Context ctx, final String id, final String brand, final String model, final String color,
                      final String owner) {
        ChaincodeStub stub = ctx.getStub();
        String carState = stub.getStringState(id);

        if (!carState.isEmpty()) {
            String errorMessage = String.format("Car %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_ALREADY_EXISTS.toString());
        }

        Car car = new Car(id, brand, model, color, owner);
        carState = genson.serialize(car);
        stub.putStringState(id, carState);

        return car;
    }

    @Transaction()
    public Car update(final Context ctx, final String id, final String brand, final String model, final String color,
                      final String owner) {
        ChaincodeStub stub = ctx.getStub();
        String carState = stub.getStringState(id);

        if (carState.isEmpty()) {
            String errorMessage = String.format("Car %s doesn't exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_NOT_FOUND.toString());
        }

        Car car = new Car(id, brand, model, color, owner);
        carState = genson.serialize(car);
        stub.putStringState(id, carState);

        return car;
    }

    @Transaction()
    public void delete(final Context ctx, final String id) {
        ChaincodeStub stub = ctx.getStub();
        String carState = stub.getStringState(id);

        if (carState == null || carState.isEmpty()) {
            String errorMessage = String.format("Car %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_NOT_FOUND.toString());
        }

        stub.delState(id);
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        create(ctx, "Car1", "Volkswagen", "Golf", "Grey", "Andrei");
        create(ctx, "Car2", "Audi", "A4", "Blue", "Alex");
        create(ctx, "Car3", "Ford", "Mustang", "Red", "Andreea");
        create(ctx, "Car4", "Fiat", "Punto", "White", "Roxana");
        create(ctx, "Car5", "Tesla", "S", "Black", "Radu");
    }
}

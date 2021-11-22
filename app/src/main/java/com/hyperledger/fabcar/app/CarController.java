package com.hyperledger.fabcar.app;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<Car> getAll() throws Exception {
        return carService.getAll();
    }

    @PostMapping
    public Car create(@RequestBody final Car car) throws Exception {
        return carService.create(car);
    }

    @GetMapping("/{id}")
    public Car getOne(@PathVariable final String id) throws Exception {
        return carService.getOne(id);
    }

    @PutMapping("/{id}")
    public Car update(@RequestBody final Car car) throws Exception {
        return carService.update(car);
    }

    @DeleteMapping("/{id}")
    public String delete(@RequestParam final String id) throws Exception {
        carService.delete(id);

        return "Deleted";
    }
}

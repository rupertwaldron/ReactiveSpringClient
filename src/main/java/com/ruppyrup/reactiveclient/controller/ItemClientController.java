package com.ruppyrup.reactiveclient.controller;


import com.ruppyrup.reactiveclient.domain.Item;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAlItemsUsingRetrieve() {
        return webClient.get().uri("v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in client Project");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAlItemsUsingExchange() {
        return webClient.get().uri("v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in client Project");
    }

    @GetMapping("/client/retrieve/singleItem/{id}")
    public Mono<Item> getOneItemUsingRetrieve(@PathVariable String id) {
        return webClient.get().uri("v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Item with id ABC in client Project: " + id);
    }

    @GetMapping("/client/retrieve/singleItemRR/{id}")
    public Mono<Item> getOneItemUsingRetrieveRR(@PathVariable String id) {

        Mono<Item> itemMono = webClient.get().uri("v1/items/{id}", id)
            .retrieve()
            .bodyToMono(Item.class)
            .log("Item with id ABC in client Project: " + id);

        Mono<Double> price = itemMono.map(Item::getPrice);
        System.out.println(price);

        Mono<Item> itemMono1 = itemMono
            .map(item -> {
                item.setPrice(item.getPrice() * 1.1);
                return item;
            })
            .log("Item updated: " + itemMono);


        return itemMono1;
    }

    @GetMapping("/client/exchange/singleItem/{id}")
    public Mono<Item> getOneItemUsingExchange(@PathVariable String id) {
        return webClient.get().uri("v1/items/{id}", id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Item with id ABC in client Project: " + id);
    }

    @PostMapping("client/createItem")
    public Mono<Item> createItem(@RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.post().uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created item is: " + item);
    }

    @PutMapping("client/changeItem/{id}")
    public Mono<Item> changeItem(@PathVariable String id, @RequestBody Item item) {
        Mono<Item> itemMono = Mono.just(item);
        return webClient.put().uri("/v1/items/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Changed item is: " + id);
    }

    @DeleteMapping("client/deleteItem/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("Deleted item is: " + id);
    }


}

package com.evolve.handler;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import com.evolve.model.Cart;
import com.evolve.repository.CartRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class CartHandler {

    private final CartRepository cartRepository;

    private ReactiveRedisTemplate<String, Cart> redisCartTemplate;


    public CartHandler(CartRepository cartRepository, ReactiveRedisTemplate<String, Cart> redisCartTemplate) {
        this.cartRepository = cartRepository;
        this.redisCartTemplate = redisCartTemplate;
    }

    public Mono<ServerResponse> getAll(ServerRequest req) {
        return ServerResponse.ok().body(cartRepository.findAll(), Cart.class);
    }

    public Mono<ServerResponse> getById(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return cartRepository.findById(id)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getByUserId(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("userId"));
        return ServerResponse.ok().body(cartRepository.findAllByUserId(userId), Cart.class);
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("userId"));
        return req.bodyToMono(Cart.class)
                .map(cart -> {
                    cart.setUserId(userId);
                    return cart;
                })
                .flatMap(cartRepository::save)
                .flatMap(saved ->
                {
                    redisCartTemplate.opsForValue().set("cart:" + userId, saved).subscribe();
                    return ServerResponse.ok().bodyValue(saved);
                });
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(Cart.class).flatMap(incoming ->
                cartRepository.findById(id).flatMap(existing -> {
                    existing.setStatus(incoming.getStatus());
                    return cartRepository.save(existing);
                }).flatMap(updated -> {
                    // Saving the Changes to Redis
                    redisCartTemplate.opsForValue().set("cart:" + updated.getUserId(), updated).subscribe();
                    return ServerResponse.ok().bodyValue(updated);
                }).switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Publish stream by polling every 1s
    public Mono<ServerResponse> streamCartUpdates(ServerRequest request) {
        String userId = request.queryParam("userId").orElseThrow();

        Flux<Cart> cartStream = Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> redisCartTemplate.opsForValue().get("cart:" + userId))
                .distinctUntilChanged();

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(cartStream, Cart.class);
    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return cartRepository.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
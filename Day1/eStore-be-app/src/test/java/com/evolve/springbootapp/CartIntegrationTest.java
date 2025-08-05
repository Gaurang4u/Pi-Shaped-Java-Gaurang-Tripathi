package com.evolve.springbootapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.evolve.model.Cart;
import com.evolve.model.CartItem;
import com.evolve.repository.CartItemRepository;
import com.evolve.repository.CartRepository;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartIntegrationTest {


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2").withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long cartId;

    @BeforeEach
    void cleanUp() {
        cartRepository.deleteAll().block();
        cartItemRepository.deleteAll().block();

        Cart cart = new Cart();
        cart.setUserId(1L);
        cart.setStatus("active");
        cartId = cartRepository.save(cart).block().getId();
    }

    @Test
    void testCreateCart() {
        Cart newCart = new Cart();
        newCart.setStatus("active");

        webTestClient.post()
                .uri("/carts/user/1")
                .bodyValue(newCart)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("active");
    }

    @Test
    void testGetCartByUserId() {
        Cart cart = new Cart();
        cart.setUserId(99L);
        cart.setStatus("pending");
        cartRepository.save(cart).block();

        webTestClient.get()
                .uri("/carts/user/99")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].userId").isEqualTo(99)
                .jsonPath("$[0].status").isEqualTo("pending");
    }

    @Test
    void testUpdateCart() {
        Cart cart = new Cart();
        cart.setUserId(101L);
        cart.setStatus("pending");
        Cart saved = cartRepository.save(cart).block();

        saved.setStatus("checked_out");

        webTestClient.put()
                .uri("/carts/" + saved.getId())
                .bodyValue(saved)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("checked_out");
    }

    @Test
    void testDeleteCart() {
        Cart cart = new Cart();
        cart.setUserId(102L);
        cart.setStatus("expired");
        Cart saved = cartRepository.save(cart).block();

        webTestClient.delete()
                .uri("/carts/" + saved.getId())
                .exchange()
                .expectStatus().isNoContent();
    }
    @Test
    public void testAddCartItem() {
        CartItem item = new CartItem();
        item.setProductId(101L);
        item.setQuantity(2);

        webTestClient.post()
                .uri("/carts/{cartId}/items", cartId)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CartItem.class)
                .value(ci -> {
                    assertThat(ci.getId()).isNotNull();
                    assertThat(ci.getProductId()).isEqualTo(101L);
                });
    }

    @Test
    public void testListCartItems() {
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setProductId(200L);
        item.setQuantity(1);
        cartItemRepository.save(item).block();

        webTestClient.get()
                .uri("/carts/{cartId}/items", cartId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CartItem.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0).getProductId()).isEqualTo(200L));
    }

    @Test
    public void testRemoveCartItem() {
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setProductId(300L);
        item.setQuantity(5);
        Long itemId = cartItemRepository.save(item).block().getId();

        webTestClient.delete()
                .uri("/carts/items/{itemId}", itemId)
                .exchange()
                .expectStatus().isNoContent();

        assertThat(cartItemRepository.findById(itemId).block()).isNull();
    }
}
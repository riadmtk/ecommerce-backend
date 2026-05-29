package com.ecommerce.wishlist.infrastructure.adapter.in.web;

import com.ecommerce.wishlist.domain.model.Wishlist;
import com.ecommerce.wishlist.domain.port.in.AddProductToWishlistUseCase;
import com.ecommerce.wishlist.domain.port.in.AddProductToWishlistUseCase.AddProductCommand;
import com.ecommerce.wishlist.domain.port.in.GetWishlistUseCase;
import com.ecommerce.wishlist.domain.port.in.RemoveProductFromWishlistUseCase;
import com.ecommerce.wishlist.infrastructure.adapter.in.web.dto.AddProductRequest;
import com.ecommerce.wishlist.infrastructure.adapter.in.web.dto.WishlistResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    // Injecting the strictly segregated Use Cases
    private final AddProductToWishlistUseCase addProductUseCase;
    private final RemoveProductFromWishlistUseCase removeProductUseCase;
    private final GetWishlistUseCase getWishlistUseCase;

    @GetMapping("/me")
    public ResponseEntity<WishlistResponse> getMyWishlist(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Wishlist wishlist = getWishlistUseCase.getWishlist(userId);

        return ResponseEntity.ok(WishlistResponse.from(wishlist));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addProductToWishlist(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddProductRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());

        AddProductCommand command = new AddProductCommand(
                userId,
                request.productId(),
                request.notifyOnRestock()
        );

        addProductUseCase.addProduct(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeProductFromWishlist(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID productId) {

        UUID userId = UUID.fromString(jwt.getSubject());

        removeProductUseCase.removeProduct(userId, productId);

        return ResponseEntity.noContent().build();
    }
}
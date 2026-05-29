package com.ecommerce.wishlist.application.service;

import com.ecommerce.wishlist.domain.model.Wishlist;
import com.ecommerce.wishlist.domain.model.WishlistItem;
import com.ecommerce.wishlist.domain.port.in.AddProductToWishlistUseCase;
import com.ecommerce.wishlist.domain.port.in.GetWishlistUseCase;
import com.ecommerce.wishlist.domain.port.in.RemoveProductFromWishlistUseCase;
import com.ecommerce.wishlist.domain.port.out.ProductServiceClientPort;
import com.ecommerce.wishlist.domain.port.out.NotificationEventPort;
import com.ecommerce.wishlist.domain.port.out.UserServiceClientPort;
import com.ecommerce.wishlist.domain.port.out.WishlistRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService implements
        AddProductToWishlistUseCase,
        RemoveProductFromWishlistUseCase,
        GetWishlistUseCase {

    private final WishlistRepositoryPort wishlistRepository;
    private final ProductServiceClientPort productServiceClient;
    private final NotificationEventPort notificationEventPort;
    private final UserServiceClientPort userServiceClient;

    @Override
    @Transactional
    public void addProduct(AddProductCommand command) {
        // 1. Validate Product Exists (Synchronous HTTP call via Port)
        // If it throws a 404 or 503, the transaction stops here, and your ControllerAdvice catches it!
        productServiceClient.validateProductExists(command.productId());

        // 2. Fetch existing wishlist, or create a brand new one if this is the user's first time
        Wishlist wishlist = wishlistRepository.findByUserId(command.userId())
                .orElseGet(() -> Wishlist.create(command.userId()));

        // 3. Construct the Domain Object
        WishlistItem item = WishlistItem.builder()
                .productId(command.productId())
                .addedAt(LocalDateTime.now())
                .notifyOnRestock(command.notifyOnRestock())
                .build();

        // 4. Delegate business logic to the pure Java Domain Model (No Spring logic here!)
        wishlist.addProduct(item);

        // 5. Save the updated state to the DB
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeProduct(UUID userId, UUID productId) {
        // We use ifPresent: if the wishlist doesn't exist, we just ignore the request.
        wishlistRepository.findByUserId(userId).ifPresent(wishlist -> {
            wishlist.removeProduct(productId);
            wishlistRepository.save(wishlist);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Wishlist getWishlist(UUID userId) {
        // Returning an empty wishlist instead of throwing a 404 is a much better UX for new users.
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> Wishlist.create(userId));
    }

    @Transactional(readOnly = true) // C'est une lecture seule côté Wishlist, l'écriture se fait côté Notification
    public void handleProductRestocked(UUID productId, String productName) {
        log.info("Traitement du restockage pour le produit : {}", productName);

        // 1. On cherche qui veut être notifié pour CE produit précis
        List<Wishlist> wishlistsToNotify = wishlistRepository.findWishlistsNeedingRestockNotification(productId);

        for (Wishlist wishlist : wishlistsToNotify) {
            String email = userServiceClient.getUserEmail(wishlist.getUserId());

            log.info("Envoi d'une alerte de restockage à l'utilisateur : {}", wishlist.getUserId());
            notificationEventPort.sendRestockNotificationRequest(
                    wishlist.getUserId(),
                    email,
                    productId,
                    productName
            );
        }
    }
}
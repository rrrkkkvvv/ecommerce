package com.ecommerce.shop.domain.order.service;

import com.ecommerce.shop.domain.entitlement.repository.EntitlementRepository;
import com.ecommerce.shop.domain.order.dto.OrderPaymentInitResponseDto;
import com.ecommerce.shop.exception.payment.PaymentNotFoundException;
import com.ecommerce.shop.exception.user.UserNotFoundException;
import com.ecommerce.shop.domain.cart.entity.CartEntity;
import com.ecommerce.shop.domain.entitlement.entity.EntitlementEntity;
import com.ecommerce.shop.domain.order.entity.Currency;
import com.ecommerce.shop.domain.order.entity.OrderEntity;
import com.ecommerce.shop.domain.order.entity.OrderStatus;
import com.ecommerce.shop.domain.order.entity.OrderItemEntity;
import com.ecommerce.shop.domain.payment.entity.PaymentEntity;
import com.ecommerce.shop.domain.payment.entity.PaymentStatus;
import com.ecommerce.shop.domain.order.repository.OrderItemRepository;
import com.ecommerce.shop.domain.order.repository.OrderRepository;
import com.ecommerce.shop.domain.payment.repository.PaymentRepository;
import com.ecommerce.shop.domain.product.entity.ProductEntity;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import com.ecommerce.shop.domain.user.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import com.stripe.model.checkout.Session;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntitlementRepository entitlementRepository;

    @Value("${frontend.url}")
    private  String frontendUrl;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, OrderItemRepository orderItemRepository, EntitlementRepository entitlementRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;

        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.entitlementRepository = entitlementRepository;
    }

    @Transactional
    public OrderPaymentInitResponseDto initiateOrderPayment(Long userId){

        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        CartEntity cart = user.getCart();
        Set<ProductEntity> products = cart.getProducts();
        BigDecimal totalPrice = products.stream().map(ProductEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<OrderItemEntity> orderItems = products.stream().map(p-> new OrderItemEntity(p, Currency.PLN, p.getPrice())).toList();

        OrderEntity order = new OrderEntity(user, totalPrice, OrderStatus.PENDING, Currency.PLN,orderItems);


        PaymentEntity payment = new PaymentEntity(PaymentStatus.processing, order.getTotalPrice(), Currency.PLN );
        order.setPayment(payment);

        orderRepository.save(order);

        String sessionUrl = getSessionUrl(user, order);
        return new OrderPaymentInitResponseDto(sessionUrl);
    }

    private String getSessionUrl(UserEntity user, OrderEntity order){
        try {
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();
            Customer customer = Customer.create(customerParams);
            SessionCreateParams.Builder sessionParamsBuilder =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(frontendUrl+"/payment/success")
                            .setCancelUrl(frontendUrl+"/payment/failure")
                            .setCustomer(customer.getId());
            for(OrderItemEntity orderItem : order.getItems()){
                sessionParamsBuilder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(orderItem.getCurrency().toString().toLowerCase(Locale.ROOT))
                                                .setUnitAmount(
                                                        orderItem.getUnitPrice().multiply(BigDecimal.valueOf(100)).longValueExact()
                                                )
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(orderItem.getProduct().getName())
                                                                .setDescription(orderItem.getProduct().getDescription())
                                                                .build()
                                                )
                                                .build()
                                ).build()
                );
            }
            SessionCreateParams sessionParams = sessionParamsBuilder.build();

            Session session = Session.create(sessionParams);
            order.getPayment().setProviderPaymentId(session.getId());
            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }
    }

    @Transactional
    public void capturePayment(Event event){

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;
            String sessionId = session.getId();
            int updMark = paymentRepository.makePaymentSucceeded(sessionId);
            if (updMark == 0) {
                return;
            }
            PaymentEntity payment = paymentRepository.findByProviderPaymentId(sessionId)
                    .orElseThrow(PaymentNotFoundException::new);

            OrderEntity order = payment.getOrder();
            UserEntity user = order.getUser();
            order.setStatus(OrderStatus.COMPLETED);
            LocalDateTime grantedAt = LocalDateTime.now();
            LocalDateTime expiresAt = LocalDateTime.now().plusMonths(1L);


            order.getItems().forEach(orderItemEntity -> {
                EntitlementEntity entitlement = new EntitlementEntity(user, orderItemEntity.getProduct(),order, grantedAt, expiresAt);
                entitlementRepository.save(entitlement);
            });



        }
    }
}

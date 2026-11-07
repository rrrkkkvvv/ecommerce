package com.ecommerce.shop.domain.order.service;

import com.ecommerce.shop.domain.checkoutAttempt.entity.CheckoutAttemptEntity;
import com.ecommerce.shop.domain.checkoutAttempt.entity.CheckoutAttemptStatus;
import com.ecommerce.shop.domain.checkoutAttempt.repository.CheckoutAttemptRepository;
import com.ecommerce.shop.domain.entitlement.repository.EntitlementRepository;
import com.ecommerce.shop.domain.order.dto.Order;
import com.ecommerce.shop.domain.order.dto.OrderPaymentInitResponseDto;
import com.ecommerce.shop.domain.order.mapper.OrderMapper;
import com.ecommerce.shop.domain.payment.entity.PaymentStatus;
import com.ecommerce.shop.domain.product.repository.ProductRepository;
import com.ecommerce.shop.exception.checkoutAttempt.CheckoutAttemptNotFound;
import com.ecommerce.shop.exception.order.OrderNotFoundException;
import com.ecommerce.shop.exception.payment.PaymentNotFoundException;
import com.ecommerce.shop.exception.product.ProductNotFoundException;
import com.ecommerce.shop.exception.user.UserNotFoundException;
import com.ecommerce.shop.domain.cart.entity.CartEntity;
import com.ecommerce.shop.domain.entitlement.entity.EntitlementEntity;
import com.ecommerce.shop.domain.order.entity.Currency;
import com.ecommerce.shop.domain.order.entity.OrderEntity;
import com.ecommerce.shop.domain.order.entity.OrderStatus;
import com.ecommerce.shop.domain.order.entity.OrderItemEntity;
import com.ecommerce.shop.domain.payment.entity.PaymentEntity;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.stripe.model.checkout.Session;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntitlementRepository entitlementRepository;
    private final ProductRepository productRepository;
    private final CheckoutAttemptRepository checkoutAttemptRepository;

    private final OrderMapper orderMapper;


    @Value("${frontend.url}")
    private  String frontendUrl;

    public OrderService(
            UserRepository userRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            OrderItemRepository orderItemRepository,
            EntitlementRepository entitlementRepository,
            ProductRepository productRepository, CheckoutAttemptRepository checkoutAttemptRepository, OrderMapper orderMapper
    ) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;

        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.entitlementRepository = entitlementRepository;
        this.productRepository = productRepository;
        this.checkoutAttemptRepository = checkoutAttemptRepository;
        this.orderMapper = orderMapper;
    }
    @Transactional
    public Order createOrder(Long userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        CartEntity cart = user.getCart();
        Set<ProductEntity> products = cart.getProducts();
        BigDecimal totalPrice = products.stream().map(ProductEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<OrderItemEntity> orderItems = products.stream().map(p-> new OrderItemEntity(p.getId(),p.getName(), p.getPrice(), Currency.PLN)).toList();
        OrderEntity order = new OrderEntity(user, totalPrice, OrderStatus.PENDING, Currency.PLN,orderItems);

        CheckoutAttemptEntity checkoutAttempt = new CheckoutAttemptEntity();
        checkoutAttempt.setOrder(order);
        checkoutAttempt.setStatus(CheckoutAttemptStatus.ACTIVE);
        checkoutAttempt.setUser(user);
        orderRepository.save(order);

        return orderMapper.convertToDomain(order);
    }
    @Transactional
    public OrderPaymentInitResponseDto initiateCheckoutAttempt(Long userId, Long orderId)    {

            CheckoutAttemptEntity checkoutAttempt = checkoutAttemptRepository.findByOrderId(orderId).orElseThrow(CheckoutAttemptNotFound::new);
//        int markCheckoutPending = checkoutAttemptRepository.makeCheckoutPending(checkoutAttempt.getId());
//        if (markCheckoutPending == 0) {
//
//        }
            return new OrderPaymentInitResponseDto(getSessionUrl(checkoutAttempt)) ;



    }
    private String getSessionUrl(CheckoutAttemptEntity checkoutAttempt)  {
        try {
            if (checkoutAttempt.getSessionId() == null || checkoutAttempt.getSessionExpiresAt().isAfter(LocalDateTime.now())) {
                Session session = Session.retrieve(checkoutAttempt.getSessionId());
                return session.getUrl();
            } else {
                UserEntity user = checkoutAttempt.getUser();
                OrderEntity order = checkoutAttempt.getOrder();

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
                    ProductEntity product = productRepository.findById(orderItem.getProductId()).orElseThrow(ProductNotFoundException::new);

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
                                                                    .setName(product.getName())
                                                                    .setDescription(product.getDescription())
                                                                    .build()
                                                    )
                                                    .build()
                                    ).build()
                    );
                }
                SessionCreateParams sessionParams = sessionParamsBuilder.build();

                Session session = Session.create(sessionParams);
                return session.getUrl();
            }
        } catch (StripeException e) {
            throw new RuntimeException("Failed to handle Stripe session", e);
        }
    }
//    TODO: LEGACY
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


            order.getItems().forEach(orderItem -> {
                ProductEntity product = productRepository.findById(orderItem.getProductId()).orElseThrow(ProductNotFoundException::new);

                EntitlementEntity entitlement = new EntitlementEntity(user, product, order, grantedAt, expiresAt);
                entitlementRepository.save(entitlement);
            });



        }
    }
}

export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8080',
  stripePublicKey: 'pk_test_51T7CPLI1WdM60jRsa5FtW0BCWTJ0bxkjarPpWkwOYHiQbBZJHSOYqHRQfZfaU2A6feqB18iSkEO6Rj42pzrhLvKB00xqU4CiK8',
  services: {
    auth: 'http://localhost:8080/api/auth',
    users: 'http://localhost:8080/api/users',
    products: 'http://localhost:8080/api/v1/products',
    orders: 'http://localhost:8080/api/orders',
    payments: 'http://localhost:8080/api/payments',
    search: 'http://localhost:8080/api/v1/search',
    cart: 'http://localhost:8080/api/v1/carts'
  }
};
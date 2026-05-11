export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8080',
  services: {
    auth: 'http://localhost:8080/api/auth',
    users: 'http://localhost:8080/api/users',
    products: 'http://localhost:8080/api/v1/products',
    orders: 'http://localhost:8080/api/orders',
    cart: 'http://localhost:8080/api/v1/carts/my-cart'
  }
};
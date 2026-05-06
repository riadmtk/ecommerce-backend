export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8080',
  services: {
    auth: 'http://localhost:8081/api/auth',
    users: 'http://localhost:8081/api/users',
    products: 'http://localhost:8082/api/v1/products',
    orders: 'http://localhost:8085/api/orders'
  }
};
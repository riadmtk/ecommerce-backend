export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8080',
  stripePublicKey: 'pk_test_51T9obaRU9nynbdRwW3ZmrlTWmDg0DFbRtDpnjtRrfM1IbTSK9FxiIxvfuq6nbCX3DxhjVhSVcOwfGXU1IevGQwRq00NmX8T90x',
  services: {
    auth: 'http://localhost:8080/api/auth',
    users: 'http://localhost:8080/api/users',
    products: 'http://localhost:8080/api/v1/products',
    categories: 'http://localhost:8080/api/v1/categories',
    orders: 'http://localhost:8080/api/orders',
    payments: 'http://localhost:8080/api/payments',
    search: 'http://localhost:8080/api/v1/search',
    cart: 'http://localhost:8080/api/v1/carts',
    wishlists: 'http://localhost:8080/api/v1/wishlists'
  }
};
const MainPage = {template: "<main_page></main_page>"}
const ProfilePage = {template: "<profile></profile>"}
const AddVehicleToCartPage = {template: "<add_vehicle_to_cart></add_vehicle_to_cart>"}
const ShopingCartPage = {template: "<shoping_cart></shoping_cart>"}
const ObjectInfoPage = {template: "<object_info></object_info>"}
const RentVehiclePage = {template: "<rent_vehicle></rent_vehicle>"}
const AllOrdersPage = {template: "<all_orders></all_orders>"}
const Comment = {template: "<comment></comment>"}
const Logout = {template: "<logout></logout>"}

const router = new VueRouter({
    mode: 'hash',
    routes: [
        { path: '/', component: MainPage},
        { path: '/Profile', component: ProfilePage},
        { path: '/AddToCart/:objectId', component: AddVehicleToCartPage},
        { path: '/ShopingCart', component: ShopingCartPage},
        { path: '/ObjectInfo', component: ObjectInfoPage},
        { path: '/RentVehicle', component: RentVehiclePage},
        { path: '/AllOrders', component: AllOrdersPage},
        { path: '/Comment', name: "COMMENT" , component: Comment},
        { path: '/Logout', component: Logout}

    ]
});


var app = new Vue({
    router,
    el: "#main_container"
});
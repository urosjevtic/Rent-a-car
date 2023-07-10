const MainPage = {template: "<main_page></main_page>"}
const ProfilePage = {template: "<profile></profile>"}
const ObjectInfoPage = {template: "<object_info></object_info>"}
const Vehicles = {template: "<vehicles></vehicles>"}
const NewVehicle = {template: "<register_vehicle></register_vehicle>"}
const Orders = {template: "<orders></orders>"}
const ManagerComments = {template: "<manager_comments></manager_comments>"}
const ManagerObject = {template: "<manager_object></manager_object>"}
const Logout = {template: "<logout></logout>"}

const router = new VueRouter({
    mode: 'hash',
    routes: [
        { path: '/', component: MainPage},
        { path: '/Profile', component: ProfilePage},
        { path: '/ObjectInfo', component: ObjectInfoPage},
        { path: '/Vehicles', component: Vehicles},
        { path: '/RegisterVehicle', name: 'REGISTER_VEHICLE' ,component: NewVehicle},
        { path: '/Orders', component: Orders},
        { path: '/ManagerComments', component: ManagerComments},
        { path: '/ManagerObject', component: ManagerObject},
        { path: '/Logout', component: Logout}
    ]
});


var app = new Vue({
    router,
    el: "#main_container"
});
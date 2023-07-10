const MainPage = {template: "<main_page></main_page>"}
const ProfilePage = {template: "<profile></profile>"}
const AllProfiles = {template: "<allProfiles></allProfiles>"}
const RegistrateManager = {template: "<register_manager></register_manager>"}
const CreateNewObject = {template: "<createNewObject></createNewObject>"}
const ObjectInfoPage = {template: "<object_info></object_info>"}
const Logout = {template: "<logout></logout>"}

const router = new VueRouter({
    mode: 'hash',
    routes: [
        {path: '/', component: MainPage},
        {path: '/Profile', component: ProfilePage},
        {path: '/AllProfiles', component: AllProfiles},
        {path: '/RegistrateManager',name: 'REGISTER_MANAGER', component: RegistrateManager},
        {path: '/CreateNewObject',component: CreateNewObject},
        { path: '/ObjectInfo', component: ObjectInfoPage},
        { path: '/Logout', component: Logout}
    ]
});


var app = new Vue({
    router,
    el: "#main_container"
});
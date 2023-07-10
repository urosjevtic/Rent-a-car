const RegisterPage = {template: "<register_user></register_user>"}
const MainPage = {template: "<main_page></main_page>"}
const LoginPage = {template: "<login_page></login_page>"}
const ObjectInfoPage = {template: "<object_info></object_info>"}
const router = new VueRouter({
    mode: 'hash',
    routes: [
        { path: '/', component: MainPage},
        { path: '/Register', component: RegisterPage},
        { path: '/Login', component: LoginPage},
        { path: '/ObjectInfo', component: ObjectInfoPage}
    ]
});


var app = new Vue({
    router,
    el: "#main_container"
});
Vue.component('logout', {
    mounted(){
        axios.delete("api/user/logout")
            .then(response => {
                if(response.data === "User nog logged in")
                {
                    window.alert("No logged user");
                }else {
                    location.href = response.data;
                }
            });
    }
})
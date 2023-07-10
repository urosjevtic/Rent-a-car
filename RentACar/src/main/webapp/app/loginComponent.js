Vue.component("login_page", {
    data: function (){
        return{
            userCredential:{}
        }
    },
    template:
        `
     <div class="container">
        <div class="image-section"></div>
        <div class="registration-section">
            <form class="registration-form" @submit="logIn">
                <div class="form-group">
                    <input type="text" id="Username" v-model="userCredential.username" required>
                    <label for="Username">Username</label>
                </div>
                <div class="form-group">
                    <input type="password" id="Password" v-model="userCredential.password" required>
                    <label for="Password">Password</label>
                </div>
              
                <input type="submit" value="Log in">
            </form>

        </div>
    </div>
    `,
    methods:
        {
            logIn: function(event) {
                event.preventDefault(); // Prevent the default form submission behavior

                axios.post("api/user/login", this.userCredential)
                    .then(response => {
                        console.log(response);
                        if (response.data === "User is blocked") {
                            console.log(response);
                            window.alert("Your profile is blocked");
                        } else {
                            console.log(response);
                            window.alert("Successful login");
                            location.href = response.data;
                        }
                    })
                    .catch(error => {
                        console.error(error);
                    });
            }
        }
});
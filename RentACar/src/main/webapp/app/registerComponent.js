Vue.component("register_user", {
    data: function (){
        return{
            newUser: {},
            showError: false,
            wrongPasswordFormatError: false,
            wrongUsernameFormatError: false
        }
    },
    template:
    `
     <div class="container">
        <div class="image-section"></div>
        <div class="registration-section">
            <form class="registration-form" @submit="registerNewUser">
                <div class="form-group">
                     <input type="text" id="Username" v-model="newUser.username">
                     <label for="Username">Username</label>
                     <span v-if="showError && !newUser.username" class="error-message">Please fill in this field.</span>
                     <span v-if="wrongUsernameFormatError" class="error-message">Wrong username format.</span>
                </div>
                <div class="form-group">
                    <input type="password" id="Password" v-model="newUser.password">
                    <label for="Password">Password</label>
                    <span v-if="showError && !newUser.password" class="error-message">Please enter your password.</span>
                    <span v-if="wrongPasswordFormatError" class="error-message">Password should be at least 8 character long and contain 1 number.</span>
                </div>
                <div class="form-group">
                    <input type="text" id="Name" v-model="newUser.name">
                    <label for="Name">First name</label>
                    <span v-if="showError && !newUser.name" class="error-message">Please enter name.</span>
                </div>
                <div class="form-group">
                    <input type="text" id="LastName" v-model="newUser.lastName">
                    <label for="LastName">Last name</label>
                    <span v-if="showError && !newUser.lastName" class="error-message">Please enter your last name.</span>
                </div>
                <div class="form-group">
                    <input type="date" id="Birth_date" v-model="newUser.birthDate"  placeholder="">
                    <label for="Birth_date">Date of birth</label>
                    <span v-if="showError && !newUser.birthDate" class="error-message">Please enter your birth date.</span>
                </div>
                <div class="form-group">
                    <select id="Gender" v-model="newUser.gender">
                        <option disabled selected value=""></option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                    </select>
                    <label for="Gender">Gender</label>
                    <span v-if="showError && !newUser.gender" class="error-message">Please select your gender.</span>
                </div>
                <input type="submit" value="Submit">
            </form>

        </div>
    </div>
    `,
    methods:
        {
            registerNewUser: function (event) {
                event.preventDefault(); // Prevent the default form submission behavior
                if (!this.newUser.username || !this.newUser.password) {
                    this.showError = true;
                } else {
                    this.newUser.role = "Customer";
                    axios
                        .post("api/user/register", this.newUser)
                        .then((response) => {
                            // Handle the response or perform any necessary actions
                            if (response.data === 'User successfully created') {
                                console.log(response);
                                window.alert(["Successful registration"]);
                                router.push("/");
                            } else if (response.data === 'Data not valid') {
                                const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
                                const usernamePattern = /^[a-zA-Z0-9_-]{3,16}$/;
                                if (!passwordPattern.test(this.newUser.password)) {
                                    this.wrongPasswordFormatError = true;
                                }
                                if (!usernamePattern.test(this.newUser.username)) {
                                    this.wrongUsernameFormatError = true;
                                }
                            }else {
                                window.alert(["Registration error"]);
                            }
                        })
                        .catch((error) => {
                            // Handle any errors that occur during the request
                            console.error(error);
                        });
                }

            }
        }
});
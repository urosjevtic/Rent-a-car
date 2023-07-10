Vue.component("profile", {
    data:function (){
        return{
            user:{},
            userEdited: {},
            showEditFields: {
                username: false,
                name: false,
                lastName: false,
                birthDate: false,
                gender: false
            },

        }
    },
    template:
    `
        <div id="user_profile">
        <form @submit="updateUser">
            <h2>User data</h2>
            <table>
                <tr>
                    <td>Username:</td>
                    <td>{{ user.username }}</td>
                    <td v-on:click="toggleEditField('username')">Edit</td>
                </tr>
                <tr v-show="showEditFields.username">
                    <td></td>
                    <td><label>New username: </label></td>
                    <td><input type="text" v-model="userEdited.username" required></td>
                </tr>
                <tr>
                    <td>Name:</td>
                    <td>{{ user.name }}</td>
                    <td v-on:click="toggleEditField('name')">Edit</td>
                </tr>
                <tr v-show="showEditFields.name">
                    <td></td>
                    <td><label>New name: </label></td>
                    <td><input type="text" v-model="userEdited.name" required></td>
                </tr>
                <tr>
                    <td>Last name:</td>
                    <td>{{ user.lastName }}</td>
                    <td v-on:click="toggleEditField('lastName')">Edit</td>
                </tr>
                <tr v-show="showEditFields.lastName">
                    <td></td>
                    <td><label>New last name: </label></td>
                    <td><input type="text" v-model="userEdited.lastName" required></td>
                </tr>
                <tr>
                    <td>Date of birth:</td>
                    <td>{{ user.birthDate }}</td>
                    <td v-on:click="toggleEditField('birthDate')">Edit</td>
                </tr>
                <tr v-show="showEditFields.birthDate">
                    <td></td>
                    <td><label>New birth date: </label></td>
                    <td><input type="date" v-model="userEdited.birthDate"></td>
                </tr>
                <tr>
                    <td>Gender:</td>
                    <td>{{ user.gender }}</td>
                    <td v-on:click="toggleEditField('gender')">Edit</td>
                </tr>
                <tr v-show="showEditFields.gender">
                    <td></td>
                    <td><label>New gender: </label></td>
                    <td><select name="gender" v-model="userEdited.gender">
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                     </select> </td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td>
                        <input type="submit" value="Submit"
                         >
                    </td>
                </tr>
            </table>
            </form>
        </div>
    `,
    mounted(){
        axios.get("api/profile/getProfile")
            .then(response => {
                 this.user = response.data;
                 this.userEdited = JSON.parse(JSON.stringify(response.data));
            });
    },
    methods: {
    toggleEditField(fieldName) {
        this.showEditFields[fieldName] = !this.showEditFields[fieldName];
    },
    updateUser : function(event) {
        event.preventDefault();
        axios.post("api/profile/update",this.userEdited)
            .then(respones => {
                this.user = respones.data;
            })

    }
}
})
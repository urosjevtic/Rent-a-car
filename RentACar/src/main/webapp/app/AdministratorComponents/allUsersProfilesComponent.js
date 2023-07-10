Vue.component("allProfiles", {
    data: function () {
        return {
            allUsers: {},
            users: {},
            searchParams: {
                username: '',
                name: '',
                lastName: '',
                role: '',
                customerType: ''
            },
            sortDirection: 'ascending'
        }
    },
    template: `
<div>
<div>
<button type="button" class="collapsible-search-bar"> Filters
        </button>
        <div class="content-collapsible-search-bar">
            <div class="object-search">
                <div class="object-search-params">
                    
                    <label class="object-search-header">Search by</label>
                    
                    <div class="param-group">
                        <input id="NameSearch" type="text" placeholder=" " v-model="searchParams.name">
                        <label for="NameSearch" >Name</label>
                    </div>
                    
                    
                    <div class="param-group">
                        <input id="LastNameSearch" type="text" placeholder=" " v-model="searchParams.lastName">
                        <label for="LastNameSearch" >Last name</label>
                    </div>
                    
                     <div class="param-group">
                        <input id="UserNameSearch" type="text" placeholder=" " v-model="searchParams.username">
                        <label for="UserNameSearch" >Username</label>
                    </div>
                    
                    
                </div>
                
                <div class="object-search-params fix-pos">
                    
                    <label class="object-search-header">Filter by</label>
                    <div class="param-group">
                        <select id="RoleSelect" v-model="searchParams.role">
                            <option value="" selected></option>
                            <option value="Administrator">Administrator</option>
                            <option value="Manager">Manager</option>
                            <option value="Customer">Customer</option>
                        </select>
                        <label>Role</label>
                    </div>    
                    
                    <div class="param-group">
                        <select id="CustomerType" v-model="searchParams.customerType">
                            <option value="" selected></option>
                            <option value="Gold">Gold</option>
                            <option value="Silver">Silver</option>
                            <option value="Bronze">Bronze</option>
                        </select>
                        <label>Customer Type</label>
                    </div>         
                         
                    
                </div>
               
                
                <div class="search-buttons">
                    <button class="search-clear" @click="clearFilters">Clear filters</button>
                    <button class="search-apply" type="button" @click="applyFilters">Apply filters</button>
                </div>
                
            </div>
        </div>
      </div>
    <div class="profile-search">
     

      <table class="greenTable">
        <thead>
          <tr>
             <th v-on:click="sortByUsername">
              Username
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th v-on:click="sortByName">
              Name
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th v-on:click="sortByLastName">
              Last name
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th>Birth date</th>
            <th>Gender</th>
            <th>Role</th>
            <th v-on:click="sortByPoints">
              Points
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th></th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="u in users">
            <td v-if="u.isSus !== true">{{u.username}}</td>
            <td v-else style="color: red; font-weight: bold" >{{u.username}}</td>
            <td>{{u.name}}</td>
            <td>{{u.lastName}}</td>
            <td>{{u.dateOfBirth}}</td>
            <td>{{u.gender}}</td>
            <td>
                <span class="dot" v-if="u.customerType === 'Gold'" style="margin-right: 2px; background: gold;"></span>
                <span class="dot" v-if="u.customerType === 'Silver'" style="margin-right: 2px; background: silver;"></span>
                <span class="dot" v-if="u.customerType === 'Bronze'" style="margin-right: 2px; background: #CD7F32;"></span>
                {{u.role}}
            </td>
            <td>{{u.points}}</td>
            <td>
                <button v-if="!u.isBlocked && u.role !== 'Administrator'" v-on:click="blockUser(u)">Block user</button>
                <button v-else-if="u.isBlocked && u.role !== 'Administrator'" v-on:click="unblockUser(u)">Unblock user</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
</div>
  `,
    mounted() {
        axios.get("api/profile/getAllProfiles")
            .then(response => {
                this.allUsers = response.data;
                this.users = response.data;
            });
        this.initCollapsible();
    },
    methods: {

        sortByName: function () {
            if (this.sortDirection === 'ascending') {
                let sortedUsers = this.users.sort((u1, u2) => (u1.name > u2.name) ? 1 : (u1.name < u2.name) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'descending';
            } else {
                let sortedUsers = this.users.sort((u1, u2) => (u1.name < u2.name) ? 1 : (u1.name > u2.name) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'ascending';
            }
        },
        sortByLastName: function () {
            if (this.sortDirection === 'ascending') {
                let sortedUsers = this.users.sort((u1, u2) => (u1.lastName > u2.lastName) ? 1 : (u1.lastName < u2.lastName) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'descending';
            } else {
                let sortedUsers = this.users.sort((u1, u2) => (u1.lastName < u2.lastName) ? 1 : (u1.lastName > u2.lastName) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'ascending';
            }
        },
        sortByUsername: function () {
            if (this.sortDirection === 'ascending') {
                let sortedUsers = this.users.sort((u1, u2) => (u1.username > u2.username) ? 1 : (u1.username < u2.username) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'descending';
            } else {
                let sortedUsers = this.users.sort((u1, u2) => (u1.username < u2.username) ? 1 : (u1.username > u2.username) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'ascending';
            }
        },
        sortByPoints: function () {
            if (this.sortDirection === 'ascending') {
                let sortedUsers = this.users.sort((u1, u2) => (u1.points > u2.points) ? 1 : (u1.points < u2.points) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'descending';
            } else {
                let sortedUsers = this.users.sort((u1, u2) => (u1.points < u2.points) ? 1 : (u1.points > u2.points) ? -1 : 0);
                this.users = sortedUsers;
                this.sortDirection = 'ascending';
            }
        },
        blockUser: function (selectedUser){
            axios.post("api/user/blockUser", selectedUser);
            selectedUser.isBlocked = true;
        },
        unblockUser: function (selectedUser){
            axios.post("api/user/unblockUser",selectedUser);
            selectedUser.isBlocked = false;
        },
        initCollapsible: function(){
            var coll = document.getElementsByClassName("collapsible-search-bar");
            var i;

            for (i = 0; i < coll.length; i++) {
                coll[i].addEventListener("click", function() {
                    this.classList.toggle("active");
                    var content = this.nextElementSibling;
                    if (content.style.maxHeight){
                        content.style.maxHeight = null;
                    } else {
                        content.style.maxHeight = content.scrollHeight + "px";
                    }
                });
            }
        },
        applyFilters: function() {
            this.users = this.allUsers;
            this.filterByRole();
            this.filterByCustomerType();
            this.searchName();
            this.searchLastName();
            this.searchUsername();
        },
        searchName: function(){
            if(this.searchParams.name !== null){
                const query = this.searchParams.name.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                    this.users = this.users.filter(user => user.name.toLowerCase().includes(query));
                }
            }
        },
        searchLastName: function(){
            if(this.searchParams.name !== null){
                const query = this.searchParams.lastName.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                    this.users = this.users.filter(user => user.lastName.toLowerCase().includes(query));
                }
            }
        },
        searchUsername: function(){
            if(this.searchParams.name !== null){
                const query = this.searchParams.username.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                    this.users = this.users.filter(user => user.username.toLowerCase().includes(query));
                }
            }
        },
        filterByRole:function ()
        {
            if(this.searchParams.role !== null){
                const query = this.searchParams.role.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                    this.users = this.users.filter(user => user.role.toLowerCase() === query);
                }
            }
        },
        filterByCustomerType:function ()
        {
            if(this.searchParams.customerType !== null){
                const query = this.searchParams.customerType;

                if(!this.isStringEmptyOrBlank(query)){
                    this.users = this.users.filter(user => user.customerType === query);
                }
            }
        },
        clearFilters: function(){
            for (let prop in this.searchParams) {
                if (this.searchParams.hasOwnProperty(prop)) {
                    this.searchParams[prop] = null;
                }
            }
            this.users = this.allUsers;
        },
        isStringEmptyOrBlank: function(str){
            return /^\s*$/.test(str);
        }
    }
});
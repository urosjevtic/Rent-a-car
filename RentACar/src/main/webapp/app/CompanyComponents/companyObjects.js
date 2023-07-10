Vue.component ("main_page",{
    data:function() {
        return{
        originalData: null,
        companies : null,
        searchParams : {
            name: null,
            vehicleType: null,
            city: null,
            minRating: null,
            onlyOpen: false,
            fuelType: null,
            sort: null,
            sortType: null,
        },
        currentLocation : {
            longitude: null,
            latitude: null
        }
    }},
    mounted(){

        this.initCities();
        axios.get('api/object/getAll')
            .then((response) => {
                this.originalData = response.data;
                this.companies = JSON.parse(JSON.stringify(this.originalData)); // make a copy not pass by ref
                this.initCollapsible();
                this.showObjects();
            });



    },
    template:`
    <div id="main_container">
    
    <div class="loader" id="loader"></div>
    
    <button type="button" class="collapsible-search-bar"> Filters
    </button>
    <div class="content-collapsible-search-bar">
        <div class="object-search">
            <div class="object-search-params">
                
                <label class="object-search-header">Search by</label>
                
                <div class="param-group">
                    <input id="NameSearch" v-model="searchParams.name" type="text" placeholder=" ">
                    <label for="NameSearch" >Name</label>
                </div>
                
                
                <div class="param-group">
                    <select id="CitySelect" @change="updateSelectClass" v-model="searchParams.city"></select>
                    <label>City</label>
                </div>
                
                <div class="param-group">
                    <input id="minRate" placeholder=" " type="number" min="1.0" max="5.0" v-model="searchParams.minRating"
                    step="0.25">
                    <label for="minRate" >Min rating</label>
                </div>
                
                <div class="param-group">
                    <input v-model="searchParams.onlyOpen" type="checkbox" placeholder=" ">
                    <label>Only Open</label>
                </div>
                
                
            </div>
            
            <div class="object-search-params fix-pos">
                
                <div class="param-group">
                    <select id="VehicleSelect" @change="updateSelectClass" v-model="searchParams.vehicleType">
                    <option value="Car">Car</option>
                    <option value="Van">Van</option>
                    <option value="Mobile_Home">Mobile Home</option>
                    </select>
                    <label>Vehicle type</label>
                </div>    
                
                <div class="param-group">
                    <select id="FuelSelect" @change="updateSelectClass" v-model="searchParams.fuelType">
                    <option value="petrol">Petrol</option>
                    <option value="diesel">Diesel</option>
                    <option value="hybrid">Hybrid</option>
                    <option value="electric">Electric</option>
                    </select>
                    <label>Vehicle Fuel Type</label>
                </div>         
                     
                
            </div>
            
            <div class="object-search-params fix-pos2">
            <label class="object-search-header">Sort by</label>
            
                <div class="param-group">
                    <select @change="updateSelectClass" id="SortSelect" v-model="searchParams.sort">
                    <option value="Name">Name</option>
                    <option value="Location">Location</option>
                    <option value="Rating">Rating</option>
                    </select>
                    <label>Sort by</label>
                </div> 
                
                <div class="param-group">
                    <select @change="updateSelectClass" id="SortTypeSelect" v-model="searchParams.sortType">
                    <option value="asc">Ascending</option>
                    <option value="des">Descending</option>
                    </select>
                    <label>Sort type</label>
                </div>   
                     
            </div>
            
            <div class="search-buttons">
                <button class="search-clear" @click="clearFilters">Clear filters</button>
                <button class="search-apply" type="button" @click="applyFilters">Apply filters</button>
            </div>
            
        </div>
    </div>
    
    <div class="grid-container" id="company-grid" style="visibility: hidden">
        
        <div class="grid-item" v-for="c in companies" v-on:click="showObjectInfo(c.id)" >
            <div class="item-image-section" :style="{'background-image': 'url(' + c.image + ')'}">
            </div>
            <div class="item-info-section">
            <form class="item-info">
            
            <div class="info-group">
                <label id="Name">{{c.name}}</label>
            </div>
            <div class="info-group">
                <label id="WrkHrs">Working hours: {{c.workingHours.from}} - {{c.workingHours.to}}</label>
            </div>
            <div class="info-group">
                <label id="Location">{{c.location.street + ', ' + c.location.streetNumber+ ', ' + c.location.city}}</label>
            </div>
            <div class="info-group">  
                        <label>Rating: </label>
                        <label v-if="c.rating == 0"> NOT RATED</label>
                        <label v-if="c.rating != 0">{{c.rating}}</label>
            </div>
            
            
            
            </form>
            </div>

        </div>

    </div>
    </div>
    
    `,
    methods: {
        showObjectInfo: function(objectId)
        {
            this.$router.push({path: '/ObjectInfo', query: {"id":objectId.toString()}});
        },
        showObjects: function()
        {
            let content = document.getElementById("company-grid");
            let loader = document.getElementById("loader");

            loader.style.visibility = 'hidden';
            content.style.visibility = 'visible';
        },
        async initCities(){

            let citySelect = document.getElementById('CitySelect');
            let response = await axios.get('api/object/getCities');

            let cities = response.data;

            cities.forEach((city) => {
                let option = new Option(city.city,city.city);
                citySelect.add(option,undefined);
            });


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
        updateSelectClass: function(event) {
            if (event.target.value !== null) {
                event.target.classList.add('valid-values');
            } else {
                event.target.classList.remove('valid-values');
            }
        },
        applyFilters: function() {
            this.companies = JSON.parse(JSON.stringify(this.originalData));
            this.searchName();
            this.searchCity();
            this.searchOpen();
            this.filterByFuelType();
            this.filterByVehicleType();
            this.searchMinRating();
            this.applySort();
        },
        async filterByVehicleType(){
            if(this.searchParams.vehicleType !== null)
            {
                let url = 'api/vehicle/objByVehicleType?vehicleType=' + this.searchParams.vehicleType;
                console.log(url);
                let response = await axios.get(url);
                let objects = this.convertObjectToList(response.data);
                console.log(objects);

                this.companies = this.companies.filter(company => objects.includes(company.id));
            }
        },
        async filterByFuelType() {
            if(this.searchParams.fuelType !== null)
            {
                let url = 'api/vehicle/objByFuelType?fuelType=' + this.searchParams.fuelType;
                let response = await axios.get(url);
                console.log(response.data);
                let objects = this.convertObjectToList(response.data);

                this.companies = this.companies.filter(company => objects.includes(company.id));

            }
        },
        convertObjectToList: function(object){
            return object.map(obj => obj.id);
        }
        ,
        searchName: function(){
            if(this.searchParams.name !== null){
                const query = this.searchParams.name.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                    this.companies = this.companies.filter(company => company.name.toLowerCase().includes(query));
                }
            }
        },
        searchOpen: function() {
            if(this.searchParams.onlyOpen === true){
                this.companies = this.companies.filter(company => company.open === true);
            }
        },
        searchCity: function() {
            if(this.searchParams.city !== null){
                const query = this.searchParams.city.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                  this.companies = this.companies.filter(company => company.location.city.toLowerCase().includes(query));
                }
            }
        },
        searchMinRating: function(){
            if(this.searchParams.minRating !== null){
                const query = Number(this.searchParams.minRating);
                if(query >= 0.0 && query <= 5.0){
                    this.companies = this.companies.filter(company => company.rating >= query);
                }
            }
        }
        ,
        applySort: function(){
            if(this.searchParams.sort !== null && this.searchParams.sortType !== null)
            {
                if(this.searchParams.sort === "Name") this.nameSort();
                if(this.searchParams.sort === "Location") this.locationSort();
                if(this.searchParams.sort === "Rating") this.ratingSort();
            }
        },
        ratingSort: function(){
            let sortType = 1;
            if(this.searchParams.sortType === "des") sortType = -1;

            this.companies.sort((a, b) => (b.rating - a.rating)*sortType);
        },
        nameSort: function(){
            let sortType = 1;
            if(this.searchParams.sortType === "des") sortType = -1;

            this.companies.sort(function(a,b) {
                let nameA = a.name.toUpperCase();
                let nameB = b.name.toUpperCase();

                if (nameA < nameB) {
                    return -1*sortType;
                }

                if (nameA > nameB) {
                    return sortType;
                }

                return 0;

            });

        },
        locationSort: function() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(this.locationSuccess, this.locationError);
            } else {
                console.error('Geolocation is not supported by this browser.');
            }
        },
        locationError: function(error) {
            console.error("Error occurred while retrieving the location:", error);
        },
        locationSuccess: function(position){
                this.currentLocation.latitude = position.coords.latitude;
                this.currentLocation.longitude = position.coords.longitude;
                this.locationSortByNearest();
        },
        locationSortByNearest: function() {
            let sortType = 1;
            if(this.searchParams.sortType === "des") sortType = -1;

            this.companies.sort((a, b) => {
                const distanceA = this.calculateDistance(a.location.latitude, a.location.longitude, this.currentLocation.latitude, this.currentLocation.longitude);
                const distanceB = this.calculateDistance(b.location.latitude, b.location.longitude, this.currentLocation.latitude, this.currentLocation.longitude);
                return (distanceA - distanceB)*sortType;
            });
        },
        calculateDistance: function(lat1,lng1,lat2,lng2){
            //Haversine formula, calculate distance between two coordinates on a sphere
            const R = 6371;
            const dLat = this.toRadians(lat2 - lat1);
            const dLon = this.toRadians(lng2 - lng1);

            const a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(this.toRadians(lat1)) * Math.cos(this.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

            const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return R * c;
        },
        toRadians: function(degrees){
            return degrees * (Math.PI / 180);
        },
        clearFilters: function(){
            for (let prop in this.searchParams) {
                if (this.searchParams.hasOwnProperty(prop)) {
                    this.searchParams[prop] = null;
                }
            }
            this.resetSelectClass();
            this.companies = JSON.parse(JSON.stringify(this.originalData));
        },
        isStringEmptyOrBlank: function(str){
            return /^\s*$/.test(str);
        },
        resetSelectClass: function(){
            let selectList = ['CitySelect','FuelSelect','VehicleSelect','SortSelect','SortTypeSelect'];

            selectList.forEach((select) => {
                let sel = document.getElementById(select);
                sel.classList.remove('valid-values');
            });
        }


    }


});
Vue.component ("object_info",{
    data:function() {
        return{
            object : {},
            isOpen : "OPEN",
            rateScore: "Not Rated",
            vehicles : [],
            comments : []
        }},
    template:`
    <div>
        <div class="loader" id="infoLoader"></div>
        <div id="infoContent" style="visibility: hidden">
        <div class="company-header">
            <div class="company-info">
            <label id="NameInfo">{{object.name}}</label>
            <label id="WrkHrsInfo">Working hours: {{object.workingHours.from}} - {{object.workingHours.to}} ({{isOpen}})</label>
            </div>  
            <img :src="object.image">
        </div> 
        <div class="company-section">  
             
             <div class="company-section-header">
             
                <div class="location-section">
                    <label id="LocationInfo">Find us at {{object.location.street + ', ' + object.location.streetNumber+ ', ' + object.location.city}}</label>
                    <div id="mapObjLoc"></div>
                </div>
                
                <div class="rating-section" >
                    <div class="rating-label">
                        <label>Rating: </label>
                        <label v-if="object.rating == 0"> NOT RATED</label>
                        <meter v-if="object.rating != 0" class="average-rating" :rating-value="object.rating" ></meter>
                    </div>
                    <div class="rating-comments">
                    
                        <div class="comment-container" v-for="c in comments">
                    
                            <label style="font-weight:bold" >{{c.username}} </label>
                            <label style="font-style:italic ">Comment </label>
                            <textarea readonly="readonly" rows="4" cols="50">{{c.comment.comment}}</textarea>
                            <label style="font-style:italic ">Rating </label>
                            <meter class="average-rating" :rating-value="c.comment.reviewScore" ></meter>
                        
                        </div>
                        
                    </div>
                </div>
                
                    
            </div>
            
            
            
            
            <button type="button" class="collapsible">Show Available Vehicles</button>
                <div class="content-collapsible">
                    <div class="vehicle-list">
                        <div class="vehicle" v-for="v in vehicles">
                            <img :src="v.image">
                            <div class="vehicle-info">
                                <label class="vehicle-info-name">{{v.make + ' ' + v.model}}</label>
                                <label class="vehicle-info-price">{{v.price + '$'}}</label>
                                <ul class="vehicle-info-stats">
                                    <li><b>Vehicle type: </b>{{v.vehicleType}}</li>
                                    <li><b>Transmission type: </b>{{v.transmissionType}}</li>
                                    <li><b>Fuel type: </b>{{v.fuelType}}</li>
                                    <li><b>Fuel consumption: </b>{{v.fuelConsumption}}</li>
                                    <li><b>Number of seats:</b>{{v.seatNumber}}</li>
                                    <li><b>Number of doors: </b>{{v.numberOfDoors}}</li>
                                </ul>
                            </div>
                            <div class="vehicle-info-desc">
                            <p class="vehicle-desc">{{v.description}}</p>
                            </div>
                        </div>
                        <div class="vehicle-end"></div>
                    </div>
                </div>
        </div>
        
        <div class="end"></div>
        </div>
    </div>
    
    `,
    mounted(){


        this.loadObject();



    },
    methods: {
        async loadObject() {
            let objectId = this.$route.query.id;
            let apiUrl = 'api/object/getById?id=' + objectId.toString();

            let response  = await axios.get(apiUrl);

            this.object = response.data;
            if(this.object.open === false) this.isOpen = "CLOSED";
            if(this.object.rating !== 0) this.rateScore = this.object.rating;
            this.$nextTick(() => {
                this.initMap();
                this.initCollapsible();
            });
            await this.loadVehicles(objectId);
            await this.initComments(objectId);
        },
        async loadVehicles(objectId) {
            let apiUrl = 'api/vehicle/getById?id=' + objectId.toString();

            let response = await axios.get(apiUrl);

            this.vehicles = response.data;
            this.showObjects();

            console.log(this.vehicles);
        },
        showObjects: function()
        {
            let content = document.getElementById("infoContent");
            let loader = document.getElementById("infoLoader");

            loader.style.visibility = 'hidden';
            content.style.visibility = 'visible';
        },
        async initComments(objectId){
            let response = await axios.get('api/comment/getComments?id='+ objectId);
            this.comments = response.data;
            this.$nextTick(() => {
            this.initRatingStars();
            });
        }
        ,
        initMap: function(){

            let map = L.map('mapObjLoc').setView([this.object.location.latitude, this.object.location.longitude], 10);

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
                maxZoom: 18,
            }).addTo(map);

            let marker = L.marker([this.object.location.latitude, this.object.location.longitude], { draggable: false }).addTo(map);


        },
        initCollapsible: function(){
            var coll = document.getElementsByClassName("collapsible");
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
        initRatingStars:function(){
            const meters = document.querySelectorAll('meter.average-rating');

            meters.forEach((meter) => {
                const ratingValue = meter.getAttribute('rating-value');
                meter.style.setProperty('--rating-value',  ratingValue);
            });
        }


    }


});
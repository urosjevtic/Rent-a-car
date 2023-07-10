Vue.component("createNewObject",{

    data:function(){
        return{
            buttonName: 'Next',
            selectedManager: null,
            name: null,
            from: null,
            to: null,
            imageBase64: null,
            imageFile: null,
            position: null,
        }
    },
    template:
    `    
        <div class="container-object">
            <div class="image-section-object"></div>
            <div class="new-object-section">
                <form class="object-form" @submit="sendObject" >
                
                    <div class="form-group">
                        <input id="Name" v-model="name" type="text" required/>
                        <label for="Name">Name</label>
                    </div>
                    
                    <div class="form-group">
                        <label class="loneLabel">Working hours</label>
                    </div>
                    <div class="form-group">
                        <label>From</label>
                        <input id="From" type="time" v-model="from" pattern="HH:mm" placeholder="HH:mm" required/>
                    </div>
                    <div class="form-group">
                        <label>To</label>
                        <input id="To" type="time" v-model="to" pattern="HH:mm" placeholder="HH:mm" required/>
                    </div>
                    
                    <div class="form-group">
                        <label id="imgLabel">Image</label>
                        <input id="Image" type="file" @change="fileUpload" accept="image/jpeg, image/png, image/gif" required/>
                    </div>
                    
                    <div class="form-group">
                        <label class="loneLabel">Location</label>
                    </div>
                    <div class="form-group">
                        <div id="map"></div>
                    </div>
                    
                    <div class="form-group">
                        <select id="select_manager" @change="updateButtonName()" v-model="selectedManager" required>
                        <option value="-1">--- Register New Manager ---</option>
                        </select>
                        <label for="select_manager">Manager</label>
                    </div>
                    
                    <input type="submit" v-model="buttonName"/>
                    
                </form>
            </div>
        </div>
    
        
    
    `,
    mounted(){
        this.initSelectManager();
        this.initMap();
    },
    methods:
        {
            sendObject: function(event)
            {
                event.preventDefault();
                if(this.buttonName == 'Save')
                {
                this.saveObject();
                }else
                {
                this.registerManger();
                }

            },
            async registerManger()
            {
                let object = await this.makeObject();
                this.$router.push({ name:'REGISTER_MANAGER', params: {"data": object}});

            }
            ,
            async makeObject()
            {
                this.imageToBase64();
                let location = await this.reverseGeocode();
                let workingHours = this.getWorkingHoursJSON();

                let object = {
                    "id" : "-1",
                    "name" : this.name,
                    "location" : location,
                    "image" : this.imageBase64,
                    "rating" : 0.0,
                    "workingHours" : workingHours,
                    "open" : true
                }

                return object;
            }
            ,
            async saveObject()
            {
                let object = await this.makeObject();

                let response = await axios.post('api/object/save',object);

                let updateObjectInManager = {
                    "managerId" : this.selectedManager.toString(),
                    "objectId" : response.data.id
                };

                await axios.post('api/user/updateObjectInManager',updateObjectInManager);

                this.$router.push('/');

            },
            getWorkingHoursJSON : function()
            {
                let from = this.from.toString();
                let to = this.to.toString();

                let workingHours = {
                    "from" : from,
                    "to" : to
                };

                return workingHours;
            }
            ,
            fileUpload : function(event){
                this.imageFile = event.target.files[0];
                this.imageToBase64();
            },
            imageToBase64 : function() {
                let reader = new FileReader();
                reader.onload = (event) => {
                    this.imageBase64 = event.target.result;
                };
                reader.readAsDataURL(this.imageFile);
            },
            async reverseGeocode()
            {
                let lat = this.position.lat;
                let lng = this.position.lng;
                let geocodeApi = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat.toString() + "&lon=" + lng.toString();

                let response = await axios.get(geocodeApi)

                let geoData = response.data;


                let location =  {
                    "street": geoData.address.road,
                    "streetNumber": geoData.address.house_number,
                    "city": geoData.address.city,
                    "zipCode": geoData.address.postcode,
                    "longitude": lng,
                    "latitude": lat
                    };


                return location;



            },
            updateButtonName: function()
            {
                this.buttonName="Next";
                if(this.selectedManager != -1){
                    this.buttonName = 'Save';
                }
            },

            initSelectManager: function(){

            const select = document.getElementById('select_manager');
            let managers = [];

            axios.get('api/user/freeManagers')
                .then((response) =>
                    {
                    managers = response.data;
                    managers.forEach((manager) =>
                        {
                            let option = new Option(manager.fullNameUsername,manager.id);
                            select.add(option,undefined);
                        } );

                    }
                );
            },
            initMap: function(){

                let map = L.map('map').setView([45.267136, 19.833549], 10);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
                    maxZoom: 18,
                }).addTo(map);

                let marker = L.marker([45.267136, 19.833549], { draggable: true }).addTo(map);
                this.position = marker.getLatLng();

                marker.on('dragend', (event) => {
                    let marker = event.target;
                    this.position = marker.getLatLng();
                });

                map.on('dblclick', (event) => {

                    const { lat, lng } = event.latlng;
                    this.position.lat = lat;
                    this.position.lng = lng;
                    marker.setLatLng([lat, lng]);


                });

            }


        }

    });
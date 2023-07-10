Vue.component("register_vehicle",{
    data: function() {
        return {
            vehicle: {
                make: null,
                model: null,
                price: null,
                vehicleType: null,
                transmissionType: null,
                fuelType: null,
                fuelConsumption: null,
                numberOfDoors: null,
                seatNumber: null,
                image: null,
                description: null,
                companyId: null,
                rented: false
            },
            imageFile: null,
            mode: null,
            newImage: false,

        }},
    template:
        `
    <div class="container-register-vehicle">
        <div class="vehicle-image-section"></div>
        <div class="register-vehicle-section">
            <form class="vehicle-form" @submit="saveVehicle">
            
                <div class="vehicle-form-group">
                <input v-model="vehicle.make" type="text" required>
                <label>Make</label>
                </div>
                
                <div class="vehicle-form-group">
                <input v-model="vehicle.model" type="text" required>
                <label>Model</label>
                </div>
                
                <div class="vehicle-form-group">
                <input v-model="vehicle.price" type="number" required>
                <label>Price</label>
                </div>
                
                <div class="vehicle-form-group">
                <select v-model="vehicle.vehicleType" required>
                    <option value="Car">Car</option>
                    <option value="Van">Van</option>
                    <option value="Mobile_Home">Mobile Home</option>
                </select>
                <label>Vehicle type</label>
                </div>
                
                <div class="vehicle-form-group">
                <select v-model="vehicle.transmissionType" required>
                    <option value="automatic">Automatic</option>
                    <option value="manual">Manual</option>
                </select>
                <label>Transmission type</label>
                </div>
                
                <div class="vehicle-form-group">
                <select v-model="vehicle.fuelType" required>
                    <option value="petrol">Petrol</option>
                    <option value="diesel">Diesel</option>
                    <option value="hybrid">Hybrid</option>
                    <option value="electric">Electric</option>
                </select>
                <label>Fuel type</label>
                </div>
                
                <div class="vehicle-form-group">
                    <input step="0.1" v-model="vehicle.fuelConsumption" type="number" required>
                    <label>Fuel consumption</label>
                </div>
                
                <div class="vehicle-form-group">
                    <input v-model="vehicle.numberOfDoors" type="number" required>
                    <label>Number of doors</label>
                </div>
                
                <div class="vehicle-form-group">
                    <input v-model="vehicle.seatNumber" type="number" required>
                    <label>Number of passengers</label>
                </div>
                
                <div v-if="mode==='edit'" class="vehicle-form-group">
                    <input type="checkbox" v-model="newImage">
                    <label>Load new image</label>
                </div>
               
                
                <div class="vehicle-form-group" v-if="mode!=='edit' || newImage">
                    <input id="fileInput" @change="fileUpload" accept="image/jpeg, image/png, image/gif" type="file" required>
                    <label>Image</label>
                </div>
                
                <div class="vehicle-form-group">
                    <input placeholder=" " v-model="vehicle.description" type="text">
                    <label>Description (optional)</label>
                </div>
                
                <input type="submit" content="Save vehicle">
                
            </form>
        </div>
    </div>
    
    `,
    mounted(){
        this.checkMode();
    },
    methods: {
        checkMode:function(){
            this.mode = this.$route.query.mode;
            if(this.mode === "edit")
            {
                this.vehicle = this.$route.params.data;


            }else{
                this.initObjectId();
            }
        },
        async initObjectId(){
            let response = await axios.get('api/user/getManagerObjectID');
            this.vehicle.companyId = response.data.id;
        },
        saveVehicle: function(event){
            event.preventDefault();
            if(this.mode === "edit"){
                //update vehicle
                axios.post('api/vehicle/update',this.vehicle)
                    .then((response) => {
                        if(response.status === 200){
                            alert("Vehicle updated successfully!");
                            setTimeout(this.$router.push('/Vehicles'),500);
                        }else{
                            alert("Error while updating vehicle!");
                        }
                    } )
                    .catch((error) => {
                        alert("Error while updating vehicle!");
                        console.log(error);
                    });

            }else{
            axios.post('api/vehicle/save',this.vehicle) //add new vehicle
                .then((response) => {
                    if(response.status === 200){
                        alert("Vehicle added successfully!");
                        setTimeout(this.$router.push('/Vehicles'),500);
                    }else{
                        alert("Error while adding new vehicle!");
                    }
                } )
                .catch((error) => {
                    alert("Error while adding new vehicle!");
                    console.log(error);
                });
            }
        },
        fileUpload : function(event){
            this.imageFile = event.target.files[0];
            this.imageToBase64();
        },
        imageToBase64 : function() {
            let reader = new FileReader();
            reader.onload = (event) => {
                this.vehicle.image = event.target.result;
            };
            reader.readAsDataURL(this.imageFile);
        },
    },

});
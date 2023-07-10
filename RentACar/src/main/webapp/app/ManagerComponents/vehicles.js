Vue.component("vehicles",{
    data: function() {
        return {
            vehicles: []
        }},
    template:
        `
        <div class="vehicle-list">
                        <div class="vehicle" v-for="v in vehicles">
                            <img :src="v.image">
                            <div class="vehicle-info" style="border: 0px">
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
                            <div class="button-separator">
                                <button class="vehicle-edit" @click="editVehicle(v)">Edit</button>
                                <button class="vehicle-delete" @click="deleteVehicle(v)">Delete</button>
                            </div>
                        </div>
          </div>          
    
    `,
    mounted(){
        this.initVehicles();
    },
    methods: {
        async initVehicles(){
            let id = await this.initObjectId();
            let url = 'api/vehicle/getById?id=' + id;
            let response = await axios.get(url);
            this.vehicles = response.data;
        },
        async initObjectId(){
            let response = await axios.get('api/user/getManagerObjectID');
            return response.data.id;
        },
        editVehicle: function(vehicle){
            this.$router.push({name: "REGISTER_VEHICLE", params: {data: vehicle}, query: {"mode" : "edit"}});
        },
        deleteVehicle: function(vehicle) {
            if( confirm("Are you sure that you want to delete this vehicle?") === true)
            {
                axios.post('api/vehicle/delete',vehicle)
                    .then((response) => {
                       if(response.status === 200){
                           alert("Vehicle deleted successfully!");
                           setTimeout(this.$router.go(),500);
                       }else{
                           alert("Error while deleting vehicle!");
                       }
                        }).catch((error) => {
                        alert("Error while deleting vehicle!");
                        console.error(error);
                        });
            }
        }
    },

});
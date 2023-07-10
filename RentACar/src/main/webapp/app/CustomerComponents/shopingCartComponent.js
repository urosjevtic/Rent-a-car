Vue.component("shoping_cart", {
    data: function (){
        return{
            cartItems: {},
            cartPrice: 0,
            messageStyle: {
                visibility: 'hidden',
                position: 'absolute'
            }
        }
    },
    template:
    `
    <div class="rentAcar_body">
        <div class="wrapper">
            <h1>Shopping Cart</h1>
            <div class="success-msg" v-bind:style="messageStyle">
               <i class="fa fa-check"></i>
               Vehicle successfuly removed from cart.
            </div>
            <div class="project">
                <div class="shop">
                    <div class="box" v-for="vehicle in cartItems">
                        <img :src="vehicle.image">
                        <div class="content">
                            <div class="company">
                                <label>{{vehicle.objectName}}</label>
                                <img :src="vehicle.objectLogo">
                            </div>
                            <h3>{{ vehicle.make }} {{ vehicle.model }}</h3>
                            <h4>$ {{ vehicle.price }}</h4>
                            <h4>Vehicel inforamtion:</h4>
                            <div class="information">
                                <div class="list-container">
                                    <ul>
                                        <li><b>Vehicel type:</b> {{ vehicle.vehicleType }}</li>
                                        <li><b>Transmision type:</b> {{ vehicle.transmissionType }}</li>
                                        <li><b>Fuel type:</b> {{ vehicle.fuelType }}</li>
                                        <li><b>Fuel consumption:</b> {{vehicle.fuelConsumption}}</li>
                                        <li><b>Number of seats:</b> {{ vehicle.seatNumber }}</li>
                                    </ul>
                                </div>
                                <div class="vertical-line"></div>
                                <div class="text-container">
                                    <p>{{vehicle.description}}</p>
                                </div>
                            </div>
                            <p class="btn-area" v-on:click="removeFromCart(vehicle)"><i aria-hidden="true" class="fa fa-trash"></i> <span class="btn2">Remove</span></p>
                        </div>
                    </div>
                </div>
                <div class="right-bar">
                    <p><b>Cart</b></p>
                    <p><span>Current price</span> <span>$ {{cartPrice}}</span></p>
                    <hr>
                   <a href="#" v-on:click="finalizeOrder">Finalize order</a>
                </div>
            </div>
        </div>
    </div>
    `,
    mounted()
    {
        axios.get("api/shopingCart/getItems")
            .then(response => {
                this.cartItems = response.data;
            });
        axios.get("api/shopingCart/getCurrentPrice")
            .then( response =>{
                this.cartPrice = response.data;
            })
    },
    methods: {
        removeFromCart(vehicle){
            axios.post("api/shopingCart/removeItem", vehicle)
                .then(response=>
                {
                    if(response.data === 'Item has been removed')
                    {
                        this.removeItemFromList(vehicle);
                        this.getCurrentPrice();
                        this.messageStyle.visibility = 'visible';
                        this.messageStyle.position = 'relative';

                        // Change messageStyle after 5 seconds
                        setTimeout(() => {
                            this.messageStyle.visibility = 'hidden';
                            this.messageStyle.position = 'absolute';
                        }, 5000);
                    }
                })
        },
        removeItemFromList(vehicle){
            const index = this.cartItems.findIndex((item) => item.id === vehicle.id);
            if (index !== -1) {
                this.cartItems.splice(index, 1);
            }
        },
        getCurrentPrice(){
            axios.get("api/shopingCart/getCurrentPrice")
                .then( response =>{
                    this.cartPrice = response.data;
                })
        },
        finalizeOrder(){
            axios.put("api/order/createOrder")
                .then(response => {
                    axios.delete("api/shopingCart/emptyCart");
                    if (response.data === "Order successfully created") {
                        console.log(response);
                        window.alert("Order successfully created");
                        this.$router.push({path: "/RentVehicle"});
                    } else {
                        console.log(response);
                        window.alert("Error, order not created");
                    }
                })
        }
    }
})
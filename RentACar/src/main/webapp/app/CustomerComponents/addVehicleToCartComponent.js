Vue.component('add_vehicle_to_cart', {
    data: function ()
    {
        return{
            vehicles: {},
            cartPrice: 0,
            cartDate: {},
            dateForRenting: {},
            orderStartDate: null,
            orderEndDate: null,
            orderObjectId: null,
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
                <div class="header">
            <div class="filter_bar">
                <div class="headers">
                    <h1 class="rc">Rent a car</h1>
                    <h2>Select date for your rentals</h2>
                </div>
                <div class="date_picker">

                    <div class="start_date">
                        <label for="rentdate">Start date</label>
                        <input name="rentdate" id="rentdate" type="date" v-model="dateForRenting.rentStartDate">
                    </div>
                    <div class="end_date">
                        <label for="rentdate">End date</label>
                        <input name="rentdate" id="rentdate" type="date" v-model="dateForRenting.rentEndDate">
                    </div>
                </div>
                <div class="filter_button">
                    <p class="btn-area-header" @click="emptyCart(); getVehicalsByDate(dateForRenting)"><i aria-hidden="true" class="fa fa-search"></i> <span class="btn2">Search</span></p>
                </div>
            </div>
        </div>
        <div class="success-msg" v-bind:style="messageStyle">
          <i class="fa fa-check"></i>
          Vehicle successfuly added to cart.
        </div>
        <div class="project">
            <div class="shop">
                <div class="box" v-for="vehicle in vehicles">
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
                        <p class="btn-area" v-on:click="addToCart(vehicle)"><i aria-hidden="true" class="fa fa-shopping-cart"></i> <span class="btn2">Add to cart</span></p>
                    </div>
                </div>
            </div>
            <div class="right-bar">
                <p><b>Cart</b></p>
                <p><span>Current price</span> <span>$ {{cartPrice}}</span></p>
                <hr>
               <a href="#" v-on:click="goToCheckout"><i class="fa fa-shopping-cart"></i>Checkout</a>
            </div>
        </div>
    </div>
</div>
    `,
    mounted()
    {
        axios.get("api/shopingCart/getCurrentPrice")
            .then( response =>{
                this.cartPrice = response.data;
            });

        axios.get("api/shopingCart/getOrderDate")
            .then( response =>{
                this.cartDate = response.data;
            });


        const url = window.location.href;
        const queryString = url.split('?')[1];
        const queryParams = new URLSearchParams(queryString);

        if (queryParams.has('rentStartDate') && queryParams.has('rentEndDate')) {
            const rentStartDate = queryParams.get('rentStartDate');
            const rentEndDate = queryParams.get('rentEndDate');
            this.dateForRenting.rentStartDate = rentStartDate;
            this.dateForRenting.rentEndDate = rentEndDate;
            this.getVehicalsByDate(this.dateForRenting);

            if(this.cartDate.rentStartDate.toString() != this.dateForRenting.rentStartDate.toString()
            || this.cartDate.rentEndDate.toString() != this.dateForRenting.rentEndDate.toString())
            {
                this.emptyCart();
            }

        } else {
            this.emptyCart();
            console.log("No query in the URL");
        }

    },
    methods: {
        addToCart(vehicle) {
            axios.post("api/shopingCart/addItemToCart", vehicle)
                .then(response => {
                    if (response.data === 'Added to cart') {
                        this.messageStyle.visibility = 'visible';
                        this.messageStyle.position = 'relative';

                        // Change messageStyle after 5 seconds
                        setTimeout(() => {
                            this.messageStyle.visibility = 'hidden';
                            this.messageStyle.position = 'absolute';
                        }, 5000);
                    }
                    this.removeItemFromList(vehicle);
                    return axios.get("api/shopingCart/getCurrentPrice");
                })
                .then(response => {
                    this.cartPrice = response.data;
                })
                .catch(error => {
                    console.error(error);
                });
        },
        getVehicalsByDate(dateForRenting)
        {
            const currentUrl = window.location.href;
            const objectId = currentUrl.split('/').pop();
            this.$router.push({path: "/AddToCart/" + objectId, query: {"rentStartDate":dateForRenting.rentStartDate, "rentEndDate": dateForRenting.rentEndDate}});
            this.orderStartDate = dateForRenting.rentStartDate;
            this.orderEndDate = dateForRenting.rentEndDate;

            const url = window.location.href;
            this.orderObjectId = url.split('/').pop().charAt(0);

            axios.post("api/vehicle/getAvailable/" + objectId, dateForRenting)
                .then(response => {
                    this.vehicles = response.data;
                })
                .catch(error => {
                    console.error(error);
                });
        },
        removeItemFromList(vehicle){
            const index = this.vehicles.findIndex((item) => item.id === vehicle.id);
            if (index !== -1) {
                this.vehicles.splice(index, 1);
            }
        },
        goToCheckout(){
            event.preventDefault(); // Prevent the default navigation behavior

            // Set data for order
            let setOrderData = {}
            setOrderData.rentStartDate = this.orderStartDate;
            setOrderData.rentEndDate = this.orderEndDate;
            setOrderData.objectId = this.orderObjectId;

            axios.post("api/shopingCart/setOrderData", setOrderData)
                .then(()=>{
                    this.$router.push({ path: "/ShopingCart" });
                })
                .catch(error => {
                    // Handle the error
                });



        },
        emptyCart()
        {
            axios.delete("api/shopingCart/emptyCart")
                .then(()=>{
                    this.cartPrice = 0;
                })
        }
        }
})
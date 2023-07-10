Vue.component('all_orders',{
    data: function()
    {
        return{
            allOrders: {},
            orders: {},
            sortDirection: 'ascending',
            searchParams : {
                objectName: null,
                priceFrom: null,
                priceTo: null,
                rentalStartDate: null,
                rentalEndDate: null
            }

        }
    },
    template:
        `
<div>
    <button type="button" class="collapsible-search-bar"> Filters
    </button>
    <div class="content-collapsible-search-bar">
        <div class="object-search">
            <div class="object-search-params" style="width: 30%">
                
                <label class="object-search-header">Search by</label>
                
                <div class="param-group">
                    <input id="ObjectSearch" type="text" placeholder=" " v-model="searchParams.objectName">
                    <label for="ObjectSearch" >Object name</label>
                </div>
                
                <div class="price-search" style="display: flex; flex-direction: row;">
                     <div class="param-group" style="width: 50%;">
                        <input id="PriceFrom" type="number" placeholder=" " v-model="searchParams.priceFrom">
                        <label for="PriceFrom">Price from</label>
                    </div>
                    
                     <div class="param-group" style="width: 50%; margin-left: 30px">
                        <input id="PriceTo" type="number" placeholder=" " v-model="searchParams.priceTo">
                        <label for="PriceTo">Price to</label>
                    </div>
                </div>
                
               <div class="date-search" style="display: flex; flex-direction: row;">
                     <div class="param-group" style="width: 50%;">
                        <input id="DateFrom" type="date" placeholder=" " v-model="searchParams.rentalStartDate">
                        <label for="DateFrom">Date from</label>
                    </div>
                    
                     <div class="param-group" style="width: 50%; margin-left: 30px">
                        <input id="DateTo" type="date" placeholder=" " v-model="searchParams.rentalEndDate">
                        <label for="DateTo">Date to</label>
                    </div>
                </div>
                
                
            </div>
            
            
            
            <div class="search-buttons">
                <button class="search-clear" @click="clearFilters">Clear filters</button>
                <button class="search-apply" type="button" @click="applyFilters">Apply filters</button>
            </div>
            
        </div>
    </div>
    <div class="profile-search">
  
      <table class="greenTable">
        <thead>
          <tr>
            <th>Unique Id</th>
            <th v-on:click="sortByObjectName">
              Rent a car object
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th>Rented vehicles</th>
            <th v-on:click="sortByRentStartDate">
              Rent start date
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th v-on:click="sortByRentEndDate">
              Rent end date
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th v-on:click="sortByPrice">
              Price
              <i v-if="sortDirection === 'ascending'" class="fas fa-sort-up"></i>
              <i v-else class="fas fa-sort-down"></i>
            </th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
  <tr v-for="order in orders" :key="order.uniqueId">
  <td>{{ order.uniqueId }}</td>
  <td>{{ order.objectName }}</td>
  <td>
    <span v-for="(vehicle, index) in order.vehicleNames" :key="index">
      {{ vehicle }}
      <span v-if="index < order.vehicleNames.length - 1">, </span>
    </span>
  </td>
  <td>{{ order.rentalStartDate }}</td>
  <td>{{ order.rentalEndDate }}</td>
  <td>{{ order.price }}</td>
  <td>{{ order.status }}</td>
  <td><p class="btn-area-header" v-if="order.status!=='Canceled' && order.status!=='Taken' && order.status !== 'Returned'" v-on:click="cancelOrder(order)"><i aria-hidden="true" class="fa fa-cancel"></i> <span class="btn2">Cancel</span></p></td>
  <td><button class="btn-area-header" v-if="order.isReviewable" type="button" @click="redirectToComment(order)"><span class="btn2">Leave comment</span></button></td>
</tr>
        </tbody>
      </table>
    </div>
    </div><button type="button" class="collapsible-search-bar"> Filters
    </button>
    </div>
    `,
    mounted(){
        axios.get("api/order/getCustomerOrders")
            .then(response =>
            {
                this.orders = response.data;
                this.allOrders = response.data
                this.initReviewable();
            });
        this.initCollapsible();
    },
    methods:{
        redirectToComment: function(order) {
                this.$router.push( {name:"COMMENT",params:{data: order}});
        },
        sortByObjectName: function () {
            if (this.sortDirection === 'ascending') {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.objectName > o2.objectName) ? 1 : (o1.objectName < o2.objectName) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'descending';
            } else {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.objectName < o2.objectName) ? 1 : (o1.objectNam > o2.objectName) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'ascending';
            }
        },
        sortByPrice: function () {
            if (this.sortDirection === 'ascending') {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.price > o2.price) ? 1 : (o1.price < o2.price) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'descending';
            } else {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.price < o2.price) ? 1 : (o1.price > o2.price) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'ascending';
            }
        },
        sortByRentStartDate: function () {
            if (this.sortDirection === 'ascending') {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.rentalStartDate > o2.rentalStartDate) ? 1 : (o1.rentalStartDate < o2.rentalStartDate) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'descending';
            } else {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.rentalStartDate < o2.rentalStartDate) ? 1 : (o1.rentalStartDate > o2.rentalStartDate) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'ascending';
            }
        },
        sortByRentEndDate: function () {
            if (this.sortDirection === 'ascending') {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.rentalEndDate > o2.rentalEndDate) ? 1 : (o1.rentalEndDate < o2.rentalEndDate) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'descending';
            } else {
                let sortedOrders = this.orders.sort((o1, o2) => (o1.rentalEndDate < o2.rentalEndDate) ? 1 : (o1.rentalEndDate > o2.rentalEndDate) ? -1 : 0);
                this.orders = sortedOrders;
                this.sortDirection = 'ascending';
            }
        },
        cancelOrder(order)
        {
            if(order.status == "Processing")
            {
                axios.post("api/order/cancel", order)
                    .then(()=>
                    {
                        this.setCancelStatus(order);
                    })
            }
        },
        setCancelStatus(order) {
            const index = this.orders.findIndex((item) => item.id === order.id);
            if (index !== -1) {
                this.orders[index].status = "Canceled";
            }
        },
        removeItemFromList(order){
            const index = this.orders.findIndex((item) => item.id === order.id);
            if (index !== -1) {
                this.orders.splice(index, 1);
            }
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
            this.orders = this.allOrders;
            this.searchByObjectName();
            this.searchByPrice();
            this.searchByDates();
        },
        searchByObjectName: function (){
            if(this.searchParams.objectName !== null){
                const query = this.searchParams.objectName.toLowerCase();

                if(!this.isStringEmptyOrBlank(query)){
                    this.orders = this.orders.filter(order => order.objectName.toLowerCase().includes(query));
                }
            }
        },
        searchByPrice: function() {
            const priceFrom = this.searchParams.priceFrom;
            const priceTo = this.searchParams.priceTo;

                this.orders = this.orders.filter(order => {
                    if (priceFrom !== null && priceTo !== null) {
                        return order.price >= priceFrom && order.price <= priceTo;
                    } else if (priceFrom !== null) {
                        return order.price >= priceFrom;
                    } else if (priceTo !== null) {
                        return order.price <= priceTo;
                    } else {
                        // Both prices are null, include all orders
                        return true;
                    }
                });
        },
        searchByDates: function() {
            const startDate = this.searchParams.rentalStartDate;
            const endDate = this.searchParams.rentalEndDate;

            if (startDate !== null || endDate !== null) {
                this.orders = this.orders.filter(order => {
                    const orderStartDate = new Date(order.rentalStartDate);
                    const orderEndDate = new Date(order.rentalEndDate);

                    if (startDate !== null && endDate !== null) {
                        // Convert the start and end dates to Date objects
                        const searchStartDate = new Date(startDate);
                        const searchEndDate = new Date(endDate);

                        // Compare the order's dates with the search dates
                        return orderStartDate >= searchStartDate && orderEndDate <= searchEndDate;
                    } else if (startDate !== null) {
                        const searchStartDate = new Date(startDate);
                        return orderStartDate >= searchStartDate;
                    } else if (endDate !== null) {
                        const searchEndDate = new Date(endDate);
                        return orderEndDate <= searchEndDate;
                    }

                    return false; // Neither start nor end date specified
                });
            }
        },
        isStringEmptyOrBlank: function(str){
            return /^\s*$/.test(str);
        },
        clearFilters: function(){
            for (let prop in this.searchParams) {
                if (this.searchParams.hasOwnProperty(prop)) {
                    this.searchParams[prop] = null;
                }
            }
            this.orders = this.allOrders;
        }



    }
})
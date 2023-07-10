Vue.component ("rent_vehicle",{
    data:function() {
        return{
            companies : null
        }},
    mounted(){
        axios.get('api/object/getAll')
            .then((response) => {
                this.companies = response.data;
                this.showObjects();
            })

    },
    template:`
    <div id="main_container">
    <div class="title_area">
        <h1 style="text-align: center; color: white">Select rent a car object</h1>
    </div>
   
    <div class="loader" id="loader"></div>
    
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
                <label id="Rating">Rating: {{c.rating}}</label>
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
            this.$router.push({path: '/AddToCart/' + objectId});
        },
        showObjects: function()
        {
            let content = document.getElementById("company-grid");
            let loader = document.getElementById("loader");

            loader.style.visibility = 'hidden';
            content.style.visibility = 'visible';
        },
    }


})
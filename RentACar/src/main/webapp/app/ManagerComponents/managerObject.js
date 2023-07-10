Vue.component("manager_object",{
    data: function() {
        return {
            refresh: true
        }},
    template:
        `
    <div>
    
        <object_info>
    
        </object_info>
        
        <div style="width: 90%; margin-left:auto; margin-right:auto; margin-top:-150px; padding-bottom: 200px; ">
        
        <button style="" type="button" class="collapsible">Show Orders</button>
                <div class="content-collapsible" >
                    <orders  ></orders>
                </div>
        </div>
       
        
       
        
    </div>
    
    
    
    `,
    mounted(){
        this.initObjectId();
    },
    methods: {
        async initObjectId(){

            let newRefresh = this.$route.query.re;


            if(newRefresh !== undefined &&  newRefresh === 'false') {
                this.refresh = false;
            }

            if(this.refresh){
            let response = await axios.get('api/user/getManagerObjectID');
            this.$router.push('/ManagerObject?id=' + response.data.id + '&re=' + false);
            this.$router.go();
            }

        }
    }

});
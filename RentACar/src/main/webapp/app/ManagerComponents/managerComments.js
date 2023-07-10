Vue.component('manager_comments',{
    data: function(){
        return {
            pendingComments: null,
            acceptedComments: null,
            deniedComments: null
        }},
    template:
        `
        <div class="manager_comments">
        
            <div class="comment_pending">
                <label>Pending comments</label>
                
                <div class="comment_container" v-for="c in pendingComments" >
                    <label>User {{c.username}} </label>
                    <label>Comment </label>
                    <textarea readonly="readonly" rows="4" cols="50">{{c.comment.comment}}</textarea>
                    <div class="rating_n_buttons">
                     
                        <div class="rating">
                            <label>Rating </label>
                            <meter class="average-rating" :rating-value="c.comment.reviewScore" ></meter>
                        </div>
                        
                        <div class="buttons">
                            <button id="Den" @click="updateStatus(c.comment,'Denied')">Deny</button>
                            <button id="Acc" @click="updateStatus(c.comment,'Accepted')">Accept</button>
                        </div>
                        
                    </div>
                </div>    
                           
            </div>
            
            <div class="comment_accepted">
                <label>Accepted comments</label>
                <div class="comment_container" v-for="c in acceptedComments" >
                    <label>User {{c.username}} </label>
                    <label>Comment </label>
                    <textarea readonly="readonly" rows="4" cols="50">{{c.comment.comment}}</textarea>
                    <div class="rating_n_buttons">
                     
                        <div class="rating">
                            <label>Rating </label>
                            <meter class="average-rating" :rating-value="c.comment.reviewScore" ></meter>
                        </div>
                        
                        
                        
                    </div>
                </div>  
            </div>
            
            <div class="comment_denied">
                <label>Denied comments</label>
                <div class="comment_container" v-for="c in deniedComments" >
                    <label>User {{c.username}} </label>
                    <label>Comment </label>
                    <textarea readonly="readonly" rows="4" cols="50">{{c.comment.comment}}</textarea>
                    <div class="rating_n_buttons">
                     
                        <div class="rating">
                            <label>Rating </label>
                            <meter class="average-rating" :rating-value="c.comment.reviewScore" ></meter>
                        </div>
                        
                        
                        
                    </div>
                </div>  
            </div>
        </div>   
        `,
    mounted(){
        this.initComments();

    },
    methods: {
        async initComments() {
            let objectId = await this.initObjectId();

            let responsePending = await axios.get('api/comment/getPending?id=' + objectId);
            let responseAccepted = await axios.get('api/comment/getAccepted?id=' + objectId);
            let responseDenied = await axios.get('api/comment/getDenied?id=' + objectId);

            this.pendingComments = responsePending.data;
            this.acceptedComments = responseAccepted.data;
            this.deniedComments = responseDenied.data;

            this.$nextTick(() => {
            this.initRatingStars();
            });

        },
        async initObjectId(){
            let response = await axios.get('api/user/getManagerObjectID');
            return response.data.id;
        },
        updateStatus(comment,newStatus){

            let sendUpdate = {
                "commentId" : comment.id.toString(),
                "objectId" : comment.companyId.toString(),
                "newCommentStatus" : newStatus.toString()
            };

            axios.post('api/comment/updateStatus',sendUpdate)
                .then((response)=> {
                    if(response.status === 200){
                        alert("Comment status successfully change to: " + newStatus);
                        this.$router.go();
                    }else{
                        alert("Error while updating comment status!");
                    }
                })
                .catch((error)=> {
                    alert("Error while updating comment status!");
                    console.log(error);
                });


        },
        initRatingStars:function(){
            const meters = document.querySelectorAll('meter.average-rating');

            meters.forEach((meter) => {
                const ratingValue = meter.getAttribute('rating-value');
                meter.style.setProperty('--rating-value',  ratingValue);
            });
        }

    },
});
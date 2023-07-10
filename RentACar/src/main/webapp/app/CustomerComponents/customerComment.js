Vue.component('comment',{
    data: function (){
        return{
            comment: {
                comment: null,
                reviewScore: null,
                uniqueId: null,
            }
        }},
    template:
        `
        <div class="container-comment" >
            <div class="comment-image-section"></div>
            <div class="comment-section">
                <form class="comment-form" @submit="saveComment" >
                

                    <div class="comment-form-group">
                        <textarea v-model="comment.comment" rows="4" cols="50" required></textarea>
                        <label id="commentLab">Comment</label>
                    </div>
                    
                    <div class="comment-form-group">
                        <label id="ratingLab">Rating:</label>
                    </div>
                    
                    <div class="star-rating">
                        <input @click="setReviewScore(5)" class="radio-input" type="radio" id="star5" name="star-input" value="5" required />
                        <label class="radio-label" for="star5" title="5 stars">5 stars</label>

                        <input @click="setReviewScore(4)" class="radio-input" type="radio" id="star4" name="star-input" value="4" />
                        <label class="radio-label" for="star4" title="4 stars">4 stars</label>

                        <input @click="setReviewScore(3)" class="radio-input" type="radio" id="star3" name="star-input" value="3" />
                        <label class="radio-label" for="star3" title="3 stars">3 stars</label>

                        <input @click="setReviewScore(2)" class="radio-input" type="radio" id="star2" name="star-input" value="2" />
                        <label class="radio-label" for="star2" title="2 stars">2 stars</label>

                        <input @click="setReviewScore(1)" class="radio-input" type="radio" id="star1" name="star-input" value="1" />
                        <label class="radio-label" for="star1" title="1 star">1 star</label>
                    </div>
                    
                    <input content="Send comment" type="submit">
                </form>
            </div>
        </div>
        
        `,
    mounted(){
        this.initOrder();
    },
    methods:{
        initOrder:function() {
            this.comment.uniqueId = this.$route.params.data.uniqueId;
        },
        setReviewScore: function(score){
            this.comment.reviewScore = Number(score);
        },
        saveComment: function(event){
            event.preventDefault();
            axios.post('api/comment/save',this.comment)
                .then((response) => {
                    if(response.status === 200){
                        alert("Comment sent successfully!");
                        this.$router.push('/AllOrders');
                    }else{
                        alert("Error while sending comment!");
                    }
                })
                .catch((error) => {
                    alert("Error while sending comment!");
                    console.log(error);
                });

        }
    },
});
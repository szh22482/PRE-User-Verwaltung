<template>
    <div class="profile-picture" :style="generateRandomColor()">
        {{ generateInitials() }}
    </div>
</template>

<script>
    export default {
        data() {
            return {
                colors: {
                        0: {bg: '#dfe1ff', text: '#6264a7'},
                        1: {bg: '#FFE7E7', text: '#944E63'},
                        2: {bg: '#7FC7D9', text: '#365486'},
                        3: {bg: '#F8FAE5', text: '#43766C'},
                        4: {bg: '#F2AFEF', text: '#33186B'},
                        5: {bg: '#F3EDC8', text: '#BF3131'},
                        6: {bg: '#ECF4D6', text: '#265073'},
                        7: {bg: '#F3F3F3', text: '#860A35'},
                        8: {bg: '#739072', text: '#3A4D39'},
                        9: {bg: '#E5C3A6', text: '#2E4374'},
                }
            }
        },
        props: {
            firstname: String,
            lastname: String,
            colorNr: Number,
        },
        methods: {
            generateInitials() {
                const firstInitial = this.firstname ? this.firstname[0].toUpperCase() : '';
                const lastInitial = this.lastname ? this.lastname[0].toUpperCase() : '';
                return firstInitial + lastInitial;
            },
            generateRandomColor() {
                //use the color used by the user if it is set otherwise generate a random color
                const randomIndex = this.colorNr != null ? this.colorNr : Math.floor(Math.random() * 10);
                const randomColor = this.colors[randomIndex];
                return {
                    background: randomColor.bg,
                    color: randomColor.text
                };
            }
        }
    }
</script>

<style>
    .profile-picture {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        font-size: 12px;
        text-align: center;
        line-height: 40px;
        margin: 10px 10px 10px 0;
        box-shadow: rgba(0, 0, 0, 0.16) 0px 1px 4px;
    }
</style>
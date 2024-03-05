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
                        0: {bg: '#d3cfe8', text: '#28214d'}, //
                        1: {bg: '#ffd3c8', text: '#7c2513'}, //
                        2: {bg: '#c3d4dc', text: '#002833'}, //
                        3: {bg: '#e7eedd', text: '#4a6137'}, //
                        4: {bg: '#cdd9dc', text: '#03171c'}, //
                        5: {bg: '#f9d8df', text: '#72283b'}, //
                        6: {bg: '#c8ebf2', text: '#005562'}, //
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
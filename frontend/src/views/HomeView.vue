<template>
    <div :style="{ 'margin-left': marginWidth }">
        <v-card-title class="ps-4 pb-0 mb-0 title">USERS</v-card-title>
        <v-card-subtitle class="ps-4 pt-0 mt-0 mb-5 subtitle">view and manage users</v-card-subtitle>

        <v-hover>
        <template v-slot:default="{ props }">
            <v-tabs v-model="tab" align-tabs="start" class="mx-5 tabs" v-bind="props">
            <v-tab 
                value="active" :ripple="false" class="active-tab">Active</v-tab>
            <v-tab 
                value="deleted" :ripple="false" class="deleted-tab">Deleted</v-tab>
            <v-tab 
                value="invited" :ripple="false" class="invited-tab">Invited</v-tab>
            </v-tabs>
        </template>
        </v-hover>

        <v-window v-model="tab">
        <v-window-item value="active" :transition="false" reverse-transition="false">
            <Home />
        </v-window-item>

        <v-window-item value="deleted" :transition="false" reverse-transition="false">
            Deleted
        </v-window-item>

        <v-window-item value="invited" :transition="false" reverse-transition="false">
            Invited
        </v-window-item>
        </v-window>
    </div>
</template>

<script setup>
import Home from './Home.vue';
</script>

<script>
    export default {
        data() {
            return {
                tab: 'active',
                isSmallScreen: false,
                marginWidth: '140px'
            };
        },
        async mounted() {
            this.checkScreenSize();
            window.addEventListener('resize', this.checkScreenSize);
        },
        beforeDestroy() {
         window.removeEventListener('resize', this.checkScreenSize);
        },
        methods: {
            checkScreenSize() {
                this.isSmallScreen = window.innerWidth < 960;
                this.marginWidth = this.isSmallScreen ? '-40px' : '0px';
            },
        }
    };
</script>

<style>
    .title {
        font-size: 24px;
        font-weight: 400;
        margin-top: 50px;
    }

    .subtitle {
        font-size: 18px;
    }

    .tabs {
        display: flex !important;
        justify-content: space-between !important;
        border-bottom: 1px solid gray;
    }

    .active-tab,
    .deleted-tab,
    .invited-tab {
        flex: 1;
        text-align: center;
    }
</style>

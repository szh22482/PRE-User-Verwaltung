<template>
    <v-btn 
    class="toggle-btn" 
    v-if="!drawer && isSmallScreen" 
    @click="toggleDrawer" 
    variant="plain" 
    icon="mdi-menu" 
    :ripple="false"/> 

    <div v-if="isSmallScreen && drawer" :style="{'margin-right': '48px'}"></div>

    <div :style="{ 'margin-right': marginWidth }">
      <v-navigation-drawer width="200" v-model="drawer">

        <v-btn 
        class="close-btn" 
        v-if="isSmallScreen" 
        @click="toggleDrawer" 
        variant="plain" 
        icon="mdi-close" 
        :ripple="false" /> 

        <v-list-item class="nav-item first-item">
          <router-link class="router-link" v-bind:to="`/account`">
            <v-btn 
            prepend-icon="mdi-cog" 
            :ripple="false" 
            variant="text" 
            size="large">
                <span class="font-weight-bold">ACCOUNT</span>
            </v-btn>
          </router-link>
        </v-list-item>

        <v-list-item class="nav-item">
          <router-link class="router-link" v-bind:to="`/home`">
            <v-btn 
            prepend-icon="mdi-account-multiple" 
            :ripple="false" 
            variant="text" 
            size="large">
                <span class="font-weight-bold">USERS</span>
            </v-btn>
          </router-link>
        </v-list-item>

        <v-list-item class="nav-item">
          <router-link class="router-link" v-bind:to="`/add`">
            <v-btn 
            prepend-icon="mdi-plus" 
            :ripple="false" 
            variant="text" 
            size="large">
                <span class="font-weight-bold">ADD USER</span>
            </v-btn>
          </router-link>
        </v-list-item>

        <v-list-item class="logout">
          <v-btn 
          prepend-icon="mdi-logout" 
          :ripple="false" 
          variant="text" 
          size="large" 
          @click="logout()">
            Log out</v-btn>
        </v-list-item>
      </v-navigation-drawer>
    </div>
</template>

<script setup>
  import axios from 'axios';
</script>

<script>
    export default {
        data() {
            return {
                drawer: true,
                isSmallScreen: false,
                marginWidth: '70px',
            }
        },
        async mounted() {
          this.checkScreenSize();
          window.addEventListener('resize', this.checkScreenSize)
          if(this.isSmallScreen) this.drawer = false;
        },
        beforeUnmount() {
            window.removeEventListener('resize', this.checkScreenSize);
        },
        methods: {
          toggleDrawer() {
              this.drawer = !this.drawer;
          },

          checkScreenSize() {
              this.isSmallScreen = window.innerWidth < 1280;
              this.marginWidth = this.isSmallScreen ? '0px' : '70px';
          },
        }
    }
</script>

<style>
    .toggle-btn {
        position: relative;
        left: 35px;
        top: 20px;
    }

    .close-btn {
        margin-top: 20px;
        position: relative;
        left: 140px;
        margin-bottom: -50px;
    }

    .first-item {
        margin-top: 70px !important;
    }

    .nav-item {
        margin-block: 10px;
    }

    .logout {
        position: fixed !important;
        bottom: 40px !important;
        z-index: 10;
    }

    .router-link {
      text-decoration: none !important;
      color: #212121;
    }
</style>
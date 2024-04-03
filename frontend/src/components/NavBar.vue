<template>
  <div v-if="showNavbar">
    <div v-if="isSmallScreen && drawer"
      :style="{'margin-bottom': '48px'}">
      </div>
      <v-btn 
      class="toggle-btn" 
      v-if="!drawer && isSmallScreen" 
      @click="toggleDrawer" 
      variant="plain" 
      icon="mdi-menu" 
      :ripple="false"
      /> 

      <div 
      v-if="isSmallScreen && drawer" 
      :style="{'margin-right': '48px'}">
      </div>

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
              <v-hover>
                <template v-slot:default="{ isHovering, props }">
                  <v-btn 
                  v-bind="props"
                  prepend-icon="mdi-cog" 
                  :ripple="false" 
                  variant="text" 
                  size="large"
                  width="150"
                  :style="{ 'border-left': isHovering ? 'solid 5px black' : 'none' }">
                    <span class="font-weight-bold">ACCOUNT</span>
                  </v-btn>
                </template>
              </v-hover>
            </router-link>
            
          </v-list-item>
          <v-list-item class="nav-item">
            <router-link class="router-link" v-bind:to="`/home`">
              <v-hover>
                <template v-slot:default="{ isHovering, props }">
                  <v-btn 
                  v-bind="props"
                  prepend-icon="mdi-account-multiple" 
                  :ripple="false" 
                  variant="text" 
                  size="large"
                  width="150"
                  class="nav-btn"
                  :style="{ 'border-left': isHovering ? 'solid 5px black' : 'none' }">
                    <span class="font-weight-bold">USERS</span>
                  </v-btn>
                </template>
              </v-hover>
            </router-link>
          </v-list-item>

          <v-list-item class="logout">
            <v-hover>
                <template v-slot:default="{ isHovering, props }">
                  <v-btn 
                    v-bind="props"
                    prepend-icon="mdi-logout" 
                    :ripple="false" 
                    variant="text"
                    size="large" 
                    width="150"
                    :style="{ 'border-left': isHovering ? 'solid 5px black' : 'none' }"
                    @click="logout()">Log out</v-btn>
                </template>
              </v-hover>
          </v-list-item>
      </v-navigation-drawer>
    </div>
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
              isAdmin: false,
          }
      },
      props: {
        showNavbar: Boolean
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
        async logout() {
          
        },
      }
  }
</script>

<style>
  .toggle-btn {
      position: relative;
      left: 20px;
      top: 5px;
  }

  .close-btn {
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

  .nav-btn {
    padding-left: 0 !important;
  }
</style>
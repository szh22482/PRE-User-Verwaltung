<template>
  <v-layout class="layout">
    <NavBar />
    <v-main class="d-flex justify-start flex-column ps-5 main-content"
    :style="{ 'margin-left': marginWidth }">
      <router-view />
    </v-main>
  </v-layout>
</template>

<script setup>
  import NavBar from '@/components/NavBar.vue'
</script>

<script>
  export default {
    data() {
      return {
        drawer: true,
        isSmallScreen: false,
        marginWidth: '200px',
        isSmallScreen: false,
      }
    },
    async mounted() {
      this.checkScreenSize();
      window.addEventListener('resize', this.checkScreenSize)
      if(this.isSmallScreen) this.drawer = false;
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.checkScreenSize);
    },
    methods: {
      toggleDrawer() {
        this.drawer = !this.drawer;
      },
      checkScreenSize() {
        this.isSmallScreen = window.innerWidth < 1280;
        this.colapseDate = window.innerWidth < 750;
        this.colapseRole = window.innerWidth < 560;
        this.marginWidth = this.isSmallScreen ? '0px' : '140px';
      },
    }
  }
</script>

<style scoped>
  .layout {
    display: flex !important;
    flex-direction: row;
  }

  .main-content {
    flex-grow: 1;
  }
</style>
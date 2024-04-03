<template>
  <v-layout class="layout">
    <NavBar :showNavbar="showNavbar" />
    <v-main class="d-flex justify-start flex-column px-3 main-content" 
    :style="{ 'margin-left': marginWidth }">
      <router-view />
    </v-main>
  </v-layout>
</template>

<script>
import NavBar from '@/components/NavBar.vue';

export default {
  components: {
    NavBar
  },
  data() {
    return {
      drawer: true,
      isSmallScreen: false,
      marginWidth: '200px',
      showNavbar: false
    };
  },
  async mounted() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize);
    this.checkRoute();
    this.$router.afterEach((to) => {
      this.checkRoute(to);
    });
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
      this.marginWidth = this.isSmallScreen ? '0px' : '140px';
    },
    checkRoute(route) {
      const currentRoute = route || this.$route;
      if (currentRoute.name === 'Add' || currentRoute.name === 'Login') {
        this.showNavbar = false;
      } else {
        this.showNavbar = true;
      }
    }
  }
};
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

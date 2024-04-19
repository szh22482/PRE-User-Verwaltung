// Composables
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '',
    name: 'Login',
    component: () => import( '@/views/LoginView.vue'),
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import( '@/views/HomeView.vue'),
  },
  {
    path: '/add',
    name: 'Add',
    component: () => import( '@/views/AddUser.vue'),
  },
  {
    path: '/invite',
    name: 'Invite',
    component: () => import( '@/views/InviteUser.vue'),
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

export default router

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

import axios from 'axios'

//use http for now 
axios.default.baseURL = process.env.NODE_ENV == 'production' ? '/api' : 'http://localhost:8080/api'

// Plugins
import { registerPlugins } from '@/plugins'

const app = createApp(App)

registerPlugins(app)

app.mount('#app')

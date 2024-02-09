<template>
  <v-container fluid class="container">
    <div class="title">Users</div>
    <div class="subtitle">view and manage users</div>

    <v-data-table 
      class="table rounded-lg"
      :items="users"
      :headers="dynamicHeaders"
    >
      <template v-slot:headers>
        <tr class="table-head">
          <th>User</th>
          <th v-if="!isSmallScreen">Created</th>
          <th>Roles</th>
          <th></th>
        </tr>
      </template>

      <template v-slot:item.fullname="{ item }">
        <div class="align-horizonally">
          <profile-picture v-if="!colapse"
            :firstname="item.firstname"
            :lastname="item.lastname"
            :colorNr="item.colorNumber"
          />
          <div>
            <span class="username collapsible-text" :class="{'collapsible-small-screen': colapse}">
              {{ item.firstname }} {{ item.lastname }}
            </span>
            <span class="collapsible-text email" :class="{'collapsible-small-screen': colapse}">
              {{ item.email }}
            </span>
        </div>
      </div>
      </template>

      <template v-slot:item.created="{ item }">
        {{ new Date(item.created).toLocaleDateString("de-DE") }}
      </template>

      <template v-slot:item.roles="{ item }">
        <div v-if="item.roles && item.roles.length > 0">
          <v-chip 
            class="role" 
            v-for="(role, index) in item.roles" 
            :key="index"
            :style="roleStyle(role)"
          >
            {{ role }}
          </v-chip>
        </div>
      </template>

      <template v-slot:item.actions="{ item }">
        <div class="align-horizonally">
          <v-icon>mdi-dots-vertical</v-icon>
        </div>
      </template>

      <template v-slot:bottom> </template> <!-- removes the default footer -->
    </v-data-table>
  </v-container>
</template>

<script>
import axios from "axios";
import ProfilePicture from "@/components/ProfilePicture.vue";
export default {
  components: {
    ProfilePicture
  },
  data() {
    return {
      isSmallScreen: false,
      colapse: false,
      users: [],
      roleColors: {
        Administrator: { background: '#e2ecf7', color: '#1c6ac1' },
        Auditor: { background: '#e8f5e9', color: '#5fb762' },
        Auditee: { background: '#dff6f9', color: '#00bcd4' },
        Reporter: { background: '#fde7e5', color: '#f44336' },
        Gast: { background: '#fff2df', color: '#ff9800' },
        'Manual writer': { background: '#d1c4e9', color: '#7e57c2' }
      }
    }
  },
  computed: {
    dynamicHeaders() {
      let headers = [
        { text: 'User', value: 'fullname', sortable: true},
        ...(!this.isSmallScreen ? [{ text: 'Created', value: 'created', sortable: true}] : []),
        { text: 'Roles', value: 'roles', sortable: false},
        { text: '', value: 'actions', sortable: false, width: '30px', sortable: false},
      ];
      return headers;
    },
  },
  async mounted() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize)
    try {
      const response = await axios.get("/users/all");
      if(response != null) {
        this.users = response.data;
      }
      else {
        alert("No data found");
      }
    } catch (e) {
      alert(e)
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkScreenSize);
  },
  methods: {
    checkScreenSize() {
      this.isSmallScreen = window.innerWidth < 960;
      this.colapse = window.innerWidth < 650;
      this.marginWidth = this.isSmallScreen ? '0px' : '70px';
    },
    roleStyle(role) {
      const defaultStyle = { background: '#eeeeee', color: '#000000' };
      return this.roleColors[role] || defaultStyle;
    }
  }
}
</script>

<style>
  .title {
    color: black;
    font-size: 30px;
  }

  .subtitle {
    color: black;
    font-size: 20px;
    margin-top: -10px;
    margin-bottom: 20px;
  }

  .table {
    border: solid 1px #e0e0e0;
  }

  .table-head {
    background-color: #f9fafc;
  }

  .role {
    margin-right: 5px;
    margin-block: 5px;
  }

  .container {
    margin: 20px;
  }
  .align-horizonally {
    display: flex;
    align-items: center; 
  }

  .username {
    font-weight: bold;
    font-size: 16px;  
  }

  .email {
    font-size: 14px;
    color: #707070;
  }

  .collapsible-text {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
  }

  .collapsible-small-screen {
    /* 
    TODO: width should gradually decrease with the screen size
    */
    max-width: 95px;
  }
</style>
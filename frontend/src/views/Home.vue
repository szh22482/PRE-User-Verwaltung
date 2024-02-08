<template>
  <v-container fluid class="container">
    <v-data-table
      :items="users"
      :headers="dynamicHeaders"
    >
      <template v-slot:headers>
        <tr>
          <th class="text-left table-head">User</th>
          <th v-if="!isSmallScreen" class="text-left table-head">Created</th>
          <th class="text-left table-head">Roles</th>
          <th class="text-left table-head"></th>
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
            <span class="collapsible-text" :class="{'collapsible-small-screen': colapse}">
              {{ item.email }}
            </span>
        </div>
      </div>
      </template>

      <template v-slot:item.created="{ item }">
        {{ new Date(item.created).toLocaleDateString("de-DE") }}
      </template>

      <template v-slot:item.roles="{ item }">
        <div v-if="!colapse && item.roles && item.roles.length > 0">
              <v-chip v-if="item.roles[0]">
                {{ item.roles[0] }}
              </v-chip>
              <span v-if="item.roles.length > 1" class="text-grey text-caption">
                (+{{ item.roles.length - 1 }} other<span v-if="item.roles.length > 2">s</span>)
              </span>
            </div>
            <div v-if="colapse && item.roles && item.roles.length > 0">
              <v-chip>{{ item.roles[0] }}<span v-if="item.roles.length > 1">, ...</span></v-chip>
            </div>
      </template>

      <template v-slot:item.actions="{ item }">
        <div class="align-horizonally">
          <v-icon @click="editUser(item)" class="mr-2">mdi-pencil</v-icon>
          <v-icon @click="deleteUser(item)">mdi-delete</v-icon>
        </div>
      </template>
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
    }
  },
  computed: {
    dynamicHeaders() {
      let headers = [
        { text: 'User', value: 'fullname'},
        ...(!this.isSmallScreen ? [{ text: 'Created', value: 'created' }] : []),
        { text: 'Roles', value: 'roles' },
        { text: '', value: 'actions', sortable: false, width: '30px' },
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
  }
}
</script>

<style>
  .container {
    margin: 20px;
  }
  .align-horizonally {
    display: flex;
    align-items: center; 
  }

  .username {
    font-weight: bold;
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
    max-width: 70px;
  }
</style>
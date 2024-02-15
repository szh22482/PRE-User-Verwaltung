<template>
  <v-container fluid class="ma-1">
    <v-data-table 
      class="rounded-lg"
      :style="{ 'border': 'solid 1px #e0e0e0' }"
      :items="users"
      :headers="dynamicHeaders"
    >
      <template v-slot:headers>
        <tr :style="{ 'backgroung-color': '#f9fafc' }">
          <th>User</th>
          <th v-if="!isSmallScreen">Created</th>
          <th v-if="!colapseRole">Roles</th>
          <th></th>
        </tr>
      </template>

      <template v-slot:item.fullname="{ item }">
        <v-dialog>
          <template v-slot:activator="{ props }">
            <div class="align-horizonally" v-bind="props">
              <profile-picture
                :firstname="item.firstname"
                :lastname="item.lastname"
                :colorNr="item.colorNumber"
              />
              <div>
                <span class="username collapsible-text">
                  {{ item.firstname }} {{ item.lastname }}
                </span>
                <span class="collapsible-text email">
                  {{ item.email }}
                </span>
              </div>
            </div>
          </template>

          <template v-slot:default="{ isActive }">
            <v-container class="d-flex justify-center align-center">
              <v-card width="800" class="rounded-lg">
                <v-card-text>
                  <v-row justify="space-between pt-1">
                    <v-col cols="7">
                      <span 
                        class="username text-truncate" 
                        :style="{ 'padding-left': '20px' }">
                        {{ item.firstname }} {{ item.lastname }}
                      </span>
                    </v-col>

                    <v-col class="text-right" cols="5">
                      <v-icon color="grey-darken-4">
                        mdi-pencil
                      </v-icon>
                      <v-icon color="red-lighten-1" :style="{ 'margin-inline': '10px' }">
                        mdi-delete
                      </v-icon>
                    </v-col>
                  </v-row>

                  <v-divider class="my-4"></v-divider>

                  <v-row>
                      <v-col class="user-label">Firstname</v-col>
                      <v-col class="user-info"> {{ item.firstname }} </v-col>
                  </v-row>
                  <v-row>
                      <v-col class="user-label">Lastname </v-col>
                      <v-col class="user-info"> {{ item.lastname }} </v-col>
                  </v-row>
                  <v-row>
                      <v-col class="user-label">Email </v-col>
                      <v-col class="user-info text-truncate"> {{ item.email }} </v-col>
                  </v-row>
                  <v-row>
                      <v-col class="user-label">Created </v-col>
                      <v-col class="user-info"> 
                        {{ new Date(item.created).toLocaleDateString("de-DE") }}
                      </v-col>
                  </v-row>
                  <v-row>
                      <v-col class="user-label">Password </v-col>
                      <v-col class="user-info"> {{ item.password }} </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label"> Roles </v-col>
                      <v-col>
                        <v-chip 
                        class="role" 
                        v-for="(role, index) in item.roles" 
                        :key="index"
                        :style="roleStyle(role)"
                        >
                          {{ role }}
                        </v-chip>
                      </v-col>
                  </v-row>
                </v-card-text>

              <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                      text="Close"
                      @click="isActive.value = false"
                    ></v-btn>
                  </v-card-actions>
              </v-card>
          </v-container>
          </template>
        </v-dialog>
      </template>

      <template v-slot:item.created="{ item }">
        {{ new Date(item.created).toLocaleDateString("de-DE") }}
      </template>

      <template v-slot:item.roles="{ item }">
          <v-dialog>
            <template v-slot:activator="{ props }">
              <div v-if="!colapseDate && item.roles && item.roles.length > 0">
                <v-chip 
                  class="role" 
                  v-for="(role, index) in item.roles" 
                  :key="index"
                  :style="roleStyle(role)">
                    {{ role }}
                </v-chip>
              </div>
              <div v-else>
                <v-chip
                  class="role"
                  :style="roleStyle(item.roles[0])"
                  v-bind="props"
                  v-if="!colapseRole"
                >
                {{ item.roles[0] }}&nbsp;
                <span v-if="item.roles.length > 1">(+{{ item.roles.length - 1 }})</span>
                </v-chip>
              </div>
            </template>

            <template v-slot:default="{ isActive }">
              <v-card class="rounded-lg">
                <v-card-text>
                  <span class="username">
                    {{ item.firstname }} {{ item.lastname }} <br>
                  </span>
                  <span class="email">
                    {{ item.email }}
                  </span>

                  <v-divider class="my-4"></v-divider>

                  <v-chip 
                  class="role" 
                  v-for="(role, index) in item.roles" 
                  :key="index"
                  :style="roleStyle(role)"
                  >
                    {{ role }}
                  </v-chip>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                    text="Close"
                    @click="isActive.value = false"
                  ></v-btn>
                </v-card-actions>
              </v-card>
            </template>
          </v-dialog> 
      </template>

      <template v-slot:item.actions="{ item }">
        <v-menu location="start">
          <template v-slot:activator="{ props }">
            <v-icon v-bind="props">mdi-dots-vertical</v-icon>
          </template>

          <v-card class="rounded-lg">
            <v-btn variant="text" prepend-icon="mdi-pencil" block class="edit-button">
              Edit user
            </v-btn>
            <v-divider></v-divider>
              <v-btn variant="text" prepend-icon="mdi-delete" block>
                Delete user
              </v-btn>
          </v-card>
        </v-menu>
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
      colapseDate: false,
      colapseRole: false,
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
        ...(!this.colapseRole ? [{ text: 'Roles', value: 'roles', sortable: false}] : []),
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
        console.log(this.users)
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
      this.colapseDate = window.innerWidth < 750;
      this.colapseRole = window.innerWidth < 560;
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
  .role {
    margin-right: 5px;
    margin-block: 5px;
  }

  .align-horizonally {
    display: flex;
    align-items: center; 
    cursor: pointer;
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
    max-width: 400px;
  }

  .user-info {
    font-weight: bold;
  }

  .user-label {
    font-weight: bold;
    color: #616161;
    margin-left: 20px;
  }

  .edit-button {
    display: flex;
    justify-content: start !important;
  }
</style>
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
                    <v-col class="user-info"> {{ item.firstname }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Lastname</v-col>
                    <v-col class="user-info"> {{ item.lastname }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Email</v-col>
                    <v-col class="user-info text-truncate"> {{ item.email }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Created</v-col>
                    <v-col class="user-info">
                      {{ new Date(item.created).toLocaleDateString("de-DE") }}
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Password</v-col>
                    <v-col class="user-info"> {{ item.password }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label"> Roles</v-col>
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

            <v-dialog v-model="editDialog" max-width="700px">
              <v-card>
                <v-card-title>Edit User</v-card-title>
                <v-card-text>
                  <v-text-field v-model="item.firstname" label="Firstname" :counter="50" required variant="outlined"/>
                  <v-text-field v-model="item.lastname" label="Lastname" :counter="100" required variant="outlined"/>
                  <v-text-field v-model="item.email" label="Email" :counter="150" required variant="outlined"/>
                  <v-select v-model="item.roles.role_name" :item="item.roles" item-text="role_name"
                            item-value="id" label="Role" variant="outlined" required/>
                  <v-text-field v-model="item.password" label="Password" :counter="150" required variant="outlined"/>
                </v-card-text>
                <v-card-actions>
                  <div class="d-flex w-100">
                    <v-btn color="red" @click="cancelEdit" variant="tonal">Cancel</v-btn>
                    <v-btn color="green" @click="saveEditedUser(item)" variant="tonal" class="flex-grow-1">Save</v-btn>
                  </div>
                </v-card-actions>
              </v-card>
            </v-dialog>

            <v-dialog
              v-model="createDialog"
              width="700px"
            >
              <v-card>
                <v-card-title>Create a new User</v-card-title>
                <v-card-text>
                  <v-text-field v-model="editedUser.firstname" :rules="nameRules"
                                :counter="15" label="Firstname" required variant="outlined"/>
                  <v-text-field v-model="editedUser.lastname" :rules="nameRules"
                                :counter="150" label="Lastname" required variant="outlined"/>
                  <v-text-field v-model="editedUser.email" :rules="mailRules"
                                :counter="150" label="Email" required variant="outlined"/>
                  <v-select v-model="editedUser.roles" :items="user.roles" item-title="author_name" item-value="id" label="Role"
                            variant="outlined"
                            required/>
                  <v-text-field v-model="editedUser.password" :rules="passwordRules"
                                :counter="150" label="Password" required variant="outlined"/>
                </v-card-text>

                <v-card-actions>
                  <div class="d-flex w-100">
                    <v-btn color="red" variant="tonal" @click="$emit('cancel', null)">Cancel</v-btn>
                    <v-btn color="green" variant="tonal" @click="createUser" class="flex-grow-1">Create</v-btn>
                  </div>
                </v-card-actions>
              </v-card>
              <User @cancel="createDialog = false"/>
            </v-dialog>
          </template>

          <v-card class="rounded-lg">
            <v-btn variant="text" prepend-icon="mdi-pencil" block class="edit-button" @click="editDialog = !editDialog">
              Edit user
            </v-btn>
            <v-divider></v-divider>
            <v-btn variant="text" prepend-icon="mdi-delete" block>
              Delete user
            </v-btn>
          </v-card>

        </v-menu>
      </template>

      <template v-slot:bottom>
      </template> <!-- removes the default footer -->
    </v-data-table>
    <v-btn variant="tonal" color="green" icon="mdi-plus" size="small" class="create-button" @click="createDialog = !createDialog"></v-btn>

  </v-container>

  <v-snackbar
    v-model="snackbar.show"
    :timeout="1800"
  >
    {{ snackbar.message }}

    <template v-slot:actions>
      <v-btn
        color="green"
        variant="text"
        @click="snackbar.show = false"
      >
        Close
      </v-btn>
    </template>
  </v-snackbar>
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
      snackbar: {
        show: false,
        message: ''
      },
      createDialog: false,
      editDialog: false,
      deleteDialog: false,
      editedUser: {
        firstname: '',
        lastname: '',
        email: '',
        roles: undefined,
        password: null
      },
      //showCreateUser: false,
      roleColors: {
        Administrator: {background: '#e2ecf7', color: '#1c6ac1'},
        Auditor: {background: '#e8f5e9', color: '#5fb762'},
        Auditee: {background: '#dff6f9', color: '#00bcd4'},
        Reporter: {background: '#fde7e5', color: '#f44336'},
        Gast: {background: '#fff2df', color: '#ff9800'},
        'Manual writer': {background: '#d1c4e9', color: '#7e57c2'}
      }
    }
  },
  computed: {
    dynamicHeaders() {
      let headers = [
        {text: 'User', value: 'fullname', sortable: true},
        ...(!this.isSmallScreen ? [{text: 'Created', value: 'created', sortable: true}] : []),
        ...(!this.colapseRole ? [{text: 'Roles', value: 'roles', sortable: false}] : []),
        {text: '', value: 'actions', width: '30px', sortable: false},
      ];
      return headers;
    },
  },
  async mounted() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize)
    try {
      const response = await axios.get("/users/all");
      if (response != null) {
        this.users = response.data;
        console.log(this.users)
      } else {
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
      const defaultStyle = {background: '#eeeeee', color: '#000000'};
      return this.roleColors[role] || defaultStyle;
    },
    saveEditedUser(item) {
      console.log(item)
      axios.patch(`/users/${item.id}`, {
        firstname: this.firstname,
        lastname: this.editedUser.lastname,
        email: this.editedUser.email,
        roles: this.editedUser.roles,
        password: this.editedUser.password,
      })
        .then(response => {
          // Update the user in the users array
          const index = this.users.findIndex(user => user.id === this.id); // this.editedUser.id
          if (index !== -1) {
            this.users.splice(index, 1, response.data);
          }
          this.editDialog = false;
          console.log('User edited:', response.data);

          this.snackbar.message = 'Successfully edited User';
          this.snackbar.show = true;
          this.user_sel = null;
        })
        .catch(error => {
          console.error('Error editing User:', error);
        });
    },
    cancelEdit() { // not sure
      this.editDialog = false;
      // Reset
      this.editedUser.firstname = '';
      this.editedUser.lastname = null;
      this.editedUser.email = null;
      this.editedUser.role = '';
      this.editedUser.password = null;
    }
  },
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

.create-button {
  position: fixed;
  top: 300px;
  left: 100px;
}
</style>

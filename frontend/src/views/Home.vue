<template>
  <v-container>
    <v-flex>
      <v-row class="mb-4 mt-4" justify="start" min-width="300"  >
        <v-col cols="12" lg="9" md="8" xs="12"  class="d-flex justify-start">
          <v-text-field
            v-model="search"
            label="Search users"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            class="search-bar me-3"
            style="min-width: 145px !important;"
          ></v-text-field>

          <v-select
            v-model="selectedRoles"
            :items="availableRoles"
            variant="outlined"
            label="Filter"
            multiple
            density="compact"
            style="min-width: 155px; max-width: 240px; margin-top: -20px;"
            @change="filterUsers"
            clearable
            >
            <template v-slot:selection="{ item, index }">
                <v-chip v-if="index < 1" :style="roleStyle(item)">
                <span>{{ item.title }}</span>
                </v-chip>
                <span v-if="index === 1" class="text-grey text-caption align-self-center">
                (+{{ selectedRoles.length - 1 }})
                </span>
            </template>
          </v-select>
        </v-col>

        <v-col cols="12" lg="3" md="4" xs="12" class="d-flex justify-end">
          <v-btn 
            variant="plain"
            ripple="false"
            class="setting-button text-none"
            @click="$router.push('/add')"
          >Add new user</v-btn>
          <v-btn 
            color="black"
            ripple="false"
            size="large"
            class="setting-button text-none rounded-lg"
            @click="$router.push('/invite')"
          >Invite User</v-btn>
        </v-col>
      </v-row>
    </v-flex>
    <v-data-table
      class="rounded-lg"
      :items="filteredUsers"
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

      <template v-slot:item.fullname="{ item, index }">
        <v-dialog>
          <template v-slot:activator="{ props }">
            <div class="align-horizontally" v-bind="props">
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
                  <v-row justify="space-between" pt="1">
                    <v-col cols="7">
                      <span
                        class="username text-truncate"
                        :style="{ 'padding-left': '20px' }">
                        {{ item.firstname }} {{ item.lastname }}
                      </span>
                    </v-col>

                    <v-col class="text-right" cols="5">
                      <v-icon color="grey-darken-4" @click="openInnerDialog(index)">
                        mdi-pencil
                      </v-icon>
                      <v-icon color="red-lighten-1" :style="{ 'margin-inline': '10px' }"
                      @click="openDeleteDialog(index)">
                        mdi-delete
                      </v-icon>
                      <v-dialog width="300" v-model="dialogs[index].showDeleteDialog">
                        <Confirmation :item="item" :index="index"
                        @delete="deleteUser(index)" @cancel="cancelDelete(index)"/>
                      </v-dialog>
                      <v-dialog width="700" v-model="dialogs[index].showInnerDialog">
                        <EditDialog :item="item" :index="index" :email="item.email" @cancel="cancelEdit(index)"/>
                      </v-dialog>
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

      <template v-slot:item.actions="{ item, index }">
              <v-menu
                    v-model="dialogs[index].showOuterDialog"
                    :close-on-content-click="false"
                    location="end"
                  >
                    <template v-slot:activator="{ props} ">
                      <button
                        class="btn-more"
                        @click="openOutderDialog(index)"
                        v-bind="props">
                        <v-icon v-bind="props">mdi-dots-vertical</v-icon>
                      </button>
                    </template>

                    <v-card class="rounded-lg">
                      <v-btn variant="text" prepend-icon="mdi-pencil" block class="edit-button" @click="openInnerDialog(index)">
                        Edit user
                      </v-btn>
                      <v-divider></v-divider>
                      <v-btn variant="text" prepend-icon="mdi-delete" block @click="openDeleteDialog(index)">
                        Delete user
                      </v-btn>
                    </v-card>
                  </v-menu>
                  <v-dialog width="300" v-model="dialogs[index].showDeleteDialog">
                   <Confirmation :item="item" :index="index"
                   @delete="deleteUser(index)" @cancel="cancelDelete(index)"/>
                  </v-dialog>
                  <v-dialog width="700" v-model="dialogs[index].showInnerDialog">
                    <EditDialog :item="item" :index="index" :email="item.email" @cancel="cancelEdit(index)"/>
                  </v-dialog>
            </template>

      
    </v-data-table>
  </v-container>
</template>

<script>
import axios from "axios";
import NavBar from "@/components/NavBar.vue";
import ProfilePicture from "@/components/ProfilePicture.vue";
import EditDialog from "@/components/EditDialog.vue";
import Confirmation from "@/components/Confirmation.vue";

export default {
  components: {
    ProfilePicture,
    EditDialog,
    NavBar,
    Confirmation
  },
  data() {
    return {
      search: '',
      isSmallScreen: false,
      colapseDate: false,
      colapseRole: false,
      dialogs: [],
      users: [],
      showFilterDropdown: false,
      isDropdownOpen: false,
      availableRoles: ['Administrator', 'Auditor', 'Auditee', 'Reporter', 'Gast', 'Manual writer'],
      selectedRoles: [],
      filteredUsers: [],
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
        {text: 'User', value: 'fullname'},
        ...(!this.isSmallScreen ? [{text: 'Created', value: 'created'}] : []),
        ...(!this.colapseRole ? [{text: 'Roles', value: 'roles'}] : []),
        {text: '', value: 'actions', width: '30px'},
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
        this.filteredUsers = response.data;
        console.log(this.users)
        this.dialogs = this.users.map(() => ({ showOuterDialog: false, showInnerDialog: false, showDeleteDialog: false}));
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
  watch: {
    selectedRoles: {
      handler() {
        this.isDropdownOpen = true;
        this.filterUsers();
      },
      deep: true
    },
    search() {
      this.filterUsers();
    }
  },
  methods: {
    filterUsers() {
      this.filteredUsers = this.users.filter(user => {
        if (user.firstname == null || user.lastname == null || user.email == null) return false;
        const fullName = (user.firstname + ' ' + user.lastname).toLowerCase();
        const searchLowerCase = this.search.toLowerCase();
        return fullName.startsWith(searchLowerCase) ||
              user.firstname.toLowerCase().startsWith(searchLowerCase) ||
              user.lastname.toLowerCase().startsWith(searchLowerCase) ||
              user.email.toLowerCase().startsWith(searchLowerCase);
      });

      if (this.selectedRoles.length > 0) {
        this.filteredUsers = this.filteredUsers.filter(user => {
          return this.selectedRoles.every(role => user.roles.includes(role));
        });
      }
    },
    openOutderDialog(index) {
      this.dialogs[index].showOuterDialog = true;
    },
    openInnerDialog(index) {
      this.dialogs[index].showOuterDialog = false;
      this.dialogs[index].showInnerDialog = true;
    },
    openDeleteDialog(index) {
      this.dialogs[index].showOuterDialog = false;
      this.dialogs[index].showDeleteDialog = true;
    },
    closeDeleteDialog(index) {
      this.dialogs[index].showDeleteDialog = false;
    },
    closeInnerDialog(index) {
      this.dialogs[index].showInnerDialog = false;
    },
    checkScreenSize() {
      this.isSmallScreen = window.innerWidth < 960;
      this.colapseDate = window.innerWidth < 750;
      this.colapseRole = window.innerWidth < 560;
    },
    roleStyle(role) {
      const defaultStyle = {background: '#eeeeee', color: '#000000'};
      return this.roleColors[role] || this.roleColors[role.title] || defaultStyle;
    },
    cancelEdit(index) {
      this.dialogs[index].showInnerDialog = false;
    },
    cancelDelete(index) {
      this.dialogs[index].showDeleteDialog = false;
    },
    deleteUser(index){
      this.users.splice(index, 1);
      this.dialogs.splice(index, 1);
      this.closeDeleteDialog(index);
    },
  },
}
</script>

<style>
.role {
  margin-right: 5px;
  margin-block: 5px;
}

.setting-button {
  height: 40px !important;
  margin-top: -20px !important;
}

.search-bar {
  min-width: 300px !important;
  margin-top: -20px !important;
}

.align-horizontally {
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

.add-user-button {
  position: fixed;
  bottom: 35px;
  right: 25px;
}
</style>
